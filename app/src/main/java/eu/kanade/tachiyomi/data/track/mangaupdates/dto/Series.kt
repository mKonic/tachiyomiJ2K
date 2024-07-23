package eu.mkonic.tachiyomi.data.track.mangaupdates.dto

import kotlinx.serialization.Serializable

@Serializable
data class Series(
    val id: Long? = null,
    val title: String? = null,
)
