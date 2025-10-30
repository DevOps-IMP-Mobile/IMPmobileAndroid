package com.example.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.database.dao.FcmTokenDao
import com.example.database.dao.UserDao
import com.example.database.entity.FcmTokenEntity
import com.example.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        FcmTokenEntity::class  // ✅ 추가
    ],
    version = 2,  // ✅ 버전 업
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fcmTokenDao(): FcmTokenDao
    companion object {
        const val DATABASE_NAME = "devops_app_database"
    }
}