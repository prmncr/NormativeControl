package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.components.DocumentParserFactory
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.daos.DocumentFile
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import com.maeasoftworks.normativecontrol.dtos.enums.FailureType
import com.maeasoftworks.normativecontrol.dtos.enums.State
import com.maeasoftworks.normativecontrol.repositories.DocumentErrorRepository
import com.maeasoftworks.normativecontrol.repositories.DocumentFileRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DocumentManager(
    private val queue: DocumentQueue,
    private val errorRepository: DocumentErrorRepository,
    private val fileRepository: DocumentFileRepository,
    private val factory: DocumentParserFactory
) {
    fun addToQueue(accessKey: String): String {
        val id = UUID.randomUUID().toString().filterNot { it == '-' }
        queue.put(factory.create(Document(id, accessKey)))
        return id
    }

    fun appendFile(documentId: String, accessKey: String, bytes: ByteArray) {
        val parser = queue.getById(documentId)
        if (parser?.document?.accessKey == accessKey) {
            parser.document.file = bytes
            queue.runById(documentId)
        } else {
            TODO("throw error")
        }
    }

    @EventListener
    @Async
    fun saveToDatabase(parser: DocumentParser) {
        if (parser.document.failureType == FailureType.NONE) {
            fileRepository.save(DocumentFile(parser.document.id, parser.document.file))
            errorRepository.saveAll(parser.errors)
        }
    }

    @Transactional
    fun getState(documentId: String, accessKey: String): State {
        val parser = queue.getById(documentId)
        return if (parser?.document?.state == null) {
            if (fileRepository.existsDocumentFileByDocumentId(documentId)) State.SAVED else State.UNDEFINED
        } else if (queue.isUploadAvailable(documentId)) {
            State.READY_TO_UPLOAD
        }
        else {
            parser.document.state
        }
    }

    @Transactional
    fun getErrors(id: String): List<DocumentError> {
        return errorRepository.findAllByDocumentId(id)
    }

    @Transactional
    fun getFile(id: String): ByteArray {
        return fileRepository.findAllByDocumentId(id).flatMap { it.bytes.toList() }.toByteArray()
    }

    @Transactional
    fun dropDatabase() {
        errorRepository.deleteAll()
        fileRepository.deleteAll()
    }
}