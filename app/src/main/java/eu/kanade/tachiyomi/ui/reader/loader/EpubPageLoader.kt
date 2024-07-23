package eu.mkonic.tachiyomi.ui.reader.loader

import eu.mkonic.tachiyomi.source.model.Page
import eu.mkonic.tachiyomi.ui.reader.model.ReaderPage
import eu.mkonic.tachiyomi.util.storage.EpubFile
import java.io.File

/**
 * Loader used to load a chapter from a .epub file.
 */
class EpubPageLoader(file: File) : PageLoader() {

    /**
     * The epub file.
     */
    private val epub = EpubFile(file)

    /**
     * Recycles this loader and the open zip.
     */
    override fun recycle() {
        super.recycle()
        epub.close()
    }

    /**
     * Returns the pages found on this zip archive ordered with a natural comparator.
     */
    override suspend fun getPages(): List<ReaderPage> {
        return epub.getImagesFromPages()
            .mapIndexed { i, path ->
                val streamFn = { epub.getInputStream(epub.getEntry(path)!!) }
                ReaderPage(i).apply {
                    stream = streamFn
                    status = Page.State.READY
                }
            }
    }

    /**
     * No additional action required to load the page
     */
    override suspend fun loadPage(page: ReaderPage) {
        check(!isRecycled)
    }
}
