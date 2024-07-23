package eu.mkonic.tachiyomi.ui.reader.loader

import android.app.Application
import android.net.Uri
import com.hippo.unifile.UniFile
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.download.DownloadManager
import eu.mkonic.tachiyomi.data.download.DownloadProvider
import eu.mkonic.tachiyomi.source.Source
import eu.mkonic.tachiyomi.source.model.Page
import eu.mkonic.tachiyomi.ui.reader.model.ReaderChapter
import eu.mkonic.tachiyomi.ui.reader.model.ReaderPage
import uy.kohesive.injekt.injectLazy
import java.io.File

/**
 * Loader used to load a chapter from the downloaded chapters.
 */
class DownloadPageLoader(
    private val chapter: ReaderChapter,
    private val manga: Manga,
    private val source: Source,
    private val downloadManager: DownloadManager,
    private val downloadProvider: DownloadProvider,
) : PageLoader() {

    // Needed to open input streams
    private val context: Application by injectLazy()

    private var zipPageLoader: ZipPageLoader? = null

    override fun recycle() {
        super.recycle()
        zipPageLoader?.recycle()
    }

    /**
     * Returns the pages found on this downloaded chapter.
     */
    override suspend fun getPages(): List<ReaderPage> {
        val dbChapter = chapter.chapter
        val chapterPath = downloadProvider.findChapterDir(dbChapter, manga, source)
        return if (chapterPath?.isFile == true) {
            getPagesFromArchive(chapterPath)
        } else {
            getPagesFromDirectory()
        }
    }

    private suspend fun getPagesFromArchive(chapterPath: UniFile): List<ReaderPage> {
        val loader = ZipPageLoader(File(chapterPath.filePath!!)).also { zipPageLoader = it }
        return loader.getPages()
    }

    private fun getPagesFromDirectory(): List<ReaderPage> {
        val pages = downloadManager.buildPageList(source, manga, chapter.chapter)
        return pages.map { page ->
            ReaderPage(page.index, page.url, page.imageUrl, stream = {
                context.contentResolver.openInputStream(page.uri ?: Uri.EMPTY)!!
            },).apply {
                status = Page.State.READY
            }
        }
    }

    override suspend fun loadPage(page: ReaderPage) {
        zipPageLoader?.loadPage(page)
    }
}
