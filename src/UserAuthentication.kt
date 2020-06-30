package com.firstapp

import com.firstapp.errors.SecretInvalidError
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import javax.naming.AuthenticationException

private const val AUTH_BASIC_USERNAME = "ktorVikram"
private const val AUTH_BASIC_PASSWORD = "ktorPassword"

const val AUTH_NAME_BASIC = "ktorBasicAuth"

fun Authentication.Configuration.authenticationForm() {
    basic(name = AUTH_NAME_BASIC) {
        realm = "Ktor Server"
        validate { credentials ->
            if (AUTH_BASIC_USERNAME == credentials.name && AUTH_BASIC_PASSWORD == credentials.password) {
                UserIdPrincipal(credentials.name)
            } else {
                throw AuthenticationException("invalid basic auth")
            }
        }
    }
}