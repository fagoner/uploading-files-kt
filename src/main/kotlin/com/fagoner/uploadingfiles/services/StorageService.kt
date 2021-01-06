package com.fagoner.uploadingfiles.services

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.stream.Stream

interface StorageService {

    fun init()

    fun store(file: MultipartFile)

    fun loadAll(): Stream<Path>

    fun loadAsResource(filename: String): Resource

    fun deleteAll()

    fun load(filename: String): Path
}