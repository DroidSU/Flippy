package com.sujoy.flippy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_data WHERE userId = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM user_data WHERE userId = :userId")
    suspend fun getUserSync(userId: String): UserEntity?

    @Query("DELETE FROM user_data")
    suspend fun clearAll()
}
