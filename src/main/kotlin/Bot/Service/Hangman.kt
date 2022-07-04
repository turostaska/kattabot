package Bot.Service

import Bot.ActualCommands.TextCommands.HangmanCommand
import Bot.Utils.Constants
import Bot.Utils.writeLine
import net.dv8tion.jda.api.entities.MessageChannel
import java.io.*
import java.util.*
import kotlin.system.exitProcess

const val wordsFile = "data/words_hu.txt"

class Hangman(
    val channel: MessageChannel,
) {
    private var status = 0

    private val wordList = File(wordsFile).readLines().filter{ it.isNotBlank() }.map { it.lowercase() }.toList()

    private var currentWord: String = ""
    private var maskedWord: String = ""

    var currentGuess = 0.toChar()
        set(value) {
            if (Character.isAlphabetic(currentGuess.code)) {
                field = value.lowercaseChar()
                guess()
            } else {
                channel.sendMessage("Gonna need a letter!").queue()
            }
        }

    private var errorCount = 0
    private val guessedLetters = mutableSetOf<Char>()

    init {
        HangmanCommand.gameInProgress = true
    }

    @Throws(RuntimeException::class)
    fun chooseWord() {
        currentWord = wordList.random()
        require (currentWord.isNotBlank()) { "Couldn't fetch a word from the list!" }
        maskedWord = "-".repeat(currentWord.length)
    }

    private val wordRevealed get() = (maskedWord == currentWord)
    private val wordNotYetRevealed get() = !wordRevealed

    private fun guess() {
        // todo: check if guess in guessedLetters
        // todo: guess as parameter

        var guessIsCorrect = false
        val maskedChars = maskedWord.toCharArray()
        currentWord.forEachIndexed { index, char ->
            if (currentGuess == char) {
                maskedChars[index] = currentGuess
                guessIsCorrect = true
            }
        }
        if (!guessIsCorrect) errorCount++
        maskedWord = maskedChars.toString()

        if (wordNotYetRevealed) {
            guessedLetters += currentGuess
            val letters = guessedLetters.joinToString()
            if (errorCount < errorCountLimit) {
                channel.sendMessage("""
                    Error count: $errorCount
                    $maskedWord
                    Guess another letter!
                    Already guessed letters: $letters
                """).queue()
            } else { // Game over
                channel.sendMessage("""
                    $maskedWord
                    You lost! ${Constants.SWEAT}${Constants.SWEAT}
                    The word was $currentWord!
                """).queue()
                HangmanCommand.gameInProgress = false
                guessedLetters.clear()
            }
        } else {
            channel.sendMessage("""
                $maskedWord
                You won! ${Constants.HUGGING}${Constants.HUGGING}${Constants.HUGGING}
            """).queue()
            HangmanCommand.gameInProgress = false
            guessedLetters.clear()
        }
    }

    @Throws(Exception::class)
    fun statusCheck() {
        when (status) {
            0 -> start()
            1 -> stop()
            2 -> restart()
        }
    }

    @Throws(Exception::class)
    fun start() {
        chooseWord()
        errorCount = 0
        channel.sendMessage("""
            $maskedWord
            Guess a letter!
        """).queue()
    }

    @Throws(Exception::class)
    fun restart() {
        HangmanCommand.gameInProgress = true
        errorCount = 0
        channel.sendMessage("Restart:\n")
        status = 0
        statusCheck()
    }

    private fun stop() {
        channel.sendMessage("Crashing this VM, with no survivors.").queue()
        exitProcess(1)
    }

    //segitseg(keresettSzo)   shows a letter for 33% of the error points
    fun segitseg(kapottSzo: String, hatterSzo: String): String {
        var hatterSzo = hatterSzo
        var vanHianyzoBetu = false
        var randomNumber = randomnumber(kapottSzo.length - 1)
        for (i in 0 until kapottSzo.length) {
            if (hatterSzo[i] == '-') {
                vanHianyzoBetu = true
                break
            }
        }
        if (vanHianyzoBetu) {
            while (hatterSzo[randomNumber] != '-') {
                randomNumber = randomnumber(kapottSzo.length - 1)
            }
        } else {
            return hatterSzo
        }
        for (i in kapottSzo.indices) {
            if (kapottSzo[randomNumber] == kapottSzo[i]) {
                val hatterszoChar = hatterSzo.toCharArray()
                hatterszoChar[i] = kapottSzo[i]
                hatterSzo = String(hatterszoChar)
            }
        }
        return hatterSzo.lowercase(Locale.getDefault())
    }

    @Throws(Exception::class)
    fun addWord(word: String) = File(wordsFile).writeLine(word)

    companion object {
        @JvmField
        var errorCountLimit = 11
    }
}