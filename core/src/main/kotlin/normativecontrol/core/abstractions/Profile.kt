package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.states.AbstractGlobalState
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext

abstract class Profile(
    val startChapter: Chapter,
    val verificationConfiguration: VerificationConfiguration,
    val sharedStateFactory: (() -> AbstractGlobalState?)? = null
) {
    open val VerificationContext.globalState: AbstractGlobalState
        get() = throw NotImplementedError()
}