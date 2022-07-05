package bot

import bot.commandmanagement.GeneralCommandManager
import bot.utils.Constants
import bot.utils.noActualUserLeftInVoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MyEventListener : ListenerAdapter() {
    private val generalCommandManager = GeneralCommandManager()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot)
            return

        val content = event.message.contentRaw
        if (content.startsWith(Constants.PREFIX))
            generalCommandManager.handleCommand(event)
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        event.jda.audioManagers.firstOrNull()?.let {
            if (it.isConnected)
                if (event.channelLeft.noActualUserLeftInVoiceChannel())
                    it.closeAudioConnection()
        }
    }
}
