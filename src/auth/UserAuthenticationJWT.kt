package com.firstapp.auth

import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt

fun Authentication.Configuration.authenticationJWT() {
    jwt {
        verifier(JwtConfig.verifier)
        realm = "com.imran"
        validate {
            val name = it.payload.getClaim("name").asString()
            val password = it.payload.getClaim("password").asString()
            if(name != null && password != null){
                JWTUser(name, password )
            }else{
                null
            }
        }
    }
}