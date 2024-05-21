package normativecontrol.core.components

import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.traits.Implementor
import normativecontrol.core.traits.TextContentHandler
import org.docx4j.TextUtils

abstract class AbstractTextContentHandler(handler: AbstractHandler<*>): Implementor<TextContentHandler> {
    var textValue: String? = null
    var isBlank: Boolean? = null

    init {
        handler.events.beforeHandle.subscribe { element ->
            if (textValue != null) return@subscribe
            textValue = cacheText(element)
        }
        handler.events.afterHandle.subscribe {
            textValue = null
            isBlank = null
        }
    }

    fun cacheText(element: Any): String {
        if (textValue == null) {
            textValue = TextUtils.getText(element)
            isBlank = textValue!!.isBlank()
        }
        defineStateByText()
        return textValue!!
    }

    open fun defineStateByText() { }
}