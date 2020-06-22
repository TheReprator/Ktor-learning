package com.firstapp.crud

import com.firstapp.database.User
import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDatabase : UserDatabaseRepository {

    override fun getAllUser(): List<UserFetch> {
        val userFetchList: ArrayList<UserFetch> = arrayListOf()
        transaction {
            User.selectAll().map { userFetchList.add(UserFetch(it[User.username], it[User.id])) }
        }
        return userFetchList
    }

    override fun getUser(userName: String): UserFetch {
        return transaction {
            User.select {
                User.username.eq(userName)
            }.map {
                UserFetch(it[User.username], it[User.id])
            }.first()
        }
    }

    override fun deleteUser(userName: String) {
        return transaction {
            User.deleteWhere {
                User.username eq userName
            }
        }
    }

    override fun addUser(user: UserInsert) {
         transaction {
            User.insert {
                it[username] = user.username
                it[password] = user.password
            }
        }
    }

    override fun updateUser(userName: String, user: UserInsert) {
        transaction {
            User.update({ User.username eq userName }) {
                it[User.password] = user.password
            }
        }
    }

}