package arguments

import me.aberrantfox.kjdautils.internal.arguments.ChoiceArg
import utilities.ArgumentTestFactory

class ChoiceArgTest : ArgumentTestFactory {
    override val argumentType = ChoiceArg("Choices", "a", "b", "c")

    override val validArgs = listOf(
        "a" to "a",
        "B" to "b"
    )

    override val invalidArgs = listOf("abc", "d", "")
}