package com.karaokelyrics.app.presentation.shared.utils

object TextUtils {

fun String.isPunctuation(): Boolean {
    if (isEmpty()) return false
    return all { it in ".,;:!?\"'()[]{}…—–" || it.category == CharCategory.OTHER_PUNCTUATION }
}

fun String.isPureCjk(): Boolean {
    return all { char ->
        char.code in 0x4E00..0x9FFF || // CJK Unified Ideographs
        char.code in 0x3400..0x4DBF || // CJK Extension A
        char.code in 0x20000..0x2A6DF || // CJK Extension B
        char.code in 0x2A700..0x2B73F || // CJK Extension C
        char.code in 0x2B740..0x2B81F || // CJK Extension D
        char.code in 0x2B820..0x2CEAF || // CJK Extension E
        char.code in 0x2CEB0..0x2EBEF || // CJK Extension F
        char.code in 0x3000..0x303F || // CJK Symbols and Punctuation
        char.code in 0x3040..0x309F || // Hiragana
        char.code in 0x30A0..0x30FF || // Katakana
        char.code in 0x31F0..0x31FF || // Katakana Phonetic Extensions
        char.code in 0x3200..0x32FF || // Enclosed CJK Letters and Months
        char.code in 0x3300..0x33FF || // CJK Compatibility
        char.code in 0xF900..0xFAFF || // CJK Compatibility Ideographs
        char.code in 0xFE30..0xFE4F || // CJK Compatibility Forms
        char.code in 0x1F200..0x1F2FF // Enclosed Ideographic Supplement
    }
}

fun Char.isArabic(): Boolean {
    return code in 0x0600..0x06FF || // Arabic
           code in 0x0750..0x077F || // Arabic Supplement
           code in 0x08A0..0x08FF || // Arabic Extended-A
           code in 0xFB50..0xFDFF || // Arabic Presentation Forms-A
           code in 0xFE70..0xFEFF    // Arabic Presentation Forms-B
}

fun Char.isDevanagari(): Boolean {
    return code in 0x0900..0x097F || // Devanagari
           code in 0xA8E0..0xA8FF || // Devanagari Extended
           code in 0x1CD0..0x1CFF    // Vedic Extensions
}

fun String.isRtl(): Boolean {
    return any { it.isArabic() || it.isHebrew() }
}

fun Char.isHebrew(): Boolean {
    return code in 0x0590..0x05FF || // Hebrew
           code in 0xFB1D..0xFB4F    // Hebrew Presentation Forms
}

}