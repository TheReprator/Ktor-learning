package com.firstapp.session

import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.directorySessionStorage
import java.io.File

data class SessionLearning(val name: String, val id: Int, val isMarried: Boolean, val counter: Int)

fun Sessions.Configuration.sessionLearning() {
    cookie<SessionLearning>(
        "SESSION_SERVER",
        directorySessionStorage(File(".sessions"), cached = true)
    ) {
        cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
    }
}