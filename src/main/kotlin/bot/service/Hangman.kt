package bot.service

import bot.utils.Constants
import bot.utils.isAlphabetic
import bot.utils.writeLine
import net.dv8tion.jda.api.entities.MessageChannel
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess

const val wordsFile = "data/words_hu.txt"

class Hangman(
    val channel: MessageChannel,
) {
    private val LOGGER = LoggerFactory.getLogger(Hangman::class.java)

    var gameInProgress = false

    private val wordList = File(wordsFile).readLines().filter{ it.isNotBlank() }.map { it.lowercase() }.toList()

    private var currentWord: String = ""
    private var maskedWord: String = ""

    // todo: this may not be needed
    private var currentGuess = 0.toChar()
        private set(value) {
            if (value.isAlphabetic()) {
                field = value.lowercaseChar()
            } else {
                throw RuntimeException("Provided character $value is not an alphabetic character.")
            }
        }

    private var errorCount = 0
    private val guessedLetters = mutableSetOf<Char>()

    @Throws(IllegalArgumentException::class)
    fun chooseWord() {
        currentWord = wordList.random()
        require (currentWord.isNotBlank()) { "Couldn't fetch a word from the list!" }
        LOGGER.info("The chosen word is $currentWord")
        maskedWord = currentWord.map { if (it.isAlphabetic()) '-' else it }.joinToString("")
    }

    private val wordRevealed get() = (maskedWord == currentWord)
    private val wordNotYetRevealed get() = !wordRevealed

    fun guess(guessedLetter: Char) {
        if (guessedLetter.lowercaseChar() in guessedLetters) {
            channel.sendMessage("Letter was already guessed, try another one.").queue()
            return
        }

        try {
            this.currentGuess = guessedLetter
        } catch (e: RuntimeException) {
            channel.sendMessage(e.message.orEmpty()).queue()
            return
        }

        var guessIsCorrect = false
        val maskedChars = maskedWord.toCharArray()
        currentWord.forEachIndexed { index, char ->
            if (currentGuess == char) {
                maskedChars[index] = currentGuess
                guessIsCorrect = true
            }
        }
        if (!guessIsCorrect) errorCount++
        maskedWord = maskedChars.joinToString(separator = "")

        if (wordNotYetRevealed) {
            guessedLetters += currentGuess

            if (errorCount < errorCountLimit) {
                channel.sendMessage("""
                    |Error count: $errorCount
                    |$maskedWord
                    |Guess another letter!
                    |Already guessed letters: ${guessedLetters.joinToString()}
                """.trimMargin()).queue()
            } else { // Game over
                channel.sendMessage("""
                    |$maskedWord
                    |You lost! ${Constants.SWEAT}${Constants.SWEAT}
                    |The word was $currentWord!
                """.trimMargin()).queue()
                end()
            }
        } else {
            channel.sendMessage("""
                |$maskedWord
                |You won! ${Constants.HUGGING}${Constants.HUGGING}${Constants.HUGGING}
            """.trimMargin()).queue()
            end()
        }
    }

    @Throws(Exception::class)
    fun start() {
        gameInProgress = true
        chooseWord()
        errorCount = 0
        channel.sendMessage("""
            |$maskedWord
            |Guess a letter!
        """.trimMargin()).queue()
    }

    @Throws(Exception::class)
    fun restart() {
        channel.sendMessage("Restart:\n")
        start()
    }

    fun end() {
        gameInProgress = false
        guessedLetters.clear()
    }

    private fun stop() {
        channel.sendMessage("Crashing this VM, with no survivors.").queue()
        exitProcess(1)
    }

    //segitseg(keresettSzo)   shows a letter for 33% of the error points
    fun help(kapottSzo: String, hatterSzo: String): String {
        TODO()
        var hatterSzo = hatterSzo
        var vanHianyzoBetu = false
        var randomNumber = nextInt(kapottSzo.length - 1)
        for (i in kapottSzo.indices) {
            if (hatterSzo[i] == '-') {
                vanHianyzoBetu = true
                break
            }
        }
        if (vanHianyzoBetu) {
            while (hatterSzo[randomNumber] != '-') {
                randomNumber = nextInt(kapottSzo.length - 1)
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

    // todo
    @Throws(Exception::class)
    fun addWord(word: String) = File(wordsFile).writeLine(word)

    companion object {
        var errorCountLimit = 11
    }
}