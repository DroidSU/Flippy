package com.fliq.common

interface AnalyticsRepository {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    fun logScreenView(screenName: String)
}
