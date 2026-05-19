package com.fliq.game_engine.models

import androidx.compose.ui.geometry.Offset
import java.util.UUID

data class RippleState(
    val id: String = UUID.randomUUID().toString(),
    val position: Offset
)
