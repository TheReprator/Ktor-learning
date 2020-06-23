package com.firstapp.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object User : Table() {
    val username: Column<String> = text("username")
    val password: Column<String> = text("password")
    val id: Column<Int> = integer("id").autoIncrement()

    override val primaryKey = PrimaryKey(username)
}