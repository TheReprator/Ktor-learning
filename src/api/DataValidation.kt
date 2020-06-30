package com.firstapp.api

import java.util.regex.Pattern

val PATTERN_EMAIL: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

val PATTERN_PASSWORD: Pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#!\$%_*./&+-])" +
        "[^~\\\\s?<>():;\\'\\\\\\\\\\\"\\|]{8,20}\$")


fun validateEmail(email: String?)=
    PATTERN_EMAIL.matcher(email).matches()

fun validatePasssword(password: String?)=
    PATTERN_PASSWORD.matcher(password).matches()