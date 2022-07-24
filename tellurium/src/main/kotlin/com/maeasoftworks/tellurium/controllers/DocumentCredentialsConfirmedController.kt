package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.tellurium.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class DocumentCredentialsConfirmedController(protected val documentManager: DocumentManager) {
    protected inline fun <T> confirm(documentId: String, accessKey: String, body: DocumentManager.() -> T): T {
        if (documentManager.validateAccessKey(documentId, accessKey)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body(documentManager)
    }
}