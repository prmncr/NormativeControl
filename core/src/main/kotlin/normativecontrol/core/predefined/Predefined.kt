package normativecontrol.core.predefined

import normativecontrol.core.configurations.HandlerCollection
import normativecontrol.core.configurations.AbstractHandlerCollection

/**
 * Predefined collection of handlers for elements that can usually be handled just in one way.
 */
@HandlerCollection(Predefined.NAME)
internal class Predefined : AbstractHandlerCollection(NAME) {
    companion object {
        internal const val NAME = "__PREDEFINED"
    }
}