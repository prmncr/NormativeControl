package ru.maeasoftworks.normativecontrol.core.rendering.css

import org.docx4j.w14.STLigatures
import org.docx4j.wml.JcEnumeration
import ru.maeasoftworks.normativecontrol.core.rendering.FONT_SCALING
import ru.maeasoftworks.normativecontrol.core.rendering.PIXELS_IN_POINT
import ru.maeasoftworks.normativecontrol.core.rendering.POINTS_IN_LINES

object BoxSizing : Property<String>("box-sizing")
object BoxShadow : Property<String>("box-shadow")
object Color : Property<String>("color", { if (it != "null") "#$it" else null })
object BackgroundColor : Property<String>("background-color", { if (it != null) "#$it" else null })
object FontFamily : Property<String>("font-family")
object FontSize : DoubleProperty("font-size", "px", FONT_SCALING)
object FontStyle : Property<Boolean?>("font-style", { if (it == true) "italic" else null })
object FontWeight : Property<Boolean?>("font-weight", { if (it == true) "bold" else null })
object FontVariantCaps : Property<Boolean?>("font-variant-caps", { if (it == true) "small-caps" else null})
object Margin : DoubleProperty("margin")
object MarginTop : DoubleProperty("margin-top", "px", PIXELS_IN_POINT)
object MarginLeft : DoubleProperty("margin-left", "px", PIXELS_IN_POINT)
object MarginBottom : DoubleProperty("margin-bottom", "px", PIXELS_IN_POINT)
object MarginRight : DoubleProperty("margin-right", "px", PIXELS_IN_POINT)
object Padding : DoubleProperty("padding")
object PaddingTop : DoubleProperty("padding-top", "px", PIXELS_IN_POINT)
object PaddingLeft : DoubleProperty("padding-left", "px", PIXELS_IN_POINT)
object PaddingBottom : DoubleProperty("padding-bottom", "px", PIXELS_IN_POINT)
object PaddingRight : DoubleProperty("padding-right", "px", PIXELS_IN_POINT)
object Position : Property<String>("position")
object ZIndex : DoubleProperty("z-index")
object Width : DoubleProperty("width", "px", PIXELS_IN_POINT)
object MinWidth : DoubleProperty("min-width", "px", PIXELS_IN_POINT)
object Height : DoubleProperty("height", "px", PIXELS_IN_POINT)
object MinHeight : DoubleProperty("min-height", "px", PIXELS_IN_POINT)
object LineHeight : DoubleProperty("line-height", POINTS_IN_LINES)
object TextIndent : DoubleProperty("text-indent", "px", PIXELS_IN_POINT)
object Hyphens : Property<Boolean?>("hyphens", { if (it == true) "auto" else "none" })
object TextTransform : Property<Boolean?>("text-transform", { if (it == true) "uppercase" else null })
object LetterSpacing : DoubleProperty("letter-spacing", "px", PIXELS_IN_POINT)

object TextAlign : Property<JcEnumeration>(
    "text-align",
    {
        when (it) {
            JcEnumeration.LEFT -> "left"
            JcEnumeration.RIGHT -> "right"
            JcEnumeration.CENTER -> "center"
            JcEnumeration.BOTH -> "justify"
            else -> null
        }
    }
)

object FontVariantLigatures : Property<STLigatures>(
    "font-variant-ligatures",
    {
        when (it) {
            STLigatures.NONE -> "none"
            STLigatures.STANDARD -> "shared-ligatures"
            STLigatures.CONTEXTUAL -> "contextual"
            STLigatures.HISTORICAL -> "historical-ligatures"
            STLigatures.DISCRETIONAL -> "discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL -> "shared-ligatures contextual"
            STLigatures.STANDARD_HISTORICAL -> "shared-ligatures historical-ligatures"
            STLigatures.CONTEXTUAL_HISTORICAL -> "contextual historical-ligatures"
            STLigatures.STANDARD_DISCRETIONAL -> "shared-ligatures discretionary-ligatures"
            STLigatures.CONTEXTUAL_DISCRETIONAL -> "contextual discretionary-ligatures"
            STLigatures.HISTORICAL_DISCRETIONAL -> "historical-ligatures discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_HISTORICAL -> "no-discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_DISCRETIONAL -> "no-historical-ligatures"
            STLigatures.STANDARD_HISTORICAL_DISCRETIONAL -> "no-contextual"
            STLigatures.CONTEXTUAL_HISTORICAL_DISCRETIONAL -> "no-shared-ligatures"
            STLigatures.ALL -> null
            null -> null
        }
    }
)