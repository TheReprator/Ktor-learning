package com.firstapp.api

import com.firstapp.crud.UserDatabaseRepository
import com.firstapp.errors.MissingParameterError
import com.firstapp.modal.UserInsert
import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.userApi(userDatabaseRepository: UserDatabaseRepository) {

    route("/user") {
        get {
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    userDatabaseRepository.getAllUser(),
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        get("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    userDatabaseRepository.getUser(userName),
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        delete("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "Delete status is: ${userDatabaseRepository.deleteUser(userName)}",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        post {
            val request = call.receive<UserInsert>()

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    userDatabaseRepository.addUser(request),
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        put("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            val request = call.receive<UserInsert>()

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "Update status is ${userDatabaseRepository.updateUser(userName, request)}",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }
    }
}