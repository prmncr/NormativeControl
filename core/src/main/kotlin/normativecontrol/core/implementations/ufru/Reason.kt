package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.mistakes.MistakeReason

internal enum class Reason(override val description: String) : MistakeReason {
    ChapterOrderMismatch("Неверный порядок разделов"),
    IncorrectLineSpacingHeader("Некорректный межстрочный интервал заголовка"),
    IncorrectLineSpacingText("Некорректный межстрочный интервал текста"),
    TabInList("Запрещено использование табуляции в качестве разделителя после маркера списка. Методические указания, п. 6.3"),
    ForbiddenMarkerTypeLevel1("Данный вид маркера списка запрещен. Используйте ненумерованный список либо русские строчные буквы. Методические указания, п. 6.3"),
    ForbiddenMarkerTypeReferences("Данный вид маркера списка запрещен. Используйте нумерованный список. Методические указания, п. 6.10"),
    ReferenceNotMentionedInText("Не найдено ссылок для этого источника. Если они есть, убедитесь, что ссылки соответствуют правилам оформления. Методические указания, п. 6.10"),
    LeftIndentOnHeader("Обнаружен отступ слева у заголовка раздела."),
    LeftIndentOnText("Обнаружен отступ слева у текста."),
    LeftIndentOnPictureDescription("Обнаружен отступ слева у подписи рисунка."),
    RightIndentOnHeader("Обнаружен отступ справа у заголовка раздела."),
    RightIndentOnText("Обнаружен отступ справа у текста."),
    IncorrectFirstLineIndentInText("Неверный отступ первой строки у текста"),
    IncorrectFirstLineIndentInHeader("Неверный отступ первой строки у текста"),
    IncorrectLeftIndentInList("Неверный отступ слева в списке")
}