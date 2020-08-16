@file:Suppress("unused")

package me.jakejmattson.discordkt.internal.utils

import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.*
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.menu.Menu
import me.jakejmattson.discordkt.api.extensions.stdlib.sanitiseMentions

internal interface Responder {
    val discord: Discord
    val channel: MessageChannel

    fun unsafeRespond(message: String) = GlobalScope.launch { chunkRespond(message) }
    fun respond(message: String) = GlobalScope.launch { chunkRespond(message.sanitiseMentions(discord)) }
    fun respond(construct: EmbedBuilder.() -> Unit) = GlobalScope.launch { channel.createEmbed { construct.invoke(this) } }
    fun respond(message: String, construct: EmbedBuilder.() -> Unit) = GlobalScope.launch {
        channel.createMessage {
            content = message
            construct.invoke(embed!!)
        }
    }

    fun respond(menu: Menu) = GlobalScope.launch { menu.build(channel) }

    private suspend fun chunkRespond(message: String) {
        require(message.isNotEmpty()) { "Cannot send an empty message." }
        message.chunked(2000).forEach { channel.createMessage(it) }
    }
}
