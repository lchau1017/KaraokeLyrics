package com.karaokelyrics.app.data.parser

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.parser.TtmlParser
import com.kyrics.kyricsLine
import com.kyrics.models.SyncedLine
import javax.inject.Inject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

/**
 * Parses TTML (Timed Text Markup Language) lyrics into [SyncedLyrics].
 *
 * Supports the following TTML features:
 * - Syllable-level timing via `<span begin="..." end="...">` elements
 * - Background/accompaniment vocals via `ttm:role="x-bg"` attribute
 * - Multiple time formats: milliseconds (100ms), seconds (1.5s), and clock time (00:01:30.500)
 *
 * Example TTML structure:
 * ```xml
 * <tt xmlns="http://www.w3.org/ns/ttml" xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
 *   <body>
 *     <div>
 *       <p begin="0ms" end="5000ms">
 *         <span begin="0ms" end="500ms">Hello </span>
 *         <span begin="500ms" end="1000ms">World</span>
 *         <span ttm:role="x-bg" begin="2000ms" end="3000ms">
 *           <span begin="2000ms" end="2500ms">(ooh)</span>
 *         </span>
 *       </p>
 *     </div>
 *   </body>
 * </tt>
 * ```
 */
class TtmlParserImpl @Inject constructor() : TtmlParser {

    override fun parse(lines: List<String>): SyncedLyrics {
        val parser = createParser(lines.joinToString("\n"))
        val lyricsLines = mutableListOf<SyncedLine>()

        try {
            parser.navigateTo("body")?.let { parseBody(it, lyricsLines) }
        } catch (_: Exception) {
            // Silently handle parsing errors
        }

        return SyncedLyrics(lyricsLines.sortedBy { it.start })
    }

    private fun createParser(content: String): XmlPullParser = XmlPullParserFactory.newInstance()
        .apply { isNamespaceAware = true }
        .newPullParser()
        .apply { setInput(content.reader()) }

    private fun parseBody(parser: XmlPullParser, lines: MutableList<SyncedLine>) {
        parser.forEachChild("body") { if (it == "div") parseDiv(parser, lines) }
    }

    private fun parseDiv(parser: XmlPullParser, lines: MutableList<SyncedLine>) {
        parser.forEachChild("div") { if (it == "p") parseParagraph(parser, lines) }
    }

    private fun parseParagraph(parser: XmlPullParser, lines: MutableList<SyncedLine>) {
        val timing = parser.getTiming() ?: return parser.skipTo("p")

        val main = mutableListOf<Syllable>()
        val bg = mutableListOf<Syllable>()
        var bgTiming: Timing? = null
        var inBg = false

        parser.forEachChild("p") { tag ->
            if (tag == "span") {
                when {
                    parser.isBackgroundVocal() -> {
                        inBg = true
                        bgTiming = parser.getTiming()
                    }
                    else -> parser.extractSyllable()?.let { if (inBg) bg.add(it) else main.add(it) }
                }
            }
        }

        main.toLine(timing.start, timing.end, isAccompaniment = false)?.let { lines.add(it) }
        val bgStart = bgTiming?.start ?: bg.firstOrNull()?.start ?: 0
        val bgEnd = bgTiming?.end ?: bg.lastOrNull()?.end ?: 0
        bg.toLine(bgStart, bgEnd, isAccompaniment = true)?.let { lines.add(it) }
    }

    private fun XmlPullParser.isBackgroundVocal() = getAttributeValue(TTML_NS, "role") == "x-bg"

    private fun XmlPullParser.getTiming(): Timing? {
        val b = getAttributeValue(null, "begin") ?: return null
        val e = getAttributeValue(null, "end") ?: return null
        return Timing(parseTime(b), parseTime(e))
    }

    private fun XmlPullParser.extractSyllable(): Syllable? {
        val timing = getTiming() ?: return skipTo("span").let { null }
        val text = readText("span")
        return if (text.isNotEmpty()) Syllable(text, timing.start, timing.end) else null
    }

    private fun List<Syllable>.toLine(start: Int, end: Int, isAccompaniment: Boolean): SyncedLine? {
        if (isEmpty()) return null
        return kyricsLine(start = start, end = end) {
            alignment("center")
            if (isAccompaniment) accompaniment()
            forEachIndexed { i, s ->
                syllable(if (i == lastIndex) s.text.trimEnd() else s.text, start = s.start, end = s.end)
            }
        }
    }

    companion object {
        private const val TTML_NS = "http://www.w3.org/ns/ttml#metadata"
    }
}

internal data class Timing(val start: Int, val end: Int)

internal data class Syllable(val text: String, val start: Int, val end: Int)
