package eu.mkonic.tachiyomi.source.online

import android.net.Uri
import eu.mkonic.tachiyomi.data.database.DatabaseHelper
import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.database.models.MangaImpl
import eu.mkonic.tachiyomi.network.NetworkHelper
import eu.mkonic.tachiyomi.source.model.SChapter
import uy.kohesive.injekt.injectLazy

abstract class DelegatedHttpSource {

    var delegate: HttpSource? = null
    abstract val domainName: String

    protected val db: DatabaseHelper by injectLazy()

    protected val network: NetworkHelper by injectLazy()

    abstract fun canOpenUrl(uri: Uri): Boolean
    abstract fun chapterUrl(uri: Uri): String?
    open fun pageNumber(uri: Uri): Int? = uri.pathSegments.lastOrNull()?.toIntOrNull()
    abstract suspend fun fetchMangaFromChapterUrl(uri: Uri): Triple<Chapter, Manga, List<SChapter>>?

    protected open suspend fun getMangaInfo(url: String): Manga? {
        val id = delegate?.id ?: return null
        val manga = Manga.create(url, "", id)
        val networkManga = delegate?.getMangaDetails(manga.copy()) ?: return null
        val newManga = MangaImpl().apply {
            this.url = url
            title = try { networkManga.title } catch (e: Exception) { "" }
            source = id
        }
        newManga.copyFrom(networkManga)
        return newManga
    }

    suspend fun getChapters(url: String): List<SChapter>? {
        val id = delegate?.id ?: return null
        val manga = Manga.create(url, "", id)
        return delegate?.getChapterList(manga)
    }
}
