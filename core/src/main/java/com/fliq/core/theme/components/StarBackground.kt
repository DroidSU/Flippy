package com.fliq.core.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun StarBackground() {
    val configuration = LocalConfiguration.current
    val starCount = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
        val area = configuration.screenWidthDp * configuration.screenHeightDp
        (area / 2500).coerceIn(50, 200)
    }

    val stars = remember(starCount) {
        List(starCount) {
            val x = kotlin.random.Random.nextFloat()
            val y = kotlin.random.Random.nextFloat()
            val starSize = kotlin.random.Random.nextFloat() * 1.5f + 0.5f
            val alpha = kotlin.random.Random.nextFloat() * 0.3f + 0.2f
            Triple(x, y, starSize to alpha)
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { (x, y, data) ->
            val (starSize, alpha) = data
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = starSize.dp.toPx(),
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}
