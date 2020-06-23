package com.firstapp.api

import com.firstapp.errors.MissingParameterError
import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.getRequest() {

    route("/") {
        get("/") {
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "default Url",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        get("/plain") {
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "great working",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        get("/returnParam/{min}") {
            val min = call.parameters["min"] ?: throw MissingParameterError("min")

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    min,
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }
    }
}