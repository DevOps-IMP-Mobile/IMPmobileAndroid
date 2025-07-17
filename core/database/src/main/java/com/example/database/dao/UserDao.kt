package com.example.database.dao

import androidx.room.*
import com.example.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE token IS NOT NULL")
    suspend fun getLoggedInUser(): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET token = :token, lastLoginAt = :lastLoginAt WHERE id = :userId")
    suspend fun updateUserToken(userId: String, token: String?, lastLoginAt: Long)

    @Query("UPDATE users SET token = NULL WHERE id = :userId")
    suspend fun clearUserToken(userId: String)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}