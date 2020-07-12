package com.firstapp.api

import com.firstapp.modal.response.SuccessResponse
import com.firstapp.session.SessionLearning
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Route.sessionApi() {

    route("/sessionLearning") {
        get("/") {
            val session = call.sessions.get<SessionLearning>() ?:
            SessionLearning("Vikram", 11, true, 0)
            call.sessions.set(session.copy(counter = session.counter + 1))

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    session,
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }
        get("/logout") {
            call.sessions.clear<SessionLearning>()

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "session cleared",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }
    }
}