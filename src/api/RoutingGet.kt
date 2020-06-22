package com.firstapp.api

import com.firstapp.LResponse
import com.firstapp.errors.MissingParameterError
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.getRequest() {
    route("/") {
        get("/") {
            call.respond(LResponse("default Url"))
        }

        get("/plain") {
            call.respond(LResponse("great working"))
        }

        get("/returnParam/{min}") {
            val min = call.parameters["min"] ?: throw MissingParameterError("publicationId")

            call.respond(min)
        }
    }
}