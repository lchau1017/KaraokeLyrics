package com.karaokelyrics.app.data.parser

/**
 * Parses a TTML time string into milliseconds.
 *
 * Supported formats:
 * - Milliseconds: "100ms" -> 100
 * - Seconds: "1.5s" -> 1500
 * - Clock time MM:SS.mmm: "01:30.500" -> 90500
 * - Clock time HH:MM:SS.mmm: "00:01:30.500" -> 90500
 * - Plain integer: "1000" -> 1000
 *
 * @param s The time string to parse
 * @return Time in milliseconds, or 0 if parsing fails
 */
internal fun parseTime(s: String): Int = when {
    s.endsWith("ms") -> s.removeSuffix("ms").toIntOrNull() ?: 0
    s.endsWith("s") -> ((s.removeSuffix("s").toDoubleOrNull() ?: 0.0) * 1000).toInt()
    ":" in s -> parseColonTime(s)
    else -> s.toIntOrNull() ?: 0
}

private fun parseColonTime(s: String): Int {
    val p = s.split(":")
    return when (p.size) {
        2 -> parseMmSs(p[0], p[1])
        3 -> parseHhMmSs(p[0], p[1], p[2])
        else -> 0
    }
}

private fun parseMmSs(m: String, ss: String): Int {
    val (sec, ms) = parseSecMs(ss)
    return (m.toIntOrNull() ?: 0) * 60_000 + sec * 1_000 + ms
}

private fun parseHhMmSs(h: String, m: String, ss: String): Int {
    val (sec, ms) = parseSecMs(ss)
    return (h.toIntOrNull() ?: 0) * 3_600_000 + (m.toIntOrNull() ?: 0) * 60_000 + sec * 1_000 + ms
}

private fun parseSecMs(ss: String): Pair<Int, Int> {
    val parts = ss.split(".")
    val sec = parts[0].toIntOrNull() ?: 0
    val ms = if (parts.size > 1) parseFraction(parts[1]) else 0
    return sec to ms
}

private fun parseFraction(f: String): Int = when (f.length) {
    1 -> f.toIntOrNull()?.times(100) ?: 0
    2 -> f.toIntOrNull()?.times(10) ?: 0
    else -> f.take(3).toIntOrNull() ?: 0
}
