package com.firstapp.auth

import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.response.respondRedirect
import io.ktor.util.getDigestFunction
import java.util.*

private const val AUTH_POST_FORM = "ktorVikram"
private const val AUTH_BASIC_PASSWORD = "ktorPassword"

fun Authentication.Configuration.formAuthenticationPost() {
    form(AUTH_POST_FORM) {
        userParamName = "testUsername"
        passwordParamName = "testPassword"
        challenge {
            // I don't think form auth supports multiple errors, but we're conservatively assuming there will be at
            // most one error, which we handle here. Worst case, we just send the user to login with no context.
            val error = call.authentication.allFailures
            when (error.singleOrNull()) {
                AuthenticationFailedCause.InvalidCredentials ->
                    call.respondRedirect("/login?invalid")

                AuthenticationFailedCause.NoCredentials ->
                    call.respondRedirect("/login?no")

                else ->
                    call.respondRedirect("/login")
            }
        }
        validate { cred: UserPasswordCredential ->
            // Realistically you'd look up the user in a database or something here; this is just a toy example.
            // The values here will be whatever was submitted in the form.
            if (cred.name == "foo" && cred.password == "bar")
                ExamplePrincipal(cred.name)
            else
                null
        }
    }
}


/**
 * You can use whatever type you want to store the user id in; I've aliased it here to follow more easily.
 * Used in the cookie config, session auth config, and routes.
 * Whatever you choose here, it should implement [io.ktor.auth.Principal].
 */
typealias ExamplePrincipal = UserIdPrincipal
