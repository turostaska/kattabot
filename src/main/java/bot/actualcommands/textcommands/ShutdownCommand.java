package bot.actualcommands.textcommands;

import bot.commandmanagement.GeneralCommandManager;
import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static bot.utils.UtilsKt.getPropertiesFromResourceFile;

public class ShutdownCommand implements ICommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownCommand.class);

    @Override
    public String command() {
        return "shutdown";
    }

    @Override
    public String help() {
        return "If called by the owner, shuts down Battabot";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            event.getChannel().sendMessage("Can't shutdown while in a voice channel " + Constants.PENSIVE).queue();
        } else {
            LOGGER.info("Shutting down due to command");
            if (event.getAuthor().getId().equals(getOwner())) {
                GeneralCommandManager.guildAudioManager.getPlayerManager().shutdown();
                event.getMessage().getJDA().shutdown();
            }
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("sd");
    }

    private String getOwner() {
        return getPropertiesFromResourceFile("config/ConfigurationKeys.properties").getProperty("OwnerId");
    }
}
