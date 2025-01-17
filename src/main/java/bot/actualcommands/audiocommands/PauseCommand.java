package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.lavaplayer.GuildAudioManager;
import bot.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static bot.actualcommands.audiocommands.VolumeCommand.botAndCallerAreInTheSameVoiceChannel;

public class PauseCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public PauseCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "pause";
    }

    @Override
    public String help() {
        return "Pauses the currently playing track";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (event.getGuild().getAudioManager().isConnected() && botAndCallerAreInTheSameVoiceChannel(event.getMember())) {
            Guild guild = event.getGuild();
            guildAudioManager.pause(guild, true);
        } else {
            event.getChannel().sendMessage("You are not in the same voice channel as the bot " + Constants.PENSIVE).queue();
        }
    }
}
