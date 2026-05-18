package com.sujoy.flippy.database

import androidx.room.Entity
import com.sujoy.flippy.core.ConstantsManager

@Entity(
    tableName = ConstantsManager.TABLE_NAME_BADGES,
    primaryKeys = ["badgeId", "userId"]
)
data class BadgeEntity(
    val badgeId: String = "",
    val userId: String = "",
    val unlockTimestamp: Long = 0,
    val isBackedUp: Boolean = false
)

fun BadgeEntity.toMap(): Map<String, Any?> {
    return mapOf(
        "badgeId" to badgeId,
        "userId" to userId,
        "unlockTimestamp" to unlockTimestamp,
        "isBackedUp" to isBackedUp
    )
}
