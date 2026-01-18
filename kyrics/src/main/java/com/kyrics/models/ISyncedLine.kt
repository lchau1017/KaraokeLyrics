package com.kyrics.models

/**
 * Core interface for any synchronized line of content.
 * This is the fundamental abstraction that the library works with.
 */
interface ISyncedLine {
    /**
     * Start time in milliseconds
     */
    val start: Int

    /**
     * End time in milliseconds
     */
    val end: Int

    /**
     * Get the display content of this line
     */
    fun getContent(): String
}
