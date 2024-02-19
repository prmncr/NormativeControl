package ru.maeasoftworks.normativecontrol.core.implementations.ufru.handlers

import org.docx4j.TextUtils
import org.docx4j.wml.Text
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import ru.maeasoftworks.normativecontrol.core.html.span
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile.globalState

@EagerInitialization
object TextHandler : Handler<Text, Nothing>(
    HandlerConfig.create {
        setHandler { TextHandler }
        setTarget<Text>()
        setProfile(UrFUProfile)
    }
) {
    private val inBrackets = """\[(.*?)]""".toRegex()
    private val removePages = """,\s*[сС]\.(?:.*)*""".toRegex()
    private val removeAndMatchRanges = """(\d+)\s*-\s*(\d+)""".toRegex()
    private val matchReference = """(\d+)""".toRegex()

    context(VerificationContext)
    override fun handle(element: Any) {
        element as Text
        val rawText = TextUtils.getText(element)

        globalState.referencesInText.addAll(getAllReferences(rawText))
        render append span {
            content = rawText.replace("<", "&lt;").replace(">", "&gt;")
        }
    }

    fun getAllReferences(text: String): Set<Int> {
        val set = mutableSetOf<Int>()
        val (refs, ranges) = findAllRanges(clearPages(findAllInBrackets(text))).let { it.first.toList() to it.second }
        ranges.forEach {
            for (i in it) {
                set += i
            }
        }
        findAllReferences(refs).forEach(set::add)
        return set
    }

    fun findAllInBrackets(text: String): Sequence<String> {
        return inBrackets.findAll(text).map { it.groups[1]!!.value }
    }

    fun clearPages(refs: Sequence<String>): Sequence<String> {
        return refs.map { removePages.replace(it, "") }
    }

    fun findAllRanges(refs: Sequence<String>): Pair<Sequence<String>, List<IntRange>> {
        val ranges = mutableListOf<IntRange>()
        return refs.map {
            val r = removeAndMatchRanges.findAll(it)
            for (matchResult in r) {
                ranges += matchResult.groups[1]!!.value.toInt() .. matchResult.groups[2]!!.value.toInt()
            }
            removeAndMatchRanges.replace(it, "")
        } to ranges
    }

    fun findAllReferences(refs: List<String>): List<Int> {
        return refs.flatMap { line -> matchReference.findAll(line).map { it.groups[1]!!.value.toInt() } }
    }
}