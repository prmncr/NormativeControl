package ru.maeasoftworks.normativecontrol.shared.extensions

import ru.maeasoftworks.normativecontrol.shared.modules.S3

suspend fun S3.uploadFile(documentId: String, file: ByteArray, accessKey: String) =
    putObject(file, documentId, mapOf("accessKey" to accessKey))

suspend fun S3.uploadSource(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(source(documentId), file, accessKey)

suspend fun S3.uploadRender(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(render(documentId), file, accessKey)

suspend fun S3.uploadConclusion(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(conclusion(documentId), file, accessKey)

fun source(documentId: String) = "${ documentId }/source.docx"

fun render(documentId: String) = "${ documentId }/conclusion.html"

fun conclusion(documentId: String) = "${ documentId }/conclusion.docx"