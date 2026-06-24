package com.ckpackage.custominfobar.main

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Sealed Interface of supported background types in [ComposeInfoBar].
 */
sealed interface CustomBackground {
    class SolidColor(val color: Color) : CustomBackground

    class GradientBrush(val gradientBrush: Brush) : CustomBackground

    class DrawableBackground(val image: Painter) : CustomBackground
}

/**
 * Extension method to convert [Color] to [CustomBackground].
 */
fun Color.toCustomBackground() = CustomBackground.SolidColor(this)

/**
 * Extension method to convert [Brush] to [CustomBackground].
 */
fun Brush.toCustomBackground() = CustomBackground.GradientBrush(this)

/**
 * Extension method to convert [Painter] to [CustomBackground].
 */
fun Painter.toCustomBackground() = CustomBackground.DrawableBackground(this)