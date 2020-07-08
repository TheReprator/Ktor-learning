package com.firstapp.errors

import com.fasterxml.jackson.core.JsonParseException
import com.firstapp.modal.response.ErrorResponse
import io.ktor.application.call
import io.ktor.features.MissingRequestParameterException
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.request.uri
import io.ktor.response.respond
import java.lang.IllegalArgumentException
import javax.naming.AuthenticationException

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

    exception<IllegalArgumentException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                cause.localizedMessage.orEmpty(),
                HttpStatusCode.BadRequest.value,
                null
            )
        )
    }

    exception<MissingRequestParameterException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                cause.localizedMessage.orEmpty(),
                HttpStatusCode.BadRequest.value,
                null
            )
        )
    }

    status(HttpStatusCode.Unauthorized) { statusCode ->
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

    exception<NotFoundException> { cause ->
        cause.printStackTrace()
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                cause.message.orEmpty(),
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
                cause.message ?: " Invalid Data",
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

    exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized,
            ErrorResponse(
                "Invalid auth",
                HttpStatusCode.NotFound.value,
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

    exception<IllegalStateException> { cause ->
        call.respond(
            HttpStatusCode.NotImplemented,
            ErrorResponse(
                cause.message.orEmpty(),
                HttpStatusCode.NotFound.value,
                cause.stackTrace
            )
        )
    }
}