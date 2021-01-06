package com.fagoner.uploadingfiles

import com.fagoner.uploadingfiles.services.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class UploadingFilesApplication {
    @Bean
    fun init(storageService: StorageService): CommandLineRunner? {
        return CommandLineRunner {
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<UploadingFilesApplication>(*args)
}


