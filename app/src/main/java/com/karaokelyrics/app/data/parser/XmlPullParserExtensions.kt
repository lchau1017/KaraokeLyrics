package com.karaokelyrics.app.data.parser

import org.xmlpull.v1.XmlPullParser

/**
 * Extension functions for [XmlPullParser] to simplify XML navigation and content extraction.
 */

/**
 * Navigates forward through the document until finding a start tag with the given name.
 *
 * @param tag The tag name to find
 * @return This parser if the tag was found, null if end of document was reached
 */
internal fun XmlPullParser.navigateTo(tag: String): XmlPullParser? {
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && name == tag) return this
        next()
    }
    return null
}

/**
 * Iterates over direct child elements of the current element.
 * Handles nested elements with the same name as the parent correctly.
 *
 * @param parent The name of the parent element (must match current element)
 * @param action Called for each child start tag with the tag name
 */
internal inline fun XmlPullParser.forEachChild(parent: String, action: (String) -> Unit) {
    var depth = 1
    while (depth > 0 && next() != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                if (name == parent) depth++ else action(name)
            }
            XmlPullParser.END_TAG -> {
                if (name == parent) depth--
            }
        }
    }
}

/**
 * Reads all text content within the current element until its closing tag.
 * Skips over any nested elements.
 *
 * @param tag The name of the current element
 * @return The concatenated text content
 */
internal fun XmlPullParser.readText(tag: String): String {
    val sb = StringBuilder()
    while (next() != XmlPullParser.END_DOCUMENT && !(eventType == XmlPullParser.END_TAG && name == tag)) {
        if (eventType == XmlPullParser.TEXT) {
            sb.append(text)
        } else if (eventType == XmlPullParser.START_TAG) {
            skipTo(name)
        }
    }
    return sb.toString()
}

/**
 * Skips forward to the closing tag of the current element.
 * Handles nested elements with the same name correctly.
 *
 * @param tag The name of the element to skip past
 */
internal fun XmlPullParser.skipTo(tag: String) {
    var depth = 1
    while (depth > 0 && next() != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && name == tag) {
            depth++
        } else if (eventType == XmlPullParser.END_TAG && name == tag) {
            depth--
        }
    }
}
