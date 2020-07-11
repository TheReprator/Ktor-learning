package com.firstapp.errors

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.firstapp.modal.response.ErrorResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.MissingRequestParameterException
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.defaultTextContentType
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import java.io.IOException
import java.lang.IllegalArgumentException
import javax.naming.AuthenticationException

fun StatusPages.Configuration.errorHandler(){

    exception<Throwable> { cause ->

        when (cause) {
            is MissingParameterError ->
                handleException(cause, HttpStatusCode.BadRequest)
            is IllegalArgumentException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is MissingRequestParameterException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is InvalidDataException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is MissingKotlinParameterException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is IOException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is NoSuchElementException ->
                handleException(cause, HttpStatusCode.BadRequest)
            is NotFoundException ->
                handleException(cause, HttpStatusCode.NotFound)
            is KotlinNullPointerException ->
                handleException(cause, HttpStatusCode.ExpectationFailed)
            is JsonParseException ->
                handleException(cause, HttpStatusCode.ExpectationFailed)
            is AuthenticationException ->
                handleException(cause, HttpStatusCode.Unauthorized)
            is IllegalStateException ->
                handleException(cause, HttpStatusCode.ExpectationFailed)
            else ->
                handleException(cause)
        }
    }

    status(HttpStatusCode.Unauthorized) { code ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(
                "invalid authentication ${call.request.uri}",
                HttpStatusCode.Unauthorized.value,
                null
            )
        )
    }

    status(HttpStatusCode.NotFound) { statusCode ->
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                "Url not found for ${call.request.uri}",
                HttpStatusCode.NotFound.value,
                null
            )
        )
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleException(cause: Throwable,
                                                                           httpStatusCode: HttpStatusCode = HttpStatusCode.InternalServerError) {
    call.defaultTextContentType(ContentType("application", "json"))

    call.respond(
        httpStatusCode,

        ErrorResponse(
            "${cause.message.orEmpty()} for url ${call.request.uri}",
            httpStatusCode.value,
            httpStatusCode.description/*,
            cause.stackTrace*/
        )
    )
}