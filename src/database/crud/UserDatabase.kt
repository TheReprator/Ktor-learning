package com.firstapp.crud

import com.firstapp.database.DatabaseFactory.dbQuery
import com.firstapp.database.User
import com.firstapp.errors.MissingParameterError
import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert
import com.sun.media.sound.InvalidDataException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

class UserDatabase : UserDatabaseRepository {

    override suspend fun getAllUser(): List<UserFetch> {
        val userFetchList: ArrayList<UserFetch> = arrayListOf()
        transaction {
            User.selectAll().map { userFetchList.add(UserFetch(it[User.username], it[User.id])) }
        }
        return userFetchList
    }

    override suspend fun getUser(userName: String): UserFetch? {
        return transaction {
            User.select {
                User.username.eq(userName)
            }.map {
                UserFetch(it[User.username], it[User.id])
            }.firstOrNull()
        }
    }

    override suspend fun deleteUser(userName: String): Boolean {
         return transaction {
            addLogger(StdOutSqlLogger)
            val deleteResult = User.deleteWhere {
                User.username eq userName
            }

             deleteResult >= 1
        }
    }

    override suspend fun addUser(user: UserInsert): UserFetch? {
        try {
            var statement: InsertStatement<Number>? = null
            dbQuery {
                statement = User.insert { users ->
                    users[User.username] = user.username
                    users[User.password] = user.password
                }
            }
            return rowToUser(statement?.resultedValues?.get(0))
        }catch (e: Exception){
            throw InvalidDataException(e.localizedMessage)
        }
    }

    private fun rowToUser(row: ResultRow?): UserFetch? {
        if (row == null) {
            return null
        }
        return UserFetch(
            row[User.username],
            row[User.id]
        )
    }

    override suspend fun updateUser(userName: String, user: UserInsert): Boolean {
       return transaction {
            val result = User.update({ User.username eq userName }) {
                it[User.password] = user.password
            }
           result >= 1
        }
    }

}