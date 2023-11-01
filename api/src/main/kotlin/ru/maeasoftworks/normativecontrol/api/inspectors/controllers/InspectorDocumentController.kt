package ru.maeasoftworks.normativecontrol.api.inspectors.controllers

import ru.maeasoftworks.normativecontrol.api.students.components.DocumentManager
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.nio.ByteBuffer

@RestController
@RequestMapping("/inspector/document")
@CrossOrigin
class InspectorDocumentController(private val documentManager: DocumentManager) {
    @GetMapping("/conclusion", produces = [MediaType.TEXT_HTML_VALUE])
    fun getConclusion(@RequestParam documentId: String): Flux<ByteBuffer> {
        return documentManager.getHtml(documentId)
    }

    @GetMapping("/download", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun download(@RequestParam documentId: String): Flux<ByteBuffer> {
        return documentManager.getConclusion(documentId)
    }
}