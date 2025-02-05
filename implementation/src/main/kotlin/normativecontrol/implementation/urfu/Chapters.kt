package normativecontrol.implementation.urfu

import normativecontrol.core.chapters.Chapter

internal enum class Chapters(
    override val names: Array<String>? = null,
    override val nextChapters: (() -> Array<Chapter>)? = null,
    override val prefixes: Array<String>? = null,
    override val shouldBeVerified: Boolean = true
) : Chapter {
    NO_DETECT_BODY(
        arrayOf("РАЗДЕЛ НЕ ОПРЕДЕЛЕН"),
        { arrayOf(Annotation, Contents, Definitions, Abbreviations, Introduction) },
        shouldBeVerified = false
    ),

    Annotation(
        arrayOf("РЕФЕРАТ"),
        { arrayOf(Contents) },
        shouldBeVerified = false
    ),

    Contents(
        arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"),
        { arrayOf(Definitions, Abbreviations, Introduction) },
        shouldBeVerified = false
    ),

    Definitions(
        arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ", "ПЕРЕЧЕНЬ ОПРЕДЕЛЕНИЙ, СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"),
        { arrayOf(Abbreviations, Introduction) },
        shouldBeVerified = false
    ),

    Abbreviations(
        arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"),
        { arrayOf(Introduction) },
        shouldBeVerified = false
    ),

    Introduction(
        arrayOf("ВВЕДЕНИЕ"),
        { arrayOf(Body) }
    ),

    Body(
        arrayOf("ОСНОВНОЙ РАЗДЕЛ"),
        { arrayOf(Conclusion, Body) }
    ),

    Conclusion(
        arrayOf("ЗАКЛЮЧЕНИЕ"),
        { arrayOf(References) }
    ),

    References(
        arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ", "СПИСОК ИСПОЛЬЗОВАННЫХ ИСТОЧНИКОВ"),
        { arrayOf(Appendix, References) },
        shouldBeVerified = false
    ),

    Appendix(
        arrayOf("ПРИЛОЖЕНИЕ"),
        { arrayOf(Appendix) },
        arrayOf("ПРИЛОЖЕНИЕ"),
        shouldBeVerified = false
    )
}