package com.firstapp.errors

import com.fasterxml.jackson.core.JsonParseException
import com.firstapp.modal.response.ErrorResponse
import com.sun.media.sound.InvalidDataException
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun StatusPages.Configuration.errorHandler(){

    exception<MissingParameterError> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                "Missing parameter ${cause.name}",
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }

    exception<MissingElementError> { cause ->
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(
                "Cannot process your request because of missing ${cause.name}",
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }

    exception<SecretInvalidError> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                "This endpoint is protected and your secret is invalid",
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }

    exception<NotFoundException> { cause ->
        cause.printStackTrace()
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                cause.message!!,
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }

    exception<InvalidDataException> { cause ->
        cause.printStackTrace()
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                cause.message!!,
                HttpStatusCode.BadRequest.value
            )
        )
    }

    exception<KotlinNullPointerException> { cause ->
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                "Data Not Found",
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }

    exception<JsonParseException> { cause ->
        cause.printStackTrace()
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorResponse(
                "Data Not Found ... Maybe The Json Data Is Invalid",
                HttpStatusCode.InternalServerError.value,
                cause.stackTrace
            )
        )
    }

    exception<Throwable> { cause ->
        call.respond(
            HttpStatusCode.NotImplemented,
            ErrorResponse(
                "This feature is not implemented",
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }
}