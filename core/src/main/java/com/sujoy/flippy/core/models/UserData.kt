package com.sujoy.flippy.core.models

data class UserData(
    val userId: String = "",
    val username: String = "",
    val avatarId: Int = 0
)

fun UserData.toMap(): Map<String, Any?> {
    return mapOf(
        "userId" to userId,
        "username" to username,
        "avatarId" to avatarId
    )
}
