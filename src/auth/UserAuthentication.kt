package com.firstapp.auth

import io.ktor.auth.Authentication
import io.ktor.auth.UserHashedTableAuth
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.util.getDigestFunction
import java.util.*

private const val AUTH_BASIC_USERNAME = "ktorVikram"
private const val AUTH_BASIC_PASSWORD = "ktorPassword"

const val AUTH_NAME_BASIC = "ktorBasicAuth"
const val AUTH_NAME_HASHED = "ktorHashed"

val hashedUserTable = UserHashedTableAuth(
    getDigestFunction("SHA-256") { "ktor${it.length}" },
    table = mapOf(
        "ktorHashed" to Base64.getDecoder().decode("vwlr4QVIkbjNeN5ardGYktSZ6FtKP6/S1SeMl5z7aEE=") // sha256 for "test"
    )
)

fun Authentication.Configuration.authenticationForm() {
    basic(name = AUTH_NAME_BASIC) {
        //realm = "Ktor Server"
        validate { credentials ->
            if (AUTH_BASIC_USERNAME == credentials.name && AUTH_BASIC_PASSWORD == credentials.password) {
                UserIdPrincipal(credentials.name)
            } else {
                null
            }
        }
    }

    basic(AUTH_NAME_HASHED) {
        realm = "ktor hashed"
        validate { credentials ->
            hashedUserTable.authenticate(credentials)
        }
    }
}