package bot.utils

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel

object Constants {
    const val PREFIX = "!"
    const val SMILING_FACE_WITH_3_HEARTS = ":smiling_face_with_3_hearts:"
    const val SLIGHT_SMILE = ":slight_smile:"
    const val PENSIVE = ":pensive:"
    const val PENSIVE_CHAIN = ":pensive: :pensive: :pensive: :thumbsup:"
    const val THINKING = ":thinking:"
    const val TRIUMPH = ":triumph:"
    const val FACE_WITH_RAISED_EYEBROWS = ":face_with_raised_eyebrow:"
    const val SWEAT = ":sweat:"
    const val HUGGING = ":hugging:"

    @JvmStatic
    var defaultTextChannels: Map<Guild, TextChannel>? = null
    set(map) {
        if (defaultTextChannels == null) field = map
    }
}