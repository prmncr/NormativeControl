package normativecontrol.core.rendering.html

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.css.Stylesheet
import org.intellij.lang.annotations.Language

@HtmlDsl
inline fun css(body: Stylesheet.Builder.() -> Unit): Stylesheet {
    return Stylesheet.Builder().apply(body).build()
}

context(VerificationContext)
@HtmlDsl
inline fun div(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.DIV).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun p(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.P).also(body)
}

context(VerificationContext)
@HtmlDsl
fun br(): HtmlElement {
    return HtmlElement(HtmlElement.Type.BR, false)
}

context(VerificationContext)
@HtmlDsl
inline fun span(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.SPAN).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun head(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.HEAD).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun body(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.BODY).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun html(body: HtmlElement.() -> Unit): HtmlElement {
    return object : HtmlElement(Type.HTML) {
        override fun toString(): String {
            return "<!doctype html>" + super.toString()
        }
    }.also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun style(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.STYLE).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun script(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.SCRIPT).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun label(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.LABEL).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun input(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.INPUT, false).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun create(type: String, hasClosingTag: Boolean = true, body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(type, hasClosingTag).also(body)
}

@HtmlDsl
fun js(@Language("javascript") code: String) = code