package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.states.State

class HandlerConfig<T, C : State?>(
    val handler: () -> Handler<T, *>,
    val test: (target: Any) -> Boolean,
    val state: (() -> C)?,
    val stateKey: State.Key?,
    val configuration: Configuration
) {
    companion object {
        inline fun <T, C : State?> create(builder: ConfigBuilder<T, C>.() -> Unit): HandlerConfig<T, C> {
            return ConfigBuilder<T, C>().also(builder).build()
        }
    }

    class ConfigBuilder<T, C : State?> {
        var test: ((target: Any) -> Boolean)? = null
        private var handler: (() -> Handler<T, *>)? = null
        private var state: (() -> C)? = null
        private var configuration: Configuration? = null
        private var stateKey: State.Key? = null

        inline fun <reified T> setTarget() {
            test = { it is T }
        }

        fun setState(key: State.Key, factory: () -> C) {
            state = factory
            stateKey = key
        }

        fun <H : Handler<T, *>> setHandler(fn: () -> H) {
            handler = fn
        }

        fun setProfile(configuration: Configuration) {
            this.configuration = configuration
        }

        fun build(): HandlerConfig<T, C> {
            return HandlerConfig(
                handler ?: throw NullPointerException("Handler should be not null"),
                test ?: throw NullPointerException("Target should be not null"),
                state,
                stateKey,
                configuration ?: throw NullPointerException("Profile should be not null")
            )
        }
    }
}