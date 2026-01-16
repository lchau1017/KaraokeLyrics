package com.karaokelyrics.app.data.parser

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import javax.inject.Inject

/**
 * Handles XML parsing for TTML format.
 * Single Responsibility: Only handles XML structure parsing.
 */
class TtmlXmlParser @Inject constructor(
    private val timeParser: TimeFormatParser
) : LyricsParser {

    override suspend fun parse(content: String): ParsedLyricsData {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(content.reader())

        val lines = mutableListOf<ParsedLine>()

        try {
            navigateToBody(parser)
            parseBody(parser, lines)
        } catch (e: Exception) {
            throw ParseException("Failed to parse TTML content", e)
        }

        return ParsedLyricsData(lines.sortedBy { it.startMs })
    }

    private fun navigateToBody(parser: XmlPullParser) {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "body") {
                return
            }
            eventType = parser.next()
        }
        throw ParseException("No body element found in TTML")
    }

    private fun parseBody(parser: XmlPullParser, lines: MutableList<ParsedLine>) {
        var eventType = parser.next()

        while (!(eventType == XmlPullParser.END_TAG && parser.name == "body")) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "div" -> parseDiv(parser, lines)
                }
            }
            eventType = parser.next()
        }
    }

    private fun parseDiv(parser: XmlPullParser, lines: MutableList<ParsedLine>) {
        val agent = parser.getAttributeValue(null, "agent")
        val isBackgroundVocal = agent?.contains("v") == true

        var eventType = parser.next()
        while (!(eventType == XmlPullParser.END_TAG && parser.name == "div")) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "p") {
                parseParagraph(parser, lines, isBackgroundVocal)
            }
            eventType = parser.next()
        }
    }

    private fun parseParagraph(
        parser: XmlPullParser,
        lines: MutableList<ParsedLine>,
        isBackgroundVocal: Boolean
    ) {
        val begin = parser.getAttributeValue(null, "begin")
        val end = parser.getAttributeValue(null, "end")

        if (begin == null || end == null) {
            // Skip paragraphs without timing
            skipElement(parser, "p")
            return
        }

        val lineStartMs = timeParser.parseTimeExpression(begin)
        val lineEndMs = timeParser.parseTimeExpression(end)
        val syllables = mutableListOf<ParsedSyllable>()

        var eventType = parser.next()
        while (!(eventType == XmlPullParser.END_TAG && parser.name == "p")) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name == "span") {
                        parseSpan(parser, syllables)
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text.trim()
                    if (text.isNotEmpty()) {
                        // Text without timing uses line timing
                        syllables.add(
                            ParsedSyllable(
                                content = text,
                                startMs = lineStartMs,
                                endMs = lineEndMs
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }

        if (syllables.isNotEmpty()) {
            lines.add(
                ParsedLine(
                    syllables = syllables,
                    startMs = lineStartMs,
                    endMs = lineEndMs,
                    isBackgroundVocal = isBackgroundVocal
                )
            )
        }
    }

    private fun parseSpan(parser: XmlPullParser, syllables: MutableList<ParsedSyllable>) {
        val begin = parser.getAttributeValue(null, "begin")
        val end = parser.getAttributeValue(null, "end")

        if (begin == null || end == null) {
            // Skip spans without timing
            skipElement(parser, "span")
            return
        }

        val startMs = timeParser.parseTimeExpression(begin)
        val endMs = timeParser.parseTimeExpression(end)

        // Get text content
        var text = ""
        var eventType = parser.next()
        while (!(eventType == XmlPullParser.END_TAG && parser.name == "span")) {
            if (eventType == XmlPullParser.TEXT) {
                text += parser.text
            }
            eventType = parser.next()
        }

        if (text.isNotEmpty()) {
            syllables.add(
                ParsedSyllable(
                    content = text,
                    startMs = startMs,
                    endMs = endMs
                )
            )
        }
    }

    private fun skipElement(parser: XmlPullParser, elementName: String) {
        var depth = 1
        var eventType = parser.next()
        while (depth > 0) {
            when (eventType) {
                XmlPullParser.START_TAG -> depth++
                XmlPullParser.END_TAG -> depth--
            }
            eventType = parser.next()
        }
    }
}