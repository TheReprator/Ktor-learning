package com.firstapp

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(StatusPages) {
            exception<Throwable> { e ->
                call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.BadRequest)
            }
        }

        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)

                //Ignore unknown fields in request
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
            }
        }

        getRequestModule()
        postRequestModule()
    }.also { it.start(wait = true) }

}

data class LResponse(val data: String)
data class LRequest(val id: Int, val name: String)


fun Application.postRequestModule() {
    routing {
        post("/postRequest") {
            val request = call.receive<LRequest>()
            call.respond(request)
        }
    }
}

fun Application.getRequestModule() {
    routing {
        get("/plain") {
            call.respond(LResponse("great working"))
        }

        get("returnParam/{min}") {
            val min = call.parameters["min"]?.toIntOrNull() ?: 0
            call.respond(min.toString())
        }
    }
}

