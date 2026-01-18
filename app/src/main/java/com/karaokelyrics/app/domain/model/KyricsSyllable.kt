package com.karaokelyrics.app.domain.model

data class KyricsSyllable(val content: String, val start: Int, val end: Int) {
    fun progress(currentTimeMs: Int): Float {
        if (currentTimeMs <= start) return 0f
        if (currentTimeMs >= end) return 1f
        return (currentTimeMs - start).toFloat() / (end - start).coerceAtLeast(1)
    }
}
