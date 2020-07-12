package com.firstapp.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.firstapp.auth.JWTUser
import com.firstapp.auth.JwtConfig
import com.firstapp.modal.response.SuccessResponse

fun Route.jwtRequest() {

    route("/jwt") {
        get {
            call.respond(
                HttpStatusCode.OK,
                SuccessResponse(
                    "Hello JWT",
                    HttpStatusCode.OK.value,
                    "Success"
                )
            )
        }

        post {
            val user = call.receive<JWTUser>()
            print("${user.name} , pwd= ${user.password}")
            val token = JwtConfig.generateToken(user)
            call.respond(
                token
            )

        }

        authenticate {
            get("/authenticate") {
                val authUser = call.authentication.principal<JWTUser>()
                //val authUser = call.principal<JWTUser>()
                call.respond(
                    HttpStatusCode.OK,
                    SuccessResponse(
                    "get authenticated value from token " +
                            "name = ${authUser!!.name}, password= ${authUser.password}",
                        HttpStatusCode.OK.value,
                        "Success"
                    )
                )
            }
        }
    }
}