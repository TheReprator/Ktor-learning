package com.firstapp.crud

import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert

interface UserDatabaseRepository {
    fun getAllUser(): List<UserFetch>
    fun getUser(username: String): UserFetch
    fun deleteUser(username: String)
    fun addUser(user: UserInsert)
    fun updateUser(username: String, user: UserInsert)
}