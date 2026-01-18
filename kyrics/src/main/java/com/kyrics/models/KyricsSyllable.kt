package com.kyrics.models

/**
 * Represents a single syllable in a karaoke line.
 * Each syllable has its own timing for synchronized highlighting.
 */
data class KyricsSyllable(val content: String, val start: Int, val end: Int)
