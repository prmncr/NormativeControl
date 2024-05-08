package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.Reason
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.Tbl

@Handler(Tbl::class, UrFUConfiguration::class)
class TblHandler: AbstractHandler<Tbl>(), StateProvider<UrFUState> {
    override fun addHooks() {
        runtime.handlers[PHandler::class]?.hooks?.afterHandle?.subscribe {
            with(ctx) {
                state.tableCounter.increment()
            }
        }
    }

    context(VerificationContext)
    override fun handle(element: Tbl) {
        if (!state.tableTitleCounter.isReset) {
            mistake(Reason.TableWithoutTitle)
        }
        state.tableTitleCounter.reset()
        state.tableCounter.reset()
    }
}