package com.firstapp

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.firstapp.api.*
import com.firstapp.auth.authenticationForm
import com.firstapp.auth.authenticationJWT
import com.firstapp.crud.UserDatabase
import com.firstapp.database.DatabaseFactory
import com.firstapp.errors.errorHandler
import com.firstapp.session.sessionLearning
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.util.toMap
import org.slf4j.event.Level
import java.io.File
import java.io.IOException

fun main(args: Array<String>) {

    embeddedServer(Netty, 8080) {

        DatabaseFactory.init()

        install(CallLogging)
        {
            level = Level.TRACE
        }

        install(StatusPages) {
            errorHandler()
        }

        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)

                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
            }
        }

        install(Sessions) {
            sessionLearning()
        }

        install(Locations)

        install(Authentication) {
            authenticationForm()
            authenticationJWT()
        }

        install(Routing) {
            // Print REST requests into a log
            trace {
                //application.log.debug(it.buildText())
                application.log.debug(it.call.request.headers.toMap().toString())
            }

            getRequest()
            postRequest()
            userApi(UserDatabase())
            upload(createUploadDirectory())
            sessionApi()
            jwtRequest()
        }

    }.also { it.start(wait = true) }

}

fun createUploadDirectory(): File {
    val uploadDirPath: String = ".multiPartFolder"
    val uploadDir = File(uploadDirPath)
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }
    return uploadDir
}

data class LRequest(val id: Int, val name: String)
