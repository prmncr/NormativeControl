package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.Text
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.span

@EagerInitialization
object TextHandler : Handler<Text, Nothing>(
    HandlerConfig.create {
        setHandler { TextHandler }
        setTarget<Text>()
        setProfile(Profile.UrFU)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Text
        render append span {
            content = TextUtils.getText(element).replace("<", "&lt;").replace(">", "&gt;")
        }
    }
}