package com.karaokelyrics.app.data.parser

import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.KyricsLine
import com.karaokelyrics.app.domain.model.KyricsSyllable
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.parser.TtmlParser
import javax.inject.Inject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class TtmlParserImpl @Inject constructor() : TtmlParser {

    override fun parse(lines: List<String>): SyncedLyrics {
        val content = lines.joinToString("\n")
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(content.reader())

        val lyricsLines = mutableListOf<ISyncedLine>()

        try {
            var eventType = parser.eventType

            // Navigate to body element
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "body") {
                    parseBody(parser, lyricsLines)
                    break
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            // Error parsing TTML
        }

        // Sort by start time
        return SyncedLyrics(lyricsLines.sortedBy { (it as? KyricsLine)?.start ?: 0 })
    }

    private fun parseBody(parser: XmlPullParser, lyricsLines: MutableList<ISyncedLine>) {
        var eventType = parser.next()

        while (!(eventType == XmlPullParser.END_TAG && parser.name == "body")) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "div" -> parseDiv(parser, lyricsLines)
                }
            }
            eventType = parser.next()
            if (eventType == XmlPullParser.END_DOCUMENT) break
        }
    }

    private fun parseDiv(parser: XmlPullParser, lyricsLines: MutableList<ISyncedLine>) {
        var eventType = parser.next()

        while (!(eventType == XmlPullParser.END_TAG && parser.name == "div")) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "p" -> parseP(parser, lyricsLines)
                }
            }
            eventType = parser.next()
            if (eventType == XmlPullParser.END_DOCUMENT) break
        }
    }

    private fun parseP(parser: XmlPullParser, lyricsLines: MutableList<ISyncedLine>) {
        val pBegin = parser.getAttributeValue(null, "begin")
        val pEnd = parser.getAttributeValue(null, "end")
        val agent = parser.getAttributeValue("http://www.w3.org/ns/ttml#metadata", "agent")

        if (pBegin == null || pEnd == null) {
            // Skip to end of this p element
            skipToEndTag(parser, "p")
            return
        }

        val mainSyllables = mutableListOf<KyricsSyllable>()
        val bgSyllables = mutableListOf<KyricsSyllable>()
        var bgStart: Int? = null
        var bgEnd: Int? = null

        var eventType = parser.next()
        var inBgSpan = false

        while (!(eventType == XmlPullParser.END_TAG && parser.name == "p")) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "span") {
                val role = parser.getAttributeValue("http://www.w3.org/ns/ttml#metadata", "role")
                val spanBegin = parser.getAttributeValue(null, "begin")
                val spanEnd = parser.getAttributeValue(null, "end")

                if (role == "x-bg") {
                    // This is a background vocal container
                    inBgSpan = true
                    if (spanBegin != null) bgStart = parseTime(spanBegin)
                    if (spanEnd != null) bgEnd = parseTime(spanEnd)
                } else if (spanBegin != null && spanEnd != null) {
                    // Regular syllable span
                    val text = getElementText(parser, "span")
                    if (text.isNotEmpty()) {
                        val syllable = KyricsSyllable(
                            content = text,
                            start = parseTime(spanBegin),
                            end = parseTime(spanEnd)
                        )

                        if (inBgSpan) {
                            bgSyllables.add(syllable)
                        } else {
                            mainSyllables.add(syllable)
                        }
                    }
                } else {
                    // Skip this span
                    skipToEndTag(parser, "span")
                }
            } else if (eventType == XmlPullParser.END_TAG && parser.name == "span") {
                val role = parser.getAttributeValue("http://www.w3.org/ns/ttml#metadata", "role")
                if (role == "x-bg") {
                    inBgSpan = false
                }
            }

            eventType = parser.next()
            if (eventType == XmlPullParser.END_DOCUMENT) break
        }

        // Create main line
        if (mainSyllables.isNotEmpty()) {
            // Trim trailing space from last syllable
            val lastIndex = mainSyllables.lastIndex
            mainSyllables[lastIndex] = mainSyllables[lastIndex].copy(
                content = mainSyllables[lastIndex].content.trimEnd()
            )

            lyricsLines.add(
                KyricsLine(
                    syllables = mainSyllables,
                    start = parseTime(pBegin),
                    end = parseTime(pEnd),
                    metadata = mapOf("alignment" to "Center"),
                    isAccompaniment = false
                )
            )
        }

        // Create background vocal line if exists
        if (bgSyllables.isNotEmpty()) {
            // Trim trailing space from last syllable
            val lastIndex = bgSyllables.lastIndex
            bgSyllables[lastIndex] = bgSyllables[lastIndex].copy(
                content = bgSyllables[lastIndex].content.trimEnd()
            )

            lyricsLines.add(
                KyricsLine(
                    syllables = bgSyllables,
                    start = bgStart ?: bgSyllables.first().start,
                    end = bgEnd ?: bgSyllables.last().end,
                    metadata = mapOf("alignment" to "Center"),
                    isAccompaniment = true
                )
            )
        }
    }

    private fun getElementText(parser: XmlPullParser, tagName: String): String {
        val text = StringBuilder()
        var eventType = parser.next()

        while (!(eventType == XmlPullParser.END_TAG && parser.name == tagName)) {
            when (eventType) {
                XmlPullParser.TEXT -> {
                    text.append(parser.text)
                }
                XmlPullParser.START_TAG -> {
                    // Skip nested tags
                    skipToEndTag(parser, parser.name)
                }
            }
            eventType = parser.next()
            if (eventType == XmlPullParser.END_DOCUMENT) break
        }

        return text.toString()
    }

    private fun skipToEndTag(parser: XmlPullParser, tagName: String) {
        var depth = 1
        var eventType = parser.next()

        while (depth > 0 && eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == tagName) depth++
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == tagName) depth--
                }
            }
            if (depth > 0) {
                eventType = parser.next()
            }
        }
    }

    private fun parseTime(timeStr: String?): Int {
        if (timeStr == null) return 0

        return when {
            timeStr.endsWith("ms") -> {
                timeStr.removeSuffix("ms").toIntOrNull() ?: 0
            }
            timeStr.endsWith("s") -> {
                ((timeStr.removeSuffix("s").toDoubleOrNull() ?: 0.0) * 1000).toInt()
            }
            timeStr.contains(":") -> {
                // Format: MM:SS.mmm or HH:MM:SS.mmm
                val parts = timeStr.split(":")
                when (parts.size) {
                    2 -> {
                        // MM:SS.mmm format
                        val minutePart = parts[0].toIntOrNull() ?: 0
                        val secondParts = parts[1].split(".")
                        val seconds = secondParts[0].toIntOrNull() ?: 0
                        val millis = if (secondParts.size > 1) {
                            // Convert fractional seconds to milliseconds
                            val fractionStr = secondParts[1]
                            when (fractionStr.length) {
                                1 -> fractionStr.toIntOrNull()?.times(100) ?: 0 // .6 = 600ms
                                2 -> fractionStr.toIntOrNull()?.times(10) ?: 0 // .60 = 600ms
                                3 -> fractionStr.toIntOrNull() ?: 0 // .600 = 600ms
                                else -> fractionStr.take(3).toIntOrNull() ?: 0 // .6000 = 600ms
                            }
                        } else {
                            0
                        }
                        minutePart * 60000 + seconds * 1000 + millis
                    }
                    3 -> {
                        // HH:MM:SS.mmm format
                        val hours = parts[0].toIntOrNull() ?: 0
                        val minutes = parts[1].toIntOrNull() ?: 0
                        val secondParts = parts[2].split(".")
                        val seconds = secondParts[0].toIntOrNull() ?: 0
                        val millis = if (secondParts.size > 1) {
                            val fraction = secondParts[1].take(3).padEnd(3, '0')
                            fraction.toIntOrNull() ?: 0
                        } else {
                            0
                        }
                        hours * 3600000 + minutes * 60000 + seconds * 1000 + millis
                    }
                    else -> 0
                }
            }
            else -> timeStr.toIntOrNull() ?: 0
        }
    }
}
