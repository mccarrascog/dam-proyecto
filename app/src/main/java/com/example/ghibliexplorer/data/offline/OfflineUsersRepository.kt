package com.example.ghibliexplorer.data.offline

import com.example.ghibliexplorer.data.User

interface OfflineUsersRepository {
    suspend fun insertUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User)
}

class LocalUserRepository(
    private val userDao: UserDao
) : OfflineUsersRepository {
    override suspend fun insertUser(user: User) = userDao.insertUser(user)
    override suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
