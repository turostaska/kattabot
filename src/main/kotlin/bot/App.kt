package bot

import bot.utils.Constants
import bot.utils.getPropertiesFromResourceFile
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.LoggerFactory
import java.util.*

object App : ListenerAdapter() {
    private val LOGGER = LoggerFactory.getLogger(App::class.java)

    private val intents: List<GatewayIntent> = listOf(
        GatewayIntent.GUILD_EMOJIS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_VOICE_STATES,
    )

    private val token: String = getPropertiesFromResourceFile("config/ConfigurationKeys.properties")
        .getProperty("DiscordToken")

    init {
        require(token.isNotBlank()) { "Discord bot token is missing!" }
    }

    private val jda: JDA = JDABuilder.create(token, intents)
        .setMemberCachePolicy(MemberCachePolicy.ALL).build().awaitReady().apply {
            this.presence.activity = Activity.watching("Help command: ${Constants.PREFIX}help")
            this.addEventListener(MyEventListener())
        }

    val defaultChannels = jda.guilds.associateWith { guild ->
        guild.textChannels.firstOrNull {
            it.name.lowercase(Locale.getDefault()).contains("general")
        } ?: guild.textChannels.first()
    }
}

fun main() = try {
    Constants.defaultTextChannels = App.defaultChannels
} catch (e: Exception) {
    e.printStackTrace()
}
