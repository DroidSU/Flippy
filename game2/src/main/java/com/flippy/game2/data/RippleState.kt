package com.flippy.game2.data

import androidx.compose.animation.core.Animatable

class RippleState(val x: Float, val y: Float, val id: Long) {
    val animationProgress = Animatable(0f)
}