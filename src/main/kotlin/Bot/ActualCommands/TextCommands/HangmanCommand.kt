package Bot.ActualCommands.TextCommands

import Bot.CommandManagement.ICommand
import Bot.Service.Hangman
import Bot.Utils.Constants
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

class HangmanCommand : ICommand {
    var hangman: Hangman? = null
    override fun command(): String {
        return "hangman"
    }

    override fun help(): String {
        return """
            Usage:
            Start: `!hangman/hm`
            Afterwards: `!hangman/hm <your letter>`
            Stop: `!hangman/hm stop`
            Change the maximum error count: `!hangman/hm errorcount <your number>`
            
            """.trimIndent()
    }

    override fun execute(args: Array<String>, event: MessageReceivedEvent) {
        val channel = event.channel
        if (!gameInProgress) {
            if (args.size > 1) {
                if (args[1].lowercase(Locale.getDefault()) == "errorcount") {
                    try {
                        Hangman.errorCountLimit = args[2].toInt()
                        channel.sendMessage("Error count changed!!").queue()
                    } catch (e: NumberFormatException) {
                        channel.sendMessage("Please give a number " + Constants.PENSIVE_CHAIN).queue()
                    }
                }
            } else {
                startGame(channel)
            }
        } else {
            if (args[1].length > 1) {
                if (args[1].lowercase(Locale.getDefault()) == "stop") {
                    gameInProgress = false
                    channel.sendMessage("Game Over!").queue()
                } else if (args[1].lowercase(Locale.getDefault()) == "restart") {
                    try {
                        hangman!!.restart()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (args[1].length == 1) {
                val guess = args[1][0].lowercaseChar()
                hangman!!.currentGuess = guess
            } else {
                startGame(channel)
            }
        }
    }

    private fun startGame(channel: MessageChannel) {
        try {
            if (hangman == null) {
                hangman = Hangman(channel)
                hangman!!.statusCheck()
            } else {
                hangman!!.restart()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAliases(): List<String> {
        return java.util.List.of("hm")
    }

    companion object {
        var gameInProgress = false
    }
}