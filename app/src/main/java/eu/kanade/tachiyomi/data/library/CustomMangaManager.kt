package eu.mkonic.tachiyomi.data.library

import android.content.Context
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.database.models.MangaImpl
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class CustomMangaManager(val context: Context) {

    private val editJson = File(context.getExternalFilesDir(null), "edits.json")

    private var customMangaMap = mutableMapOf<Long, Manga>()

    init {
        fetchCustomData()
    }

    companion object {
        fun Manga.toJson(): MangaJson {
            return MangaJson(
                id!!,
                title,
                author,
                artist,
                description,
                genre?.split(", ")?.toTypedArray(),
                status.takeUnless { it == -1 },
            )
        }
    }

    fun getManga(manga: Manga): Manga? = customMangaMap[manga.id]

    private fun fetchCustomData() {
        if (!editJson.exists() || !editJson.isFile) return

        val json = try {
            Json.decodeFromString<MangaList>(editJson.bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            null
        } ?: return

        val mangasJson = json.mangas ?: return
        customMangaMap = mangasJson.mapNotNull { mangaObject ->
            val id = mangaObject.id ?: return@mapNotNull null
            id to mangaObject.toManga()
        }.toMap().toMutableMap()
    }

    fun saveMangaInfo(manga: MangaJson) {
        val mangaId = manga.id ?: return
        if (manga.title == null &&
            manga.author == null &&
            manga.artist == null &&
            manga.description == null &&
            manga.genre == null &&
            (manga.status ?: -1) == -1
        ) {
            customMangaMap.remove(mangaId)
        } else {
            customMangaMap[mangaId] = manga.toManga()
        }
        saveCustomInfo()
    }

    private fun saveCustomInfo() {
        val jsonElements = customMangaMap.values.map { it.toJson() }
        if (jsonElements.isNotEmpty()) {
            editJson.delete()
            editJson.writeText(Json.encodeToString(MangaList(jsonElements)))
        }
    }

    @Serializable
    data class MangaList(
        val mangas: List<MangaJson>? = null,
    )

    @Serializable
    data class MangaJson(
        var id: Long? = null,
        val title: String? = null,
        val author: String? = null,
        val artist: String? = null,
        val description: String? = null,
        val genre: Array<String>? = null,
        val status: Int? = null,
    ) {

        fun toManga() = MangaImpl().apply {
            id = this@MangaJson.id
            title = this@MangaJson.title ?: ""
            author = this@MangaJson.author
            artist = this@MangaJson.artist
            description = this@MangaJson.description
            genre = this@MangaJson.genre?.joinToString(", ")
            status = this@MangaJson.status ?: -1
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as MangaJson
            if (id != other.id) return false
            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }
}
