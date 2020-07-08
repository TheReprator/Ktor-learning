package com.firstapp.api

import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.getOrFail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.IllegalArgumentException

@Location("/uploadVideo/{title}")
class UploadVideo(val title:String)

/**
 * Register [Upload] routes.
 */
fun Route.upload(uploadDir: File) {
    /**
     * Registers a POST route for [Upload] that actually read the bits sent from the client and creates a new video
     * using the [database] and the [uploadDir].
     */
    post<UploadVideo> {
        val multipart = call.receiveMultipart()
        var videoFile: File? = null

        // Processes each part of the multipart input content of the user
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name != "title")
                        throw IllegalArgumentException("Title parameter not found")
                    //title = part.value
                }
                is PartData.FileItem -> {
                    if (part.name != "file")
                        throw IllegalArgumentException("file parameter not found")

                    val ext = File(part.originalFileName).extension
                    val file = File(uploadDir, "upload-${System.currentTimeMillis()}-${call.parameters.getOrFail("title").hashCode()}.$ext")
                    part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
                    videoFile = file
                }
            }

            part.dispose()
        }

        call.respond(
            HttpStatusCode.OK,
            SuccessResponse(
                videoFile!!,
                HttpStatusCode.OK.value,
                "video file stored"
            )
        )
    }
}

/**
 * Utility boilerplate method that suspending,
 * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
 *
 * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
 * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
 */
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
