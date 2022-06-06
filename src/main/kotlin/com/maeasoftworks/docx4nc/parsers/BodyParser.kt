package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.Rules
import com.maeasoftworks.docx4nc.apply
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.model.Picture
import org.docx4j.TextUtils
import org.docx4j.wml.P
import org.docx4j.wml.R

class BodyParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    private var isPicturesOrderedInSubchapters: Boolean? = false
    private lateinit var innerPictures: MutableList<Picture>

    private val subchapters = Subchapter()

    override fun parse() {
        createSubchaptersModel(chapter.startPos + 1, 2, subchapters)
        validateSubchapters(subchapters.num.toString(), subchapters)
        parseHeader()
        parseSubchapter(subchapters)
        flatPictures()
        root.checkPicturesOrder(
            this,
            if (isPicturesOrderedInSubchapters!!) 1 else 0,
            isPicturesOrderedInSubchapters!!,
            innerPictures
        )
    }

    private fun createSubchaptersModel(pPos: Int, level: Int, currentChapter: Subchapter): Int {
        var pos = pPos
        while (pos <= chapter.startPos + chapter.content.size - 1) {
            if (pos == -1) {
                return -1
            }
            if (root.isHeader(pos, level)) {
                val newChapter = Subchapter(
                    pos,
                    root.mainDocumentPart.content[pos] as P,
                    currentChapter,
                    Regex("^(?:\\d\\.?){1,3}")
                        .find(TextUtils.getText(root.mainDocumentPart.content[pos]))!!.value
                        .removeSuffix(".")
                        .split('.')[level - 1]
                        .toInt(),
                    level
                )
                newChapter.content.add(root.mainDocumentPart.content[pos])
                currentChapter.subchapters.add(newChapter)
                currentChapter.subchapters.sortBy { it.level }
                pos = createSubchaptersModel(pos + 1, level + 1, newChapter)
            } else if (root.isHeader(pos, level - 1)) {
                return pos
            } else if (root.isHeader(pos, level - 2)) {
                return -1
            } else {
                currentChapter.content.add(root.mainDocumentPart.content[pos])
                pos++
            }
        }
        return -1
    }

    private fun flatPictures() {
        subchapters.flatPictures()
        innerPictures = subchapters.pictures
    }

    private fun parseSubchapter(subchapter: Subchapter) {
        if (subchapter.subheader != null) {
            val subheaderPPr = root.resolver.getEffectivePPr(subchapter.subheader.pPr)
            val isEmpty = TextUtils.getText(subchapter.subheader).isEmpty()
            headerPFunctions.apply(root, subchapter.startPos, subheaderPPr, isEmpty)
            commonPFunctions.apply(root, subchapter.startPos, subheaderPPr, isEmpty)
            for (r in 0 until subchapter.subheader.content.size) {
                if (subchapter.subheader.content[r] is R) {
                    val rPr = root.resolver.getEffectiveRPr(
                        (subchapter.subheader.content[r] as R).rPr,
                        subchapter.subheader.pPr
                    )
                    headerRFunctions.apply(root, subchapter.startPos, r, rPr, isEmpty)
                    commonRFunctions.apply(root, subchapter.startPos, r, rPr, isEmpty)
                } else {
                    handlePContent(subchapter.startPos, r, this)
                }
            }
        }
        for (p in subchapter.startPos + 1 until subchapter.startPos + subchapter.content.size) {
            if (pictureTitleExpected) {
                pictureTitleExpected = false
                continue
            }
            val pPr = root.resolver.getEffectivePPr((root.mainDocumentPart.content[p] as P).pPr)
            val paragraph = root.mainDocumentPart.content[p] as P
            val isEmptyP = TextUtils.getText(paragraph).isBlank()
            commonPFunctions.apply(root, p, pPr, isEmptyP)
            regularPFunctions.apply(root, p, pPr, isEmptyP)
            for (r in 0 until paragraph.content.size) {
                if (paragraph.content[r] is R) {
                    val rPr = root.resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
                    commonRFunctions.apply(root, p, r, rPr, isEmptyP)
                    regularRFunctions.apply(root, p, r, rPr, isEmptyP)
                    for (c in 0 until (paragraph.content[r] as R).content.size) {
                        handleRContent(p, r, c, this, subchapter.pictures)
                    }
                } else {
                    handlePContent(p, r, this)
                }
            }
            if (pPr.numPr != null) {
                validateListElement(p)
            }
        }
        for (sub in subchapter.subchapters) {
            parseSubchapter(sub)
        }
    }

    private fun parseHeader() {
        val headerPPr = root.resolver.getEffectivePPr(chapter.header.pPr)
        val isEmpty = TextUtils.getText(chapter.header).isBlank()
        headerPFunctions.apply(root, chapter.startPos, headerPPr, isEmpty)
        commonPFunctions.apply(root, chapter.startPos, headerPPr, isEmpty)
        for (r in 0 until chapter.header.content.size) {
            if (chapter.header.content[r] is R) {
                val rPr = root.resolver.getEffectiveRPr((chapter.header.content[r] as R).rPr, chapter.header.pPr)
                headerRFunctions.apply(root, chapter.startPos, r, rPr, isEmpty)
                commonRFunctions.apply(root, chapter.startPos, r, rPr, isEmpty)
            } else {
                handlePContent(chapter.startPos, r, this)
            }
        }
    }

    override fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(TEXT_HYPERLINKS_NOT_ALLOWED_HERE, p, r)
    }

    override fun handleTable(p: Int) {}

    private fun validateSubchapters(expectedNum: String, subchapter: Subchapter) {
        for (sub in 0 until subchapter.subchapters.size) {
            if ("${expectedNum}.${subchapter.subchapters[sub].num}" != "${expectedNum}.${sub + 1}") {
                root.addMistake(
                    TEXT_BODY_SUBHEADER_NUMBER_ORDER_MISMATCH,
                    this.chapter.startPos,
                    description = "${expectedNum}.${subchapter.subchapters[sub].num}/${expectedNum}.${sub + 1}"
                )
                return
            }
            if (subchapter.level > 3) {
                root.addMistake(TEXT_BODY_SUBHEADER_LEVEL_WAS_MORE_THAN_3, this.chapter.startPos)
                return
            }
            validateSubchapters("${expectedNum}.${sub + 1}", subchapter.subchapters[sub])
        }
    }

    override fun pictureTitleMatcher(title: String): MatchResult? {
        return if (Regex("РИСУНОК (\\d+)").matches(title.uppercase())) {
            isPicturesOrderedInSubchapters = false
            Regex("РИСУНОК (\\d+)").find(title.uppercase())
        } else if (Regex("РИСУНОК (\\d)\\.(\\d+)").matches(title.uppercase())) {
            isPicturesOrderedInSubchapters = true
            Regex("РИСУНОК (\\d)\\.(\\d+)").find(title.uppercase())
        } else null
    }

    companion object {
        private val commonPFunctions = listOf(
            Rules.Default.Common.P::hasNotBackground,
            Rules.Default.Common.P::notBordered
        )

        private val commonRFunctions = listOf(
            Rules.Default.Common.R::isTimesNewRoman,
            Rules.Default.Common.R::fontSizeIs14,
            Rules.Default.Common.R::notItalic,
            Rules.Default.Common.R::notCrossedOut,
            Rules.Default.Common.R::notHighlighted,
            Rules.Default.Common.R::isBlack,
            Rules.Default.Common.R::letterSpacingIs0
        )

        private val headerRFunctions = listOf(
            Rules.Default.Header.R::isBold
        )

        private val headerPFunctions = listOf(
            Rules.Body.Header.P::justifyIsLeft,
            Rules.Body.Header.P::isNotUppercase,
            Rules.Default.Header.P::lineSpacingIsOne,
            Rules.Default.Header.P::emptyLineAfterHeaderExists,
            Rules.Default.Header.P::hasNotDotInEnd,
            Rules.Default.RegularText.P::firstLineIndentIs1dot25,
        )

        private val regularPFunctions = listOf(
            Rules.Default.RegularText.P::leftIndentIs0,
            Rules.Default.RegularText.P::rightIndentIs0,
            Rules.Default.RegularText.P::firstLineIndentIs1dot25,
            Rules.Default.RegularText.P::justifyIsBoth,
            Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
        )

        private val regularRFunctions = listOf(
            Rules.Default.RegularText.R::isNotBold,
            Rules.Default.RegularText.R::isNotCaps,
            Rules.Default.RegularText.R::isUnderline
        )
    }

    inner class Subchapter(
        val startPos: Int,
        val subheader: P?,
        private val root: Subchapter?,
        val num: Int?,
        val level: Int
    ) {
        /*
        * Generates Subchapter with predefined args. Use carefully!
        * */
        constructor() : this(
            chapter.startPos + 1,
            null,
            null,
            Regex("^(?:\\d\\.?){1,3}").let {
                TextUtils.getText(chapter.header)
                    .let { x -> if (x != null) it.find(x)?.value?.removeSuffix(".")?.toInt() else null }
            },
            1
        )

        fun flatPictures() {
            for (subchapter in subchapters) {
                subchapter.flatPictures()
            }
            root?.pictures?.addAll(pictures)
        }

        val subchapters: MutableList<Subchapter> = ArrayList()
        val content: MutableList<Any> = ArrayList()
        var pictures: MutableList<Picture> = ArrayList()
    }
}