package com.karaokelyrics.ui.rendering.text

/**
 * Utility for detecting text direction (RTL/LTR) in karaoke content.
 */
object TextDirectionDetector {

    /**
     * Text direction enumeration.
     */
    enum class TextDirection {
        LTR, // Left to Right
        RTL, // Right to Left
        MIXED // Contains both
    }

    /**
     * Detect the predominant text direction of a string.
     *
     * @param text Text to analyze
     * @return Detected text direction
     */
    fun detectTextDirection(text: String): TextDirection {
        if (text.isEmpty()) return TextDirection.LTR

        var rtlCount = 0
        var ltrCount = 0

        text.forEach { char ->
            when (getCharacterDirection(char)) {
                CharDirection.RTL -> rtlCount++
                CharDirection.LTR -> ltrCount++
                CharDirection.NEUTRAL -> {} // Ignore neutral characters
            }
        }

        return when {
            rtlCount == 0 && ltrCount == 0 -> TextDirection.LTR // Default for all neutral
            rtlCount > 0 && ltrCount == 0 -> TextDirection.RTL
            ltrCount > 0 && rtlCount == 0 -> TextDirection.LTR
            rtlCount > ltrCount -> TextDirection.RTL
            else -> TextDirection.LTR
        }
    }

    /**
     * Check if text contains RTL characters.
     *
     * @param text Text to check
     * @return True if text contains any RTL characters
     */
    fun containsRtl(text: String): Boolean {
        return text.any { isRtlCharacter(it) }
    }

    /**
     * Check if text is purely RTL.
     *
     * @param text Text to check
     * @return True if text contains only RTL and neutral characters
     */
    fun isPureRtl(text: String): Boolean {
        return text.all { char ->
            isRtlCharacter(char) || isNeutralCharacter(char)
        }
    }

    /**
     * Check if text is purely LTR.
     *
     * @param text Text to check
     * @return True if text contains only LTR and neutral characters
     */
    fun isPureLtr(text: String): Boolean {
        return text.all { char ->
            isLtrCharacter(char) || isNeutralCharacter(char)
        }
    }

    private enum class CharDirection {
        RTL, LTR, NEUTRAL
    }

    private fun getCharacterDirection(char: Char): CharDirection {
        return when {
            isRtlCharacter(char) -> CharDirection.RTL
            isLtrCharacter(char) -> CharDirection.LTR
            else -> CharDirection.NEUTRAL
        }
    }

    private fun isRtlCharacter(char: Char): Boolean {
        val code = char.code
        return when (code) {
            // Arabic
            in 0x0600..0x06FF -> true
            in 0x0750..0x077F -> true
            in 0x08A0..0x08FF -> true
            in 0xFB50..0xFDFF -> true
            in 0xFE70..0xFEFF -> true

            // Hebrew
            in 0x0590..0x05FF -> true
            in 0xFB1D..0xFB4F -> true

            // Syriac
            in 0x0700..0x074F -> true

            // Thaana
            in 0x0780..0x07BF -> true

            // N'Ko
            in 0x07C0..0x07FF -> true

            // Samaritan
            in 0x0800..0x083F -> true

            // Mandaic
            in 0x0840..0x085F -> true

            else -> false
        }
    }

    private fun isLtrCharacter(char: Char): Boolean {
        val code = char.code
        return when (code) {
            // Basic Latin
            in 0x0041..0x005A -> true // A-Z
            in 0x0061..0x007A -> true // a-z

            // Latin Extended
            in 0x00C0..0x00FF -> true
            in 0x0100..0x017F -> true
            in 0x0180..0x024F -> true

            // Greek
            in 0x0370..0x03FF -> true

            // Cyrillic
            in 0x0400..0x04FF -> true

            // CJK (Chinese, Japanese, Korean)
            in 0x4E00..0x9FFF -> true // CJK Unified Ideographs
            in 0x3040..0x309F -> true // Hiragana
            in 0x30A0..0x30FF -> true // Katakana
            in 0xAC00..0xD7AF -> true // Hangul Syllables

            else -> false
        }
    }

    private fun isNeutralCharacter(char: Char): Boolean {
        val code = char.code
        return when (code) {
            // Numbers
            in 0x0030..0x0039 -> true

            // Punctuation and symbols
            in 0x0020..0x0040 -> true
            in 0x005B..0x0060 -> true
            in 0x007B..0x007E -> true

            // Whitespace
            0x0009, 0x000A, 0x000D -> true

            else -> false
        }
    }

    /**
     * Get alignment suggestion based on text direction.
     *
     * @param text Text to analyze
     * @param forceDirection Optional forced direction override
     * @return Suggested alignment (start, center, end)
     */
    fun getAlignmentSuggestion(
        text: String,
        forceDirection: TextDirection? = null
    ): String {
        val direction = forceDirection ?: detectTextDirection(text)

        return when (direction) {
            TextDirection.RTL -> "end"
            TextDirection.LTR -> "start"
            TextDirection.MIXED -> "center"
        }
    }
}