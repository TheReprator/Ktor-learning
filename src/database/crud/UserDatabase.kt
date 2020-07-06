package com.firstapp.crud

import com.firstapp.api.validateEmail
import com.firstapp.api.validatePasssword
import com.firstapp.database.DatabaseFactory.dbQuery
import com.firstapp.database.User
import com.firstapp.errors.InvalidDataException
import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert
import kotlinx.coroutines.CompletableDeferred
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

    override suspend fun getUser(username: String): UserFetch? {
        return transaction {
            User.select {
                User.username.eq(username)
            }.map {
                UserFetch(it[User.username], it[User.id])
            }.firstOrNull()
        }
    }

    override suspend fun deleteUser(username: String): Boolean {
        return transaction {
            addLogger(StdOutSqlLogger)
            val deleteResult = User.deleteWhere {
                User.username eq username
            }

            deleteResult >= 1
        }
    }

    override suspend fun addUser(user: UserInsert): UserFetch? {

        require(validateEmail(user.username)) { "Invalid email." }
        require(validatePasssword(user.password)) { "Invalid password." }

        try {
            val statement = CompletableDeferred<InsertStatement<Number>>()
            dbQuery {
                val insertStatement = User.insert { users ->
                    users[User.username] = user.username
                    users[User.password] = user.password
                }
                statement.complete(insertStatement)
            }
            return statement.await().resultedValues?.map {
                UserFetch(
                    it[User.username],
                    it[User.id]
                )
            }?.first()
        } catch (e: Exception) {
            throw InvalidDataException(e.localizedMessage)
        }
    }

    override suspend fun updateUser(username: String, user: UserInsert): Boolean {
        return transaction {
            val result = User.update({ User.username eq username }) {
                it[User.password] = user.password
            }
            result >= 1
        }
    }

}