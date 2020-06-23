package com.firstapp.api

import com.firstapp.LRequest
import com.firstapp.logInfo
import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route


fun Route.postRequest() {
    route("/postRequest") {
        post("/") {
            /*val receivedIssue = call.receive(Issue::class)
            call.respond(issueRepo.create(receivedIssue))*/

            val request = call.receive<LRequest>()

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    request,
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }
    }
}