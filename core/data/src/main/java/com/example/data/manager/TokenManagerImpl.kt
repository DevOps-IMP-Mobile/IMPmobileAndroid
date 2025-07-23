package com.example.data.manager

import com.example.database.dao.UserDao
import com.example.domain.manager.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    private val userDao: UserDao
) : TokenManager {
    override suspend fun getToken(): String? {
        return userDao.getLoggedInUser()?.token
    }
    override suspend fun clearToken() {
        userDao.getLoggedInUser()?.let { userDao.clearUserToken(it.id) }
    }
} 