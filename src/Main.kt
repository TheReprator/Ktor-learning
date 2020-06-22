package com.firstapp

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.firstapp.api.getRequest
import com.firstapp.api.postRequest
import com.firstapp.api.userApi
import com.firstapp.crud.UserDatabase
import com.firstapp.crud.UserDatabaseRepository
import com.firstapp.database.DatabaseFactory
import com.firstapp.errors.MissingElementError
import com.firstapp.errors.MissingParameterError
import com.firstapp.errors.SecretInvalidError
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.error
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {

        Database
        application = this
        DatabaseFactory.init()

        install(StatusPages) {

            exception<MissingParameterError> { cause ->
                call.respond(HttpStatusCode.BadRequest, "Missing parameter ${cause.name}")
            }
            exception<MissingElementError> { cause ->
                call.respond(HttpStatusCode.InternalServerError, "Cannot process your request because of missing ${cause.name}")
            }
            exception<SecretInvalidError> {
                call.respond(HttpStatusCode.BadRequest, "This endpoint is protected and your secret is invalid")
            }
            exception<Throwable> { cause ->
                environment.log.error(cause)
                call.respond(HttpStatusCode.NotImplemented)
            }
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
