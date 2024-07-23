package eu.mkonic.tachiyomi.util.chapter

import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.download.DownloadManager
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ChapterFilter(val preferences: PreferencesHelper = Injekt.get(), val downloadManager: DownloadManager = Injekt.get()) {

    /** filters chapters based on the manga values */
    fun <T : Chapter> filterChapters(chapters: List<T>, manga: Manga): List<T> {
        val readEnabled = manga.readFilter(preferences) == Manga.CHAPTER_SHOW_READ
        val unreadEnabled = manga.readFilter(preferences) == Manga.CHAPTER_SHOW_UNREAD
        val downloadEnabled = manga.downloadedFilter(preferences) == Manga.CHAPTER_SHOW_DOWNLOADED
        val notDownloadEnabled = manga.downloadedFilter(preferences) == Manga.CHAPTER_SHOW_NOT_DOWNLOADED
        val bookmarkEnabled = manga.bookmarkedFilter(preferences) == Manga.CHAPTER_SHOW_BOOKMARKED
        val notBookmarkEnabled = manga.bookmarkedFilter(preferences) == Manga.CHAPTER_SHOW_NOT_BOOKMARKED

        // if none of the filters are enabled skip the filtering of them
        val filteredChapters = chapters.filterChaptersByScanlators(manga)
        return if (readEnabled || unreadEnabled || downloadEnabled || notDownloadEnabled || bookmarkEnabled || notBookmarkEnabled) {
            filteredChapters.filter {
                if (readEnabled && it.read.not() ||
                    (unreadEnabled && it.read) ||
                    (bookmarkEnabled && it.bookmark.not()) ||
                    (notBookmarkEnabled && it.bookmark) ||
                    (downloadEnabled && downloadManager.isChapterDownloaded(it, manga).not()) ||
                    (notDownloadEnabled && downloadManager.isChapterDownloaded(it, manga))
                ) {
                    return@filter false
                }
                return@filter true
            }
        } else {
            filteredChapters
        }
    }

    /** filter chapters for the reader */
    fun <T : Chapter> filterChaptersForReader(chapters: List<T>, manga: Manga, selectedChapter: T? = null): List<T> {
        var filteredChapters = chapters.filterChaptersByScanlators(manga)
        // if filter prefs aren't enabled don't even filter
        if (!preferences.skipRead() && !preferences.skipFiltered() && !preferences.skipDupe().get()) {
            return filteredChapters
        }

        if (preferences.skipRead()) {
            filteredChapters = filteredChapters.filter { !it.read }
        }
        if (preferences.skipFiltered()) {
            filteredChapters = filterChapters(filteredChapters, manga)
        }
        if (preferences.skipDupe().get()) {
            filteredChapters = filteredChapters.groupBy { it.chapter_number }
                .map { (_, chapters) ->
                    chapters.find { it.id == selectedChapter?.id }
                        ?: chapters.find { it.scanlator == selectedChapter?.scanlator }
                        ?: chapters.find {
                            val mainScans = it.scanlator?.split(ChapterUtil.scanlatorSeparator)
                                ?: return@find false
                            val currScans =
                                selectedChapter?.scanlator?.split(ChapterUtil.scanlatorSeparator)
                                    ?: return@find false
                            mainScans.any { scanlator -> currScans.contains(scanlator) }
                        }
                        ?: chapters.first()
                }
        }

        // add the selected chapter to the list in case it was filtered out
        if (selectedChapter?.id != null) {
            val find = filteredChapters.find { it.id == selectedChapter.id }
            if (find == null) {
                val mutableList = filteredChapters.toMutableList()
                mutableList.add(selectedChapter)
                filteredChapters = mutableList.toList()
            }
        }

        return filteredChapters
    }

    companion object {
        /** filters chapters for scanlators */
        fun <T : Chapter> List<T>.filterChaptersByScanlators(manga: Manga): List<T> {
            return manga.filtered_scanlators?.let { filteredScanlatorString ->
                val filteredScanlators = ChapterUtil.getScanlators(filteredScanlatorString)
                filter { ChapterUtil.getScanlators(it.scanlator).none { group -> filteredScanlators.contains(group) } }
            } ?: this
        }
    }
}
