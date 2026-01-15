package com.karaokelyrics.app.presentation.ui.utils

import com.karaokelyrics.app.domain.util.TextAnalysisUtils

/**
 * Presentation layer text utilities
 * Delegates to domain layer for business logic
 */

fun String.isPunctuation(): Boolean = TextAnalysisUtils.isPunctuation(this)

fun String.isPureCjk(): Boolean = TextAnalysisUtils.isPureCjk(this)

fun Char.isArabic(): Boolean = TextAnalysisUtils.isArabic(this)

fun Char.isDevanagari(): Boolean = TextAnalysisUtils.isDevanagari(this)

fun String.isRtl(): Boolean = TextAnalysisUtils.isRtl(this)

fun Char.isHebrew(): Boolean = TextAnalysisUtils.isHebrew(this)