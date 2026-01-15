package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

/**
 * Reusable Text component with ViewData styling
 */
@Composable
fun AppText(
    viewData: TextViewData,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = viewData.text,
        modifier = modifier,
        style = viewData.style ?: LocalTextStyle.current,
        color = viewData.color ?: LocalTextStyle.current.color,
        fontSize = viewData.fontSize ?: viewData.style?.fontSize ?: LocalTextStyle.current.fontSize,
        fontWeight = viewData.fontWeight,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = viewData.maxLines ?: Int.MAX_VALUE
    )
}

/**
 * Convenience function for simple text
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    viewData: TextViewData = TextViewData.default(text),
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip
) {
    AppText(
        viewData = viewData.copy(text = text),
        modifier = modifier,
        textAlign = textAlign,
        overflow = overflow
    )
}