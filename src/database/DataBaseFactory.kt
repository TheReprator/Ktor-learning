package com.firstapp.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        //Database.connect(hikari())
    }

    /**
     * Look at hikari.properties and change accordingly
     * */
    private fun hikari(): HikariDataSource {
        val config = HikariConfig("/hikari.properties")
        config.schema = "public"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}