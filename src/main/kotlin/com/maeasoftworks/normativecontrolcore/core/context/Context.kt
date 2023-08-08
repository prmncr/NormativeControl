package com.maeasoftworks.normativecontrolcore.core.context

import com.maeasoftworks.normativecontrolcore.core.enums.CaptureType
import com.maeasoftworks.normativecontrolcore.core.model.ChapterMarkers
import com.maeasoftworks.normativecontrolcore.core.model.Mistake
import com.maeasoftworks.normativecontrolcore.core.parsers.chapters.ChapterParser
import com.maeasoftworks.normativecontrolcore.core.parsers.chapters.FrontPageParser
import com.maeasoftworks.normativecontrolcore.core.utils.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.wml.*
import java.math.BigInteger
import java.util.*

class Context(
    mlPackage: WordprocessingMLPackage,
    val resolver: PropertyResolver
) {
    private val doc = mlPackage.mainDocumentPart
    val ptr: Pointer = Pointer(doc.content.size)

    var chapter: ChapterParser = FrontPageParser
        set(value) {
            field = value
            println("Chapter changed to ${ChapterMarkers.names[value]?.joinToString("/")}")
        }
    var lastDefinedChapter: ChapterParser = FrontPageParser
    private val comments = (doc.commentsPart ?: CommentsPart().apply {
        jaxbElement = Comments()
        doc.addTargetPart(this)
    }).also { ptr.lastMistake = it.jaxbElement.comment.size.toLong() }

    fun addMistake(mistake: Mistake) {
        val formattedText = if (mistake.actual != null && mistake.expected != null) {
            "${mistake.mistakeType.ru}: найдено: ${mistake.actual}, требуется: ${mistake.expected}."
        } else {
            mistake.mistakeType.ru
        }

        val id = ptr.lastMistake++
        val comment = createComment(id, formattedText)
        comments.jaxbElement.comment.add(comment)

        val commentRangeStart = CommentRangeStart().apply {
            this.id = BigInteger.valueOf(id)
        }
        val commentRangeEnd = CommentRangeEnd().apply {
            this.id = BigInteger.valueOf(id)
        }

        when (mistake.captureType) {
            CaptureType.SECTOR -> {
                val paragraph = doc.content[ptr.bodyPosition] as P
                paragraph.content.add(0, commentRangeStart)
                paragraph.content.add(1, commentRangeEnd)
                paragraph.content += createRunCommentReference(id)
                ptr.totalChildContentSize += 3
                ptr.childContentPosition += 3
            }

            CaptureType.P -> {
                var paragraphStart: P
                var putInStartToStart = false
                var paragraphEnd: P
                var putInEndToEnd = false

                try {
                    paragraphStart = doc.content[ptr.bodyPosition] as P
                    putInStartToStart = true
                } catch (e: ClassCastException) {
                    paragraphStart = doc.content[ptr.bodyPosition - 1] as P
                }

                try {
                    paragraphEnd = doc.content[ptr.bodyPosition] as P
                    putInEndToEnd = true
                } catch (e: ClassCastException) {
                    paragraphEnd = doc.content[ptr.bodyPosition + 1] as P
                }

                if (putInStartToStart) {
                    paragraphStart.content.add(0, commentRangeStart)
                    paragraphStart.content += createRunCommentReference(id)
                    ptr.childContentPosition += 2
                    ptr.totalChildContentSize += 2
                } else {
                    paragraphStart.content.add(commentRangeStart)
                    paragraphStart.content += createRunCommentReference(id)
                }

                if (putInEndToEnd) {
                    paragraphEnd.content.add(commentRangeEnd)
                    ptr.childContentPosition++
                    ptr.totalChildContentSize++
                } else {
                    paragraphEnd.content.add(0, commentRangeEnd)
                }
            }

            CaptureType.R -> {
                val paragraph: P = try {
                    doc.content[ptr.bodyPosition] as P
                } catch (e: ClassCastException) {
                    ptr.totalChildSize++
                    P().apply { doc.content.add(ptr.bodyPosition + 1, this) }
                }
                paragraph.content.add(if (ptr.childContentPosition == 0) 0 else ptr.childContentPosition - 1, commentRangeStart)
                ptr.childContentPosition++
                ptr.totalChildContentSize++
                if (ptr.childContentPosition == ptr.totalChildContentSize - 1) {
                    paragraph.content += commentRangeEnd
                    paragraph.content += createRunCommentReference(id)
                    ptr.childContentPosition += 2
                    ptr.totalChildContentSize += 2
                } else {
                    paragraph.content.add(ptr.childContentPosition + 1, commentRangeEnd)
                    paragraph.content.add(ptr.childContentPosition + 2, createRunCommentReference(id))
                    ptr.childContentPosition += 2
                    ptr.totalChildContentSize += 2
                }
            }
        }
    }

    private fun createComment(commentId: Long, message: String): Comments.Comment {
        return Comments.Comment().apply {
            id = BigInteger.valueOf(commentId)
            author = "normative control"
            content.add(
                P().apply {
                    paraId = "nc-${UUID.randomUUID()}"
                    content.add(
                        R().apply {
                            content.add(Text().also { text -> text.value = message })
                            rPr = RPr().apply {
                                rFonts = RFonts().apply {
                                    ascii = "Consolas"
                                    cs = "Consolas"
                                    eastAsia = "Consolas"
                                    hAnsi = "Consolas"
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    private fun createRunCommentReference(commentId: Long): R {
        return R().also { run ->
            run.content.add(
                R.CommentReference().also {
                    it.id = BigInteger.valueOf(commentId)
                }
            )
        }
    }
}