package normativecontrol.core.handlers

import normativecontrol.core.configurations.AbstractHandlerCollection
import kotlin.reflect.KClass

/**
 * Automatic scan target annotation. All classes annotated by
 * this annotation should:
 * 1. be an inheritor of [AbstractHandler];
 * 2. have only primary constructor without args.
 *
 * @param handledElementType KClass of elements that can be handled
 * by annotated handler
 * @param configuration KClass of handlers collection
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Handler(
    val handledElementType: KClass<*>,
    val configuration: KClass<out AbstractHandlerCollection>,
    val priority: HandlerPriority = HandlerPriority.OVERRIDABLE
)