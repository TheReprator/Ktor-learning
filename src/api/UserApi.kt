package com.firstapp.api

import com.firstapp.auth.AUTH_NAME_BASIC
import com.firstapp.auth.AUTH_NAME_HASHED
import com.firstapp.crud.UserDatabaseRepository
import com.firstapp.errors.InvalidDataException
import com.firstapp.errors.MissingParameterError
import com.firstapp.modal.UserFetch
import com.firstapp.modal.UserInsert
import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
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

        authenticate(AUTH_NAME_BASIC) {
            get("/{username}") {
                val userName = call.parameters["username"] ?: throw MissingParameterError("username")
                if (!validateEmail(userName))
                    throw InvalidDataException("Invalid email")
                call.respond(
                    HttpStatusCode.OK,
                    SuccessResponse(
                        userDatabaseRepository.getUser(userName),
                        HttpStatusCode.OK.value,
                        "Success"
                    )
                )
            }
        }

        authenticate(AUTH_NAME_HASHED) {
            delete("/{username}") {
                val userName = call.parameters["username"] ?: throw MissingParameterError("username")

                System.out.println("Success, ${call.principal<UserIdPrincipal>()?.name}")

                if (!validateEmail(userName))
                    throw InvalidDataException("Invalid email")

                call.respond(
                    HttpStatusCode.OK,
                    SuccessResponse(
                        "Delete status is: ${userDatabaseRepository.deleteUser(userName)}",
                        HttpStatusCode.OK.value,
                        "Success"
                    )
                )
            }
        }

        post<UserInsert>("/") { request ->

            val result: UserFetch? = userDatabaseRepository.addUser(request)

            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    result,
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        /*post {
            try {
                val request = call.receive<UserInsert>()

                call.respond(
                    HttpStatusCode.OK,
                    SuccessResponse(
                        userDatabaseRepository.addUser(request),
                        HttpStatusCode.OK.value,
                        "Success"
                    )
                )
            } catch (e: Exception) {
                throw InvalidDataException(e.message)
            }
        }*/

        put("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            val request = call.receive<UserInsert>()

            if (!validateEmail(userName))
                throw InvalidDataException("Invalid fetch email")

            if (!validateEmail(request.username))
                throw InvalidDataException("Invalid email in body")

            if (!validatePasssword(request.password))
                throw InvalidDataException("Invalid password in body")

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