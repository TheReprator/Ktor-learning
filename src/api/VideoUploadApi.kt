package com.firstapp.api

import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream


@Location("/uploadVideo/{id}")
class UploadVideo(val id: Int)

fun Route.upload(uploadDir: File) {

    post<UploadVideo> {

        val multipart = call.receiveMultipart().readAllParts()
        val multiMap = multipart.associateBy { it.name }.toMap()
        val data = MultPartRequestModal(multiMap)
        println(data)

        val ext = File(data.file.originalFileName).extension
        val file = File(uploadDir, "upload-${System.currentTimeMillis()}-${data.file.originalFileName}")
        data.file.streamProvider()
            .use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }

        call.respond(
            HttpStatusCode.OK,
            SuccessResponse(
                file,
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

class MultPartRequestModal(map: Map<String?, PartData>) {
    val file: PartData.FileItem by map
    val type: PartData.FormItem by map
    val title: PartData.FormItem by map

    override fun toString() = "${file.originalFileName}, ${type.value}, ${title.value}"
}