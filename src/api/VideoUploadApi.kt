package com.firstapp.api

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.firstapp.modal.response.SuccessResponse
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
import io.ktor.http.defaultForFilePath
import io.ktor.http.fileExtensions
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
import java.lang.Exception
import javax.naming.SizeLimitExceededException

const val FILE_LIMIT = 3 * 1024  //3 MB Limit
typealias image = ContentType.Image

@Location("/uploadVideo/{id}")
class UploadVideo(val id: Int)

fun Route.upload(uploadDir: File) {

    post<UploadVideo> {

        val multipart = call.receiveMultipart().readAllParts()
        val multiMap = multipart.associateBy { it.name }.toMap()
        val data = MultPartRequestModal(multiMap)
        println(data)

        if(!checkForImageType(data.file.originalFileName!!))
            throw Exception("Only image is supported")

        val ext = File(data.file.originalFileName).extension
        val file = File(uploadDir, "upload-${System.currentTimeMillis()}-${data.file.originalFileName}")
        data.file.streamProvider()
            .use { input ->
                file.outputStream().buffered(20).use { output ->
                    input.copyToSuspend(output)
                }
            }

        file.checkForFileSize()

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

private fun File.checkForFileSize() {
    val fileSizeInKB = this.length() / 1024
    val fileSizeInMB = fileSizeInKB / 1024

    println("size in KB: ${fileSizeInKB}, MB: $fileSizeInMB")
    if (fileSizeInKB > FILE_LIMIT) {
        delete()
        throw SizeLimitExceededException("can't be greater that 3MB")
    }
}

private fun checkForImageType(originalFileName: String): Boolean {
    val contentType = ContentType.defaultForFilePath(originalFileName)
    return contentType.run {
        this == image.Any || this == image.GIF || this == image.JPEG || this == image.PNG ||
                this == image.SVG || this == image.XIcon
    }
}

suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 200,
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