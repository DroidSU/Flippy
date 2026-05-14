package com.sujoy.flippy.common

interface AnalyticsRepository {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    fun logScreenView(screenName: String)
}
