package eu.mkonic.tachiyomi.util.chapter

import android.content.Context
import android.content.res.ColorStateList
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.ui.manga.chapter.ChapterItem
import eu.mkonic.tachiyomi.util.system.contextCompatColor
import eu.mkonic.tachiyomi.util.system.dpToPx
import eu.mkonic.tachiyomi.util.system.dpToPxEnd
import eu.mkonic.tachiyomi.util.system.getResourceColor
import eu.mkonic.tachiyomi.util.system.timeSpanFromNow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class ChapterUtil {
    companion object {

        private val decimalFormat = DecimalFormat(
            "#.###",
            DecimalFormatSymbols()
                .apply { decimalSeparator = '.' },
        )

        fun relativeDate(chapter: Chapter): String? {
            return when (chapter.date_upload > 0) {
                true -> chapter.date_upload.timeSpanFromNow
                false -> null
            }
        }

        fun setTextViewForChapter(
            textView: TextView,
            chapter: Chapter,
            showBookmark: Boolean = true,
            hideStatus: Boolean = false,
        ) {
            val context = textView.context
            textView.setTextColor(chapterColor(context, chapter, hideStatus))
            if (!hideStatus && showBookmark) {
                setBookmark(textView, chapter)
            }
        }

        private fun setBookmark(textView: TextView, chapter: Chapter) {
            if (chapter.bookmark) {
                val context = textView.context
                val drawable = VectorDrawableCompat.create(
                    textView.resources,
                    R.drawable.ic_bookmark_24dp,
                    context.theme,
                )
                drawable?.setBounds(0, 0, textView.textSize.toInt(), textView.textSize.toInt())
                textView.setCompoundDrawablesRelative(
                    drawable,
                    null,
                    null,
                    null,
                )
                TextViewCompat.setCompoundDrawableTintList(
                    textView,
                    ColorStateList.valueOf(bookmarkedColor(context)),
                )
                textView.compoundDrawablePadding = 3.dpToPx
                textView.translationX = (-2f).dpToPxEnd(textView.resources)
            } else {
                textView.setCompoundDrawablesRelative(null, null, null, null)
                textView.translationX = 0f
            }
        }

        fun chapterColor(context: Context, chapter: Chapter, hideStatus: Boolean = false): Int {
            return when {
                hideStatus -> unreadColor(context)
                chapter.read -> readColor(context)
                else -> unreadColor(context)
            }
        }

        fun readColor(context: Context, chapter: Chapter): Int {
            return when {
                chapter.read -> readColor(context)
                else -> unreadColor(context)
            }
        }

        fun bookmarkColor(context: Context, chapter: Chapter): Int {
            return when {
                chapter.bookmark -> bookmarkedColor(context)
                else -> readColor(context)
            }
        }

        private fun readColor(context: Context): Int = context.contextCompatColor(R.color.read_chapter)

        private fun unreadColor(context: Context): Int = context.getResourceColor(R.attr.colorOnBackground)

        private fun bookmarkedColor(context: Context): Int = context.getResourceColor(R.attr.colorSecondary)

        private val volumeRegex = Regex("""(vol|volume)\.? *([0-9]+)?""", RegexOption.IGNORE_CASE)
        private val seasonRegex = Regex("""(Season |S)([0-9]+)?""")

        fun getGroupNumber(chapter: Chapter): Int? {
            val groups = volumeRegex.find(chapter.name)?.groups
            if (groups != null) return groups[2]?.value?.toIntOrNull()
            val seasonGroups = seasonRegex.find(chapter.name)?.groups
            if (seasonGroups != null) return seasonGroups[2]?.value?.toIntOrNull()
            return null
        }

        private fun getVolumeNumber(chapter: Chapter): Int? {
            val groups = volumeRegex.find(chapter.name)?.groups
            if (groups != null) return groups[2]?.value?.toIntOrNull()
            return null
        }

        private fun getSeasonNumber(chapter: Chapter): Int? {
            val groups = seasonRegex.find(chapter.name)?.groups
            if (groups != null) return groups[2]?.value?.toIntOrNull()
            return null
        }

        fun hasMultipleVolumes(chapters: List<Chapter>): Boolean {
            val volumeSet = mutableSetOf<Int>()
            chapters.forEach {
                val volNum = getVolumeNumber(it)
                if (volNum != null) {
                    volumeSet.add(volNum)
                    if (volumeSet.size >= 2) return true
                }
            }
            return false
        }

        fun hasMultipleSeasons(chapters: List<Chapter>): Boolean {
            val volumeSet = mutableSetOf<Int>()
            chapters.forEach {
                val volNum = getSeasonNumber(it)
                if (volNum != null) {
                    volumeSet.add(volNum)
                    if (volumeSet.size >= 2) return true
                }
            }
            return false
        }

        fun hasTensOfChapters(chapters: List<ChapterItem>): Boolean {
            return chapters.size > 20
        }

        const val scanlatorSeparator = " & "

        fun getScanlators(scanlators: String?): List<String> {
            if (scanlators.isNullOrBlank()) return emptyList()
            return scanlators.split(scanlatorSeparator).distinct()
        }

        fun getScanlatorString(scanlators: Set<String>): String {
            return scanlators.toList().sorted().joinToString(scanlatorSeparator)
        }

        fun Chapter.preferredChapterName(context: Context, manga: Manga, preferences: PreferencesHelper): String {
            return if (manga.hideChapterTitle(preferences) && isRecognizedNumber) {
                val number = decimalFormat.format(chapter_number.toDouble())
                context.getString(R.string.chapter_, number)
            } else {
                name
            }
        }
    }
}
