package bot.actualcommands.textcommands

import bot.commandmanagement.ICommand
import bot.service.Hangman
import bot.utils.Constants
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

const val ERROR_COUNT_COMMAND = "errorcount"

class HangmanCommand : ICommand {
    private var hangman: Hangman? = null
    override fun command() = "hangman"

    override fun help() = """
        Usage:
        Start: `!hangman/hm`
        Afterwards: `!hangman/hm <your letter>`
        Stop: `!hangman/hm stop`
        Change the maximum error count: `!hangman/hm $ERROR_COUNT_COMMAND <amount>`
        
        """.trimIndent()

    override fun execute(args: Array<String>, event: MessageReceivedEvent) {
        val channel = event.channel
        val command = args.getOrNull(1)?.lowercase()
        val amount = args.getOrNull(2)?.toInt()

        if (!gameInProgress) {
            if (command != null) {
                changeNumOfMaxErrors(command, amount, channel)
            } else {
                startGame(channel)
            }
        } else { // game in progress
            if (command == null) {
                startGame(channel)
            } else if (command.length > 1) {
                when (command) {
                    "stop" -> {
                        hangman?.end()
                        channel.sendMessage("Game Over!").queue()
                    }
                    "restart" -> hangman?.restart()
                }
            } else if (command.length == 1) {
                val guess = command.first().lowercaseChar()
                hangman?.guess(guess)
            }
        }
    }

    private fun changeNumOfMaxErrors(
        command: String,
        amount: Int?,
        channel: MessageChannel,
    ) {
        if (command == ERROR_COUNT_COMMAND && amount != null) {
            Hangman.errorCountLimit = amount
            channel.sendMessage("Max number of errors changed to $amount!").queue()
        } else {
            channel.sendMessage("Please give a number ${Constants.PENSIVE_CHAIN}").queue()
        }
    }

    private fun startGame(channel: MessageChannel) {
        try {
            if (hangman == null) {
                hangman = Hangman(channel)
                hangman?.start()
            } else {
                hangman?.restart()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAliases() = listOf("hm")

    private val gameInProgress get() = hangman?.gameInProgress ?: false
}
