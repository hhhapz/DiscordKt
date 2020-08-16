package me.jakejmattson.discordkt.api.dsl.configuration

import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.*
import me.jakejmattson.discordkt.api.dsl.command.Command

/**
 * @suppress Used in sample
 */
data class VisibilityContext(val command: Command, val user: User, val channel: MessageChannelBehavior, val guild: Guild?)