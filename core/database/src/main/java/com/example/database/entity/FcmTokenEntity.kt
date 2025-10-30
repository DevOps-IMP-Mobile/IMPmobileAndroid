package com.example.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fcm_tokens")
data class FcmTokenEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "fcm_token")
    val fcmToken: String,

    @ColumnInfo(name = "sp_uid")
    val spUid: String? = null,

    @ColumnInfo(name = "device_type")
    val deviceType: String = "ANDROID",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)