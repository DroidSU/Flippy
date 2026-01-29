package com.sujoy.flippy.common.models

data class AppUiState(
    val isUserLoggedIn: Boolean = false,
    val userName: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
