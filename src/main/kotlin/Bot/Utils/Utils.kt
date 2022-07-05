package Bot.Utils

import Bot.Service.Translator
import com.google.gson.JsonParser
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.VoiceChannel
import org.apache.commons.lang.StringEscapeUtils
import java.io.File
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

fun createBasicReactionEmbed(
    title: String,
    fields: List<String>,
): EmbedBuilder = EmbedBuilder().apply {
    setTitle(StringEscapeUtils.unescapeHtml(title))
    fields.forEachIndexed { index, f ->
        this.addField("${index + 1}\u20E3 ${StringEscapeUtils.unescapeHtml(f)}", "", false)
    }
}

fun createBasicReactionEmbed(
    title: String,
    fields: List<String>,
    translateTo: String,
): EmbedBuilder = EmbedBuilder().apply {
    setTitle(Translator.getInstance().translate("en", translateTo, StringEscapeUtils.unescapeHtml(title)))
    fields.forEachIndexed { index, f ->
        this.addField(
            "${(index + 1)}\u20E3 " + Translator.getInstance()
                .translate("en", translateTo, StringEscapeUtils.unescapeHtml(f)), "", false
        )
    }
}

private fun getCharForNumber(i: Int): String? = if (i in 1..26) (i + 'A'.code).toChar().toString() else null

fun getJsonFromAPI(url: String): String = StringBuilder().apply {
    (URL(url).openConnection() as HttpURLConnection).runCatching {
        requestMethod = "GET"
        if (responseCode == 200) {
            val data = this.inputStream.bufferedReader().readText()
            disconnect()
            return data
        } else {
            throw RuntimeException("Response: $responseCode")
        }
    }
}.toString()

fun getJsonPropertyValue(json: String, value: String): String =
    JsonParser().parse(json).asJsonObject[value].asString

fun sendHttpRequest(
    url: String,
    type: String,
    body: String,
): Int = (URL(url).openConnection() as HttpURLConnection).runCatching {
    requestMethod = type
    addRequestProperty("Content-Type", "application/json")
    setRequestProperty("Content-length", body.length.toString())
    doOutput = true
    outputStream.write(body.toByteArray(StandardCharsets.UTF_8))
    val code = responseCode

    disconnect()

    return code
}.getOrThrow<Int>()

fun VoiceChannel.isActualUserLeftInVoiceChannel() = this.members.any { !it.user.isBot }

fun VoiceChannel.noActualUserLeftInVoiceChannel() = !this.isActualUserLeftInVoiceChannel()

fun getPropertiesFromResourceFile(fileInResourcesFolder: String): Properties {
    val reader = FileReader("src/main/resources/$fileInResourcesFolder")
    return Properties().apply {
        load(reader)
    }
}

fun File.writeLine(line: String) = this.bufferedWriter().use {
    it.write(line)
    it.newLine()
}

fun Char.isAlphabetic() = Character.isAlphabetic(this.code)
