package ru.maeasoftworks.normativecontrol.api.shared.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.api.shared.extensions.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.shared.modules.S3
import ru.maeasoftworks.normativecontrol.api.students.dto.UploadMultipart
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentService @Inject constructor(private val s3: S3) {
    suspend fun getFileWithAccessKey(accessKey: String, filename: String): Flow<ByteBuffer> {
        if (s3.getTags(filename)["accessKey"] == accessKey) {
            return s3.getObject(filename)
        } else {
            throw NoAccessException()
        }
    }

    suspend fun getFileUnsafe(filename: String): Flow<ByteBuffer> = s3.getObject(filename)

    suspend fun uploadSourceDocument(documentId: String, uploadMultipart: UploadMultipart) = coroutineScope {
        launch { s3.uploadSourceDocument(documentId, uploadMultipart.file, uploadMultipart.accessKey) }
    }
}