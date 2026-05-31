package com.fliq.core.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class FliqMotion(
    val fast: Int = 200,
    val medium: Int = 400,
    val slow: Int = 700,
    
    val standardSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    ),
    
    val tightSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    
    val quickTween: androidx.compose.animation.core.TweenSpec<Float> = tween(durationMillis = 200),
    val standardTween: androidx.compose.animation.core.TweenSpec<Float> = tween(durationMillis = 400),
    
    // Semantic motion
    val pressAnimation: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    val scoreAnimation: Int = 600,
    val rewardPulse: Int = 1200
)

val LocalFliqMotion = staticCompositionLocalOf { FliqMotion() }
