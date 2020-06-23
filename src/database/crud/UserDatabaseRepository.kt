package com.firstapp.crud

import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert

interface UserDatabaseRepository {
    suspend fun getAllUser(): List<UserFetch>
    suspend fun getUser(username: String): UserFetch?
    suspend fun deleteUser(username: String): Boolean
    suspend fun addUser(user: UserInsert):UserFetch?
    suspend fun updateUser(username: String, user: UserInsert): Boolean
}