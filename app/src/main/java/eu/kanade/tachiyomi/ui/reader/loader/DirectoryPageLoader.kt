package eu.mkonic.tachiyomi.ui.reader.loader

import eu.mkonic.tachiyomi.source.model.Page
import eu.mkonic.tachiyomi.ui.reader.model.ReaderPage
import eu.mkonic.tachiyomi.util.lang.compareToCaseInsensitiveNaturalOrder
import eu.mkonic.tachiyomi.util.system.ImageUtil
import java.io.File
import java.io.FileInputStream

/**
 * Loader used to load a chapter from a directory given on [file].
 */
class DirectoryPageLoader(val file: File) : PageLoader() {

    /**
     * Returns the pages found on this directory ordered with a natural comparator.
     */
    override suspend fun getPages(): List<ReaderPage> {
        return file.listFiles()
            ?.filter { !it.isDirectory && ImageUtil.isImage(it.name) { FileInputStream(it) } }
            ?.sortedWith { f1, f2 -> f1.name.compareToCaseInsensitiveNaturalOrder(f2.name) }
            ?.mapIndexed { i, file ->
                val streamFn = { FileInputStream(file) }
                ReaderPage(i).apply {
                    stream = streamFn
                    status = Page.State.READY
                }
            } ?: emptyList()
    }

    /**
     * No additional action required to load the page
     */
    override suspend fun loadPage(page: ReaderPage) {}
}
