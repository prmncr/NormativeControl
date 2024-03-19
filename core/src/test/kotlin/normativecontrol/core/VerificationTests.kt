package normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import normativecontrol.core.implementations.ufru.handlers.PHandler

class VerificationTests : ShouldSpec({
    context(PHandler::class.simpleName!!) {
        should("regex works properly") {
            val fn = PHandler { "isChapterBodyHeader" }

            fn("1.2 Abc") shouldBe true
            fn("1.2. Abc") shouldBe false
            fn("1.2Abc") shouldBe false
            fn("1. Abc") shouldBe false
            fn("1 Abc") shouldBe true
            fn("1              Abc") shouldBe false
            fn("1") shouldBe false
            fn("1             ") shouldBe false
            fn("1 Abc abc abc abc") shouldBe true
            fn("1 Abc.") shouldBe false
        }

        should("chapter order detects properly") {

        }
    }
})