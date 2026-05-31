package com.fliq.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fliq.core.models.UserData

@Entity(tableName = "user_data")
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val avatarId: Int,
    val totalMatches: Int,
    val highestScore: Int,
    val longestRound: Long,
    val totalCorrectTaps: Int,
    val totalTaps: Int,
    val totalReflexTime: Long,
    val bestPerfectStreak: Int,
    val badges: List<String>,
    val latencyOffset: Long? = null,
    val xp: Int = 0,
    val coins: Int = 0,
    val currentWorld: Int = 1,
    val currentStage: Int = 1
)

fun UserEntity.toUserData() = UserData(
    userId = userId,
    username = username,
    avatarId = avatarId,
    totalMatches = totalMatches,
    highestScore = highestScore,
    longestRound = longestRound,
    totalCorrectTaps = totalCorrectTaps,
    totalTaps = totalTaps,
    totalReflexTime = totalReflexTime,
    bestPerfectStreak = bestPerfectStreak,
    badges = badges,
    latencyOffset = latencyOffset,
    xp = xp,
    coins = coins,
    currentWorld = currentWorld,
    currentStage = currentStage
)

fun UserData.toUserEntity() = UserEntity(
    userId = userId,
    username = username,
    avatarId = avatarId,
    totalMatches = totalMatches,
    highestScore = highestScore,
    longestRound = longestRound,
    totalCorrectTaps = totalCorrectTaps,
    totalTaps = totalTaps,
    totalReflexTime = totalReflexTime,
    bestPerfectStreak = bestPerfectStreak,
    badges = badges,
    latencyOffset = latencyOffset,
    xp = xp,
    coins = coins,
    currentWorld = currentWorld,
    currentStage = currentStage
)
