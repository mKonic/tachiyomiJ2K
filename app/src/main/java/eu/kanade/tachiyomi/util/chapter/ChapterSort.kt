package eu.mkonic.tachiyomi.util.chapter

import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.util.chapter.ChapterFilter.Companion.filterChaptersByScanlators
import eu.mkonic.tachiyomi.util.lang.compareToCaseInsensitiveNaturalOrder
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ChapterSort(val manga: Manga, val chapterFilter: ChapterFilter = Injekt.get(), val preferences: PreferencesHelper = Injekt.get()) {

    fun <T : Chapter> getChaptersSorted(
        rawChapters: List<T>,
        andFiltered: Boolean = true,
        filterForReader: Boolean = false,
        currentChapter: T? = null,
    ): List<T> {
        val chapters = when {
            filterForReader -> chapterFilter.filterChaptersForReader(
                rawChapters,
                manga,
                currentChapter,
            )
            andFiltered -> chapterFilter.filterChapters(rawChapters, manga)
            else -> rawChapters
        }

        return chapters.sortedWith(sortComparator())
    }

    fun <T : Chapter> getNextUnreadChapter(rawChapters: List<T>, andFiltered: Boolean = true): T? {
        val chapters = when {
            andFiltered -> chapterFilter.filterChapters(rawChapters, manga)
            else -> rawChapters.filterChaptersByScanlators(manga)
        }

        return chapters.sortedWith(sortComparator(true)).find { !it.read }
    }

    fun <T : Chapter> sortComparator(ignoreAsc: Boolean = false): Comparator<T> {
        val sortDescending = !ignoreAsc && manga.sortDescending(preferences)
        val sortFunction: (T, T) -> Int =
            when (manga.chapterOrder(preferences)) {
                Manga.CHAPTER_SORTING_SOURCE -> when (sortDescending) {
                    true -> { c1, c2 -> c1.source_order.compareTo(c2.source_order) }
                    false -> { c1, c2 -> c2.source_order.compareTo(c1.source_order) }
                }
                Manga.CHAPTER_SORTING_NUMBER -> when (sortDescending) {
                    true -> { c1, c2 -> c2.chapter_number.toString().compareToCaseInsensitiveNaturalOrder(c1.chapter_number.toString()) }
                    false -> { c1, c2 -> c1.chapter_number.toString().compareToCaseInsensitiveNaturalOrder(c2.chapter_number.toString()) }
                }
                Manga.CHAPTER_SORTING_UPLOAD_DATE -> when (sortDescending) {
                    true -> { c1, c2 -> c2.date_upload.compareTo(c1.date_upload) }
                    false -> { c1, c2 -> c1.date_upload.compareTo(c2.date_upload) }
                }
                else -> { c1, c2 -> c1.source_order.compareTo(c2.source_order) }
            }
        return Comparator(sortFunction)
    }
}
