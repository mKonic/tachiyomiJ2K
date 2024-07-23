package eu.mkonic.tachiyomi.data.download.model

import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.source.model.Page
import eu.mkonic.tachiyomi.source.online.HttpSource
import rx.subjects.PublishSubject
import kotlin.math.roundToInt

class Download(val source: HttpSource, val manga: Manga, val chapter: Chapter) {

    var pages: List<Page>? = null

    val totalProgress: Int
        get() = pages?.sumOf(Page::progress) ?: 0

    val downloadedImages: Int
        get() = pages?.count { it.status == Page.State.READY } ?: 0

    @Volatile @Transient
    var status: State = State.default
        set(status) {
            field = status
            statusSubject?.onNext(this)
            statusCallback?.invoke(this)
        }

    @Transient private var statusSubject: PublishSubject<Download>? = null

    @Transient private var statusCallback: ((Download) -> Unit)? = null

    val pageProgress: Int
        get() {
            val pages = pages ?: return 0
            return pages.map(Page::progress).sum()
        }

    val progress: Int
        get() {
            val pages = pages ?: return 0
            return pages.map(Page::progress).average().roundToInt()
        }

    fun setStatusSubject(subject: PublishSubject<Download>?) {
        statusSubject = subject
    }

    fun setStatusCallback(f: ((Download) -> Unit)?) {
        statusCallback = f
    }

    enum class State {
        CHECKED,
        NOT_DOWNLOADED,
        QUEUE,
        DOWNLOADING,
        DOWNLOADED,
        ERROR,
        ;

        companion object {
            val default = NOT_DOWNLOADED
        }
    }
}
