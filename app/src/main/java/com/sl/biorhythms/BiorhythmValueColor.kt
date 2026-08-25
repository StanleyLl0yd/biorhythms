package com.sl.biorhythms

import kotlin.math.roundToInt

object BiorhythmValueColor {
    private val negative = 0xFFFF3B30.toInt()
    private val zero = 0xFFFFCC00.toInt()
    private val positive = 0xFF34C759.toInt()

    fun argb(percent: Double): Int {
        val clamped = percent.coerceIn(-100.0, 100.0)
        return if (clamped <= 0.0) {
            blend(negative, zero, ((clamped + 100.0) / 100.0).toFloat())
        } else {
            blend(zero, positive, (clamped / 100.0).toFloat())
        }
    }

    private fun blend(start: Int, end: Int, fraction: Float): Int {
        val t = fraction.coerceIn(0f, 1f)
        fun channel(shift: Int): Int {
            val from = start ushr shift and 0xFF
            val to = end ushr shift and 0xFF
            return (from + (to - from) * t).roundToInt().coerceIn(0, 255)
        }

        return (channel(24) shl 24) or
            (channel(16) shl 16) or
            (channel(8) shl 8) or
            channel(0)
    }
}
