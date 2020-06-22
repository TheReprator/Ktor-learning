package com.firstapp.api

import com.firstapp.crud.UserDatabaseRepository
import com.firstapp.errors.MissingParameterError
import com.firstapp.logInfo
import com.firstapp.modal.UserInsert
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.userApi(userDatabaseRepository: UserDatabaseRepository) {
    route("/user") {
        get {
            call.respond(userDatabaseRepository.getAllUser())
        }

        get("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            call.respond(userDatabaseRepository.getUser(userName))
        }

        delete("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            userDatabaseRepository.deleteUser(userName)
            call.respond("Delete Successfull")
        }

        post {
            val request = call.receive<UserInsert>()
            userDatabaseRepository.addUser(request)
            call.respond("record inserted successfully")
        }

        put("/{username}") {
            val userName = call.parameters["username"] ?: throw MissingParameterError("username")
            val request = call.receive<UserInsert>()
            userDatabaseRepository.updateUser(userName, request)
            call.respond("Update Successfull")
        }
    }
}