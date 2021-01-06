package com.fagoner.uploadingfiles.controllers

import com.fagoner.uploadingfiles.models.FileUploadResponse
import com.fagoner.uploadingfiles.services.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Paths
import javax.activation.MimetypesFileTypeMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.streams.toList


@RestController
@RequestMapping("files")
class FileUploadController {
    @Autowired
    lateinit var storageService: StorageService

    @GetMapping
    fun index(): List<String> {
        val files = storageService.loadAll().map { path ->

            MvcUriComponentsBuilder.fromMethodName(
                FileUploadController::class.java,
                "serveFile", path.fileName.toString()
            )
                .build()
                .toUri()
                .toString()
        }.toList().map {
            it.replace("/files/", "/files/show/")
        }.toList()

        return files
    }

    @GetMapping("{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource>? {
        val file: Resource = storageService.loadAsResource(filename)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename.toString() + "\"")
            .body(file)
    }

    @GetMapping("show2/{filename:.+}")
    @ResponseBody
    fun show2(@PathVariable filename: String): ResponseEntity<Resource>? {
        val file: Resource = storageService.loadAsResource(filename)

        var mime = MimetypesFileTypeMap().run {
            this.getContentType(file.filename.toString())
        }
        println("mime: ${mime} filename: ${file.filename.toString()}")

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.filename.toString() + "\"")
            .header(HttpHeaders.CONTENT_TYPE, mime)
            .body(file)
    }

    @GetMapping("show/{filename:.+}")
    fun serveFileInline(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @PathVariable("filename") filename: String
    ) {
        val destinationFile = Paths.get("upload-dir")!!.resolve(
            Paths.get(filename)
        )

        val file = File(destinationFile.toUri())

        response.setHeader("Content-Disposition", String.format("inline; filename=\"$filename\""))
        response.setContentLength(file.length().toInt())
        val inputStream: InputStream = BufferedInputStream(FileInputStream(file))
        FileCopyUtils.copy(inputStream, response.outputStream)
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    fun HandleUpload(
        @RequestBody
        file: MultipartFile
    ): FileUploadResponse {

        storageService.store(file)
        return FileUploadResponse("You successfully uploaded ${file.originalFilename}")
    }
}