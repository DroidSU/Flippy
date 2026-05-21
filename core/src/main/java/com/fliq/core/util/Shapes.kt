package com.fliq.core.util

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

class ChamferedCornerShape(private val chamferSize: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val chamferPx = with(density) { chamferSize.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width - chamferPx, 0f)
            lineTo(size.width, chamferPx)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
