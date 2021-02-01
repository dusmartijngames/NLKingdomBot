package nl.dusmartijngames.bot.NLKingdom

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import nl.dusmartijngames.bot.NLKingdom.config.Config
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

        try {
            println("$currentTime Booting")
            JDABuilder.createDefault(
                Config.get("token"),
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
                .build().awaitReady()

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