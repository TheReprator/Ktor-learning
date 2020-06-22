package com.firstapp

import io.ktor.application.Application

// Only once and always filled on the beginning of backend creation
// null only for unit tests
var application: Application? = null

// Safe to use everywhere
fun logInfo(text: String) {
    error(text)
}