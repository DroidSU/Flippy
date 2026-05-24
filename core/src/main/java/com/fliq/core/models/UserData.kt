package com.fliq.core.models

data class UserData(
    val userId: String = "",
    val username: String = "",
    val avatarId: Int = 1,
    val totalMatches: Int = 0,
    val highestScore: Int = 0,
    val longestRound: Long = 0L,
    val totalCorrectTaps: Int = 0,
    val totalTaps: Int = 0,
    val totalReflexTime: Long = 0,
    val bestPerfectStreak: Int = 0,
    val badges: List<String> = emptyList(),
    val baseReflex: Long? = null
)

fun UserData.toMap(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "username" to username,
        "avatarId" to avatarId,
        "totalMatches" to totalMatches,
        "highestScore" to highestScore,
        "longestRound" to longestRound,
        "totalCorrectTaps" to totalCorrectTaps,
        "totalTaps" to totalTaps,
        "totalReflexTime" to totalReflexTime,
        "bestPerfectStreak" to bestPerfectStreak,
        "badges" to badges,
        "baseReflex" to baseReflex
    )
}
