package com.karaokelyrics.app.presentation.ui.utils

import androidx.compose.animation.core.Easing
import com.karaokelyrics.app.domain.util.AnimationEasing

/**
 * Presentation layer wrapper for easing functions
 * Delegates to domain layer for actual calculations
 */
class NewtonPolynomialInterpolationEasing(points: List<Pair<Double, Double>>): Easing {
    constructor(vararg points: Pair<Double, Double>) : this(points.toList())

    private val domainEasing = AnimationEasing.NewtonPolynomialInterpolation(points)

    override fun transform(fraction: Float): Float {
        return domainEasing.transform(fraction)
    }
}

val DipAndRise = DipAndRise(0.5, 1.0)

fun DipAndRise(
    dip: Double = 0.5,
    rise: Double = 1.0
): NewtonPolynomialInterpolationEasing {
    return NewtonPolynomialInterpolationEasing(
        0.0 to 0.0,
        0.5 to -dip,
        1.0 to rise
    )
}

val Swell = Swell(0.1)

fun Swell(
    swell: Double = 0.1,
): NewtonPolynomialInterpolationEasing {
    return NewtonPolynomialInterpolationEasing(
        0.0 to 0.0,
        0.5 to swell,
        1.0 to 0.0
    )
}

val Bounce = NewtonPolynomialInterpolationEasing(
    0.0 to 0.0,
    0.7 to 1.0,
    1.0 to 0.0
)