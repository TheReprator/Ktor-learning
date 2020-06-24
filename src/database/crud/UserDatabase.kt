package com.firstapp.crud

import com.firstapp.database.DatabaseFactory.dbQuery
import com.firstapp.database.User
import com.firstapp.errors.MissingParameterError
import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert
import com.sun.media.sound.InvalidDataException
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
        }catch (e: Exception){
            throw InvalidDataException(e.localizedMessage)
        }
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