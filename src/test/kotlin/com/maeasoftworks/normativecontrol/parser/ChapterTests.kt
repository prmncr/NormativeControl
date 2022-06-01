package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.enums.ChapterType
import com.maeasoftworks.normativecontrol.parser.enums.ChapterType.*
import com.maeasoftworks.normativecontrol.parser.enums.MistakeType.*
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import org.junit.jupiter.api.Test

class ChapterTests : ParserTestFactory(ChapterTests::class) {
    private fun chapterAssert(found: MutableList<Chapter>, vararg expected: ChapterType) {
        assert(found.size == expected.size) { "Expected: ${expected.size} chapters\nFound: ${found.size}" }
        for (i in 0 until found.size) {
            assert(found[i].type == expected[i]) { "Expected: ${expected[i].name}\nFound: ${found[i].type.name}" }
        }
    }

    @Test
    fun `all chapters detected`() {
        val parser = createParser("correct chapters.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()

        chapterAssert(
            parser.chapters,
            FRONT_PAGE,
            ANNOTATION,
            CONTENTS,
            INTRODUCTION,
            BODY,
            BODY,
            CONCLUSION,
            REFERENCES,
            APPENDIX
        )
        errorAssert(parser.errors)
    }

    @Test
    fun `missing chapter reported`() {
        val parser = createParser("missed chapter.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()

        chapterAssert(parser.chapters, FRONT_PAGE, ANNOTATION, CONTENTS, INTRODUCTION, CONCLUSION, REFERENCES, APPENDIX)
        errorAssert(parser.errors, CHAPTER_BODY_NOT_FOUND)
    }

    @Test
    fun `all missing chapters reported`() {
        val parser = createParser("missed center.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        chapterAssert(parser.chapters, FRONT_PAGE, APPENDIX)
        errorAssert(
            parser.errors,
            CHAPTER_ANNOTATION_NOT_FOUND,
            CHAPTER_CONTENTS_NOT_FOUND,
            CHAPTER_INTRODUCTION_NOT_FOUND,
            CHAPTER_BODY_NOT_FOUND,
            CHAPTER_CONCLUSION_NOT_FOUND,
            CHAPTER_REFERENCES_NOT_FOUND
        )
    }

    @Test
    fun `full document chapters detected properly`() {
        val parser = createParser("general/full test 1.docx", true)
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        chapterAssert(
            parser.chapters,
            FRONT_PAGE,
            ANNOTATION,
            CONTENTS,
            INTRODUCTION,
            BODY,
            BODY,
            BODY,
            BODY,
            BODY,
            CONCLUSION,
            REFERENCES,
            APPENDIX
        )
        errorAssert(parser.errors)
    }

    @Test
    fun `body headers order detects`() {
        val parserBase = createParser("header order detected.docx")
        parserBase.setupChapters()
        chapterAssert(parserBase.chapters, BODY, BODY, BODY, BODY)
        errorAssert(
            parserBase.errors,
            CHAPTER_FRONT_PAGE_NOT_FOUND,
            CHAPTER_ANNOTATION_NOT_FOUND,
            CHAPTER_CONTENTS_NOT_FOUND,
            CHAPTER_INTRODUCTION_NOT_FOUND,
            CHAPTER_CONCLUSION_NOT_FOUND,
            CHAPTER_REFERENCES_NOT_FOUND,
            CHAPTER_APPENDIX_NOT_FOUND
        )
    }

    @Test
    fun `body headers disorder detects`() {
        val parserBase = createParser("header disorder detected.docx")
        parserBase.setupChapters()
        chapterAssert(parserBase.chapters, BODY, BODY, BODY, BODY)
        errorAssert(
            parserBase.errors,
            CHAPTER_FRONT_PAGE_NOT_FOUND,
            CHAPTER_ANNOTATION_NOT_FOUND,
            CHAPTER_CONTENTS_NOT_FOUND,
            CHAPTER_INTRODUCTION_NOT_FOUND,
            CHAPTER_CONCLUSION_NOT_FOUND,
            CHAPTER_REFERENCES_NOT_FOUND,
            CHAPTER_APPENDIX_NOT_FOUND,
            CHAPTER_BODY_DISORDER,
            CHAPTER_BODY_DISORDER,
            CHAPTER_BODY_DISORDER,
            CHAPTER_BODY_DISORDER
        )
    }
}