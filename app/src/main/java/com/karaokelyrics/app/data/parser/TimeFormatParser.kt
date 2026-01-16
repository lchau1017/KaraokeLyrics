package com.karaokelyrics.app.data.parser

import javax.inject.Inject

/**
 * Handles parsing of various time formats used in lyrics files.
 * Single Responsibility: Only parses time expressions.
 */
class TimeFormatParser @Inject constructor() {

    /**
     * Parse a time expression into milliseconds.
     * Supports formats like "1.5s", "1500ms", "00:01:30.500"
     *
     * @param expression The time expression to parse
     * @return Time in milliseconds
     * @throws ParseException if the format is invalid
     */
    fun parseTimeExpression(expression: String): Int {
        return when {
            expression.endsWith("ms") -> parseMilliseconds(expression)
            expression.endsWith("s") -> parseSeconds(expression)
            expression.contains(":") -> parseTimecode(expression)
            expression.all { it.isDigit() } -> expression.toIntOrNull() ?: throwInvalidFormat(expression)
            else -> throwInvalidFormat(expression)
        }
    }

    private fun parseMilliseconds(expression: String): Int {
        val value = expression.dropLast(2).trim()
        return value.toIntOrNull() ?: throwInvalidFormat(expression)
    }

    private fun parseSeconds(expression: String): Int {
        val value = expression.dropLast(1).trim()
        val seconds = value.toFloatOrNull() ?: throwInvalidFormat(expression)
        return (seconds * 1000).toInt()
    }

    private fun parseTimecode(expression: String): Int {
        // Format: HH:MM:SS.mmm or MM:SS.mmm or SS.mmm
        val parts = expression.split(":", ".")

        return when (parts.size) {
            2 -> {
                // SS.mmm
                val seconds = parts[0].toIntOrNull() ?: throwInvalidFormat(expression)
                val millis = parts[1].padEnd(3, '0').take(3).toIntOrNull() ?: 0
                (seconds * 1000) + millis
            }
            3 -> {
                // MM:SS.mmm
                val minutes = parts[0].toIntOrNull() ?: throwInvalidFormat(expression)
                val seconds = parts[1].toIntOrNull() ?: throwInvalidFormat(expression)
                val millis = parts[2].padEnd(3, '0').take(3).toIntOrNull() ?: 0
                (minutes * 60000) + (seconds * 1000) + millis
            }
            4 -> {
                // HH:MM:SS.mmm
                val hours = parts[0].toIntOrNull() ?: throwInvalidFormat(expression)
                val minutes = parts[1].toIntOrNull() ?: throwInvalidFormat(expression)
                val seconds = parts[2].toIntOrNull() ?: throwInvalidFormat(expression)
                val millis = parts[3].padEnd(3, '0').take(3).toIntOrNull() ?: 0
                (hours * 3600000) + (minutes * 60000) + (seconds * 1000) + millis
            }
            else -> throwInvalidFormat(expression)
        }
    }

    private fun throwInvalidFormat(expression: String): Nothing {
        throw ParseException("Invalid time format: $expression")
    }
}