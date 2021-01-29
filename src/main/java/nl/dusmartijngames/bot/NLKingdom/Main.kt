package nl.dusmartijngames.bot.NLKingdom

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import nl.dusmartijngames.bot.NLKingdom.commands.admin.EmbedCommand
import nl.dusmartijngames.bot.NLKingdom.config.Config;
import nl.dusmartijngames.bot.NLKingdom.handlers.RoleHandler
import nl.dusmartijngames.bot.NLKingdom.handlers.SupportHandler
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.security.auth.login.LoginException

class Main {

    init {

        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)

        val commandManager = CommandManager()
        val listener = Listener(commandManager)
        val supportHandler = SupportHandler();
        val roleHandler = RoleHandler()
        val waiter = EventWaiter()

        try {
            println("$currentTime Booting")
            val jda = JDABuilder.createDefault(
                Config.get("token2"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ROLE_TAGS, CacheFlag.VOICE_STATE)
                .addEventListeners(listener)
                .addEventListeners(roleHandler)
                .addEventListeners(supportHandler)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("over the server"))
                .build().awaitReady()

            val builder = CommandClientBuilder()

            builder.setPrefix(Config.get("prefix"))
            builder.setOwnerId("229633170004246530")
            builder.addCommand(EmbedCommand(waiter))

            val client = builder.build()

            jda.addEventListener(client)

            var file = File("transcripts/")

            if(!file.exists()) {
                file.mkdir()
            }
            println("$currentTime Running")
        } catch (e: LoginException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    companion object {
        @JvmStatic fun main(args:Array<String>) {
            Main()
        }
    }
}