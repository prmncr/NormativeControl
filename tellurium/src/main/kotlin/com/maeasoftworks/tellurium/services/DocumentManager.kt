package com.maeasoftworks.tellurium.services

import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.polonium.model.DocumentData
import com.maeasoftworks.polonium.parsers.DocumentParser
import com.maeasoftworks.tellurium.dto.Document
import com.maeasoftworks.tellurium.dto.response.DocumentControlPanelResponse
import com.maeasoftworks.tellurium.dto.response.MistakesResponse
import com.maeasoftworks.tellurium.dto.response.QueueResponse
import com.maeasoftworks.tellurium.dto.response.StatusResponse
import com.maeasoftworks.tellurium.repository.BinaryFileRepository
import com.maeasoftworks.tellurium.repository.CredentialsRepository
import com.maeasoftworks.tellurium.repository.HtmlRepository
import com.maeasoftworks.tellurium.repository.MistakeRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class DocumentManager(
    private val queue: DocumentQueue,
    private val mistakeRepository: MistakeRepository,
    private val fileRepository: BinaryFileRepository,
    private val htmlRepository: HtmlRepository,
    private val credentialsRepository: CredentialsRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    fun createParser(document: Document) = DocumentParser(document.data, document.password)

    fun addToQueue(accessKey: String): QueueResponse {
        val id = UUID.randomUUID().toString().filterNot { it == '-' }
        val document = Document(id, accessKey, DocumentData(), UUID.randomUUID().toString().filterNot { it == '-' })
        queue.put(createParser(document), document)
        return QueueResponse(id, accessKey)
    }

    fun enqueue(documentId: String, bytes: ByteArray) {
        queue[documentId]!!.document.data.file = bytes
        queue.run(documentId)
    }

    @Transactional
    fun getState(documentId: String): StatusResponse {
        val order = queue[documentId]
        return StatusResponse(
            documentId = documentId,
            status = if (order?.document?.data?.status == null) {
                if (fileRepository.existsBinaryFileByDocumentId(documentId)) Status.SAVED else Status.UNDEFINED
            } else if (queue.isUploadAvailable(documentId)) {
                Status.READY_TO_ENQUEUE
            } else {
                order.document.data.status
            }
        )
    }

    @Transactional
    fun getMistakes(id: String) = MistakesResponse(id, mistakeRepository.findAllByDocumentId(id))

    @Transactional
    fun getFile(id: String): ByteArrayResource? {
        return fileRepository.findByDocumentId(id)?.bytes?.toList()?.toByteArray().let {
            if (it == null) null else ByteArrayResource(it)
        }
    }

    @Transactional
    fun getRender(id: String): String? = htmlRepository.findByDocumentId(id)?.html

    @Transactional
    fun validateAccessKey(documentId: String, accessKey: String): Boolean {
        return ((queue[documentId]?.document?.accessKey
            ?: credentialsRepository.findByDocumentId(documentId)?.accessKey)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Document not found."
            )) == bCryptPasswordEncoder.encode(accessKey)
    }

    fun uploaded(accessKey: String, documentId: String): Boolean {
        return queue[documentId].let {
            it != null && it.document.accessKey == accessKey && !it.document.data.file.contentEquals(ByteArray(0))
        } or credentialsRepository.findById(documentId).let {
            it.isPresent && it.get().accessKey == accessKey
        }
    }

    @Transactional
    fun find(id: String): DocumentControlPanelResponse {
        if (credentialsRepository.existsById(id)) {
            val mistakes = mistakeRepository.findAllByDocumentId(id)
            val credentials = credentialsRepository.findById(id).get()
            return DocumentControlPanelResponse(id, credentials.accessKey, credentials.password, mistakes)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun delete(id: String) {
        if (credentialsRepository.existsById(id)) {
            fileRepository.deleteById(id)
            credentialsRepository.deleteById(id)
            mistakeRepository.deleteAllByDocumentId(id)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}