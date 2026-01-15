package com.karaokelyrics.app.domain.util

import kotlin.math.pow

/**
 * Animation easing algorithms
 * Domain business logic for animation interpolation
 */
object AnimationEasing {
    
    /**
     * Newton Polynomial Interpolation for custom easing curves
     */
    class NewtonPolynomialInterpolation(points: List<Pair<Double, Double>>) {
        constructor(vararg points: Pair<Double, Double>) : this(points.toList())

        private val dividedDifferences: List<Double>
        private val xValues: List<Double>

        init {
            require(points.map { it.first }.toSet().size == points.size) {
                "All x-coordinates of the points must be unique."
            }

            val n = points.size
            xValues = points.map { it.first }

            val table = Array(n) { DoubleArray(n) }

            for (i in 0 until n) {
                table[i][0] = points[i].second
            }

            for (j in 1 until n) {
                for (i in j until n) {
                    table[i][j] = (table[i][j - 1] - table[i - 1][j - 1]) / (xValues[i] - xValues[i - j])
                }
            }

            dividedDifferences = List(n) { i -> table[i][i] }
        }

        fun transform(fraction: Float): Float {
            val x = fraction.toDouble()
            val n = xValues.size - 1
            var result = dividedDifferences[n]

            // Use Horner's method for efficient calculation
            for (i in (n - 1) downTo 0) {
                result = result * (x - xValues[i]) + dividedDifferences[i]
            }
            return result.toFloat()
        }
    }

    /**
     * Predefined easing curves
     */
    fun dipAndRise(dip: Double = 0.5, rise: Double = 1.0): NewtonPolynomialInterpolation {
        return NewtonPolynomialInterpolation(
            0.0 to 0.0,
            0.5 to -dip,
            1.0 to rise
        )
    }

    fun swell(swellAmount: Double = 0.1): NewtonPolynomialInterpolation {
        return NewtonPolynomialInterpolation(
            0.0 to 0.0,
            0.5 to swellAmount,
            1.0 to 0.0
        )
    }

    val bounce = NewtonPolynomialInterpolation(
        0.0 to 0.0,
        0.7 to 1.0,
        1.0 to 0.0
    )

    /**
     * Standard easing functions
     */
    fun easeInCubic(t: Float): Float = t * t * t
    
    fun easeOutCubic(t: Float): Float = 1 - (1 - t).toDouble().pow(3.0).toFloat()
    
    fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4 * t * t * t
        } else {
            1 - (-2 * t + 2).toDouble().pow(3.0).toFloat() / 2
        }
    }
}