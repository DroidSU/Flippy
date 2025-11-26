package com.sujoy.flippy.models

import kotlin.random.Random

// Represents the state of a single tile
data class GameTile(
    val id: Int,
    val isImage: Boolean = false,
    val value: Int = if (isImage) 0 else -Random.nextInt(1, 10), // Negative value for non-image tiles
    var isFlipped: Boolean = false
)