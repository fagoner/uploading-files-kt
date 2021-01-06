package com.fagoner.uploadingfiles.services

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "storage")
class StorageProperties(
    var location: String = "upload-dir"
)