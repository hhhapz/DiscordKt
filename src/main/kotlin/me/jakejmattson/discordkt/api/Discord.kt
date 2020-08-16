@file:Suppress("unused")

package me.jakejmattson.discordkt.api

import com.gitlab.kordlib.core.Kord
import com.google.gson.Gson
import me.jakejmattson.discordkt.api.dsl.command.*
import me.jakejmattson.discordkt.api.dsl.configuration.BotConfiguration
import me.jakejmattson.discordkt.internal.utils.diService
import kotlin.reflect.KClass

/**
 * @param repository The repository URL for DiscordKt.
 * @param libraryVersion The current DiscordKt version.
 * @param kotlinVersion The version of Kotlin used by DiscordKt.
 * @param kordVersion The version of Kord used by DiscordKt.
 */
data class Properties(val repository: String,
                      val libraryVersion: String,
                      val kotlinVersion: String,
                      val kordVersion: String)

private val propFile = Properties::class.java.getResource("/library-properties.json").readText()

/**
 * @property configuration All of the current configuration details for this bot.
 * @property properties Properties for the core library.
 */
abstract class Discord {
    abstract val kord: Kord
    abstract val configuration: BotConfiguration
    val properties = Gson().fromJson(propFile, Properties::class.java)!!

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any> getInjectionObjects(a: KClass<A>) = diService[A::class]

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>) =
        Args2(getInjectionObjects(a), getInjectionObjects(b))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>) =
        Args3(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>, d: KClass<D>) =
        Args4(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c), getInjectionObjects(d))

    /** Fetch an object from the DI pool by its type */
    inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any>
        getInjectionObjects(a: KClass<A>, b: KClass<B>, c: KClass<C>, d: KClass<D>, e: KClass<E>) =
        Args5(getInjectionObjects(a), getInjectionObjects(b), getInjectionObjects(c), getInjectionObjects(d), getInjectionObjects(e))
}

internal fun buildDiscordClient(api: Kord, botConfiguration: BotConfiguration) =
    object : Discord() {
        override val configuration = botConfiguration
        override val kord = api
    }