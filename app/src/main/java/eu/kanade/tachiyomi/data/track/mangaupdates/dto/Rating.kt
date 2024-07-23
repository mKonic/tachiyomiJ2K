package eu.mkonic.tachiyomi.data.track.mangaupdates.dto

import eu.mkonic.tachiyomi.data.database.models.Track
import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    val rating: Float? = null,
)

fun Rating.copyTo(track: Track): Track {
    return track.apply {
        this.score = rating ?: 0f
    }
}
