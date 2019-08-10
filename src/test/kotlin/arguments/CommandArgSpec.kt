package arguments

import me.aberrantfox.kjdautils.api.dsl.Command
import me.aberrantfox.kjdautils.internal.arguments.CommandArg
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import mock.GherkinMessages
import mock.attemptConvert
import mock.convertToSingle
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object CommandArgSpec : Spek({
    Feature("CommandObject command Argument") {
        Scenario("A valid command name is passed") {
            Then(GherkinMessages.ConversionSucceeds) {
                assertEquals("ping", (CommandArg.convertToSingle("ping") as Command).name)
            }
        }

        Scenario("An invalid command name is passed") {
            Then(GherkinMessages.ConversationFails) {
                assertTrue(CommandArg.attemptConvert("unknown") is ArgumentResult.Error)
            }
        }

        Scenario("A valid command is passed with different letter casing") {
            Then(GherkinMessages.ConversionSucceeds) {
                assertEquals("ping", (CommandArg.convertToSingle("pInG") as Command).name)
            }
        }
    }
})