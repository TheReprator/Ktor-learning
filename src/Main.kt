package com.firstapp

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.firstapp.api.getRequest
import com.firstapp.api.postRequest
import com.firstapp.api.userApi
import com.firstapp.crud.UserDatabase
import com.firstapp.database.DatabaseFactory
import com.firstapp.errors.errorHandler
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {

        application = this
        DatabaseFactory.init()

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

        install(Routing) {
            getRequest()
            postRequest()
            userApi(UserDatabase())
        }
    }.also { it.start(wait = true) }

}

data class LResponse(val data: String)
data class LRequest(val id: Int, val name: String)
