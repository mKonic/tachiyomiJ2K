package eu.mkonic.tachiyomi.ui.reader.chapter

import android.graphics.Typeface
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.databinding.ReaderChapterItemBinding
import eu.mkonic.tachiyomi.util.chapter.ChapterUtil
import eu.mkonic.tachiyomi.util.chapter.ChapterUtil.Companion.preferredChapterName
import uy.kohesive.injekt.injectLazy

class ReaderChapterItem(val chapter: Chapter, val manga: Manga, val isCurrent: Boolean) :
    AbstractItem<ReaderChapterItem.ViewHolder>(),
    Chapter by chapter {

    val preferences: PreferencesHelper by injectLazy()

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int = R.id.reader_chapter_layout

    /** defines the layout which will be used for this item in the list */
    override val layoutRes: Int = R.layout.reader_chapter_item

    override var identifier: Long = chapter.id!!

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<ReaderChapterItem>(view) {
        val binding = ReaderChapterItemBinding.bind(view)

        override fun bindView(item: ReaderChapterItem, payloads: List<Any>) {
            val manga = item.manga

            val chapterColor = ChapterUtil.chapterColor(itemView.context, item.chapter)

            binding.chapterTitle.text =
                item.preferredChapterName(itemView.context, manga, item.preferences)

            val statuses = mutableListOf<String>()
            ChapterUtil.relativeDate(item)?.let { statuses.add(it) }
            item.scanlator?.takeIf { it.isNotBlank() }?.let { statuses.add(item.scanlator ?: "") }

            if (item.isCurrent) {
                binding.chapterTitle.setTypeface(null, Typeface.BOLD_ITALIC)
                binding.chapterSubtitle.setTypeface(null, Typeface.BOLD_ITALIC)
            } else {
                binding.chapterTitle.setTypeface(null, Typeface.NORMAL)
                binding.chapterSubtitle.setTypeface(null, Typeface.NORMAL)
            }

            // match color of the chapter title
            binding.chapterTitle.setTextColor(chapterColor)
            binding.chapterSubtitle.setTextColor(chapterColor)

            binding.bookmarkImage.setImageResource(
                if (item.bookmark) {
                    R.drawable.ic_bookmark_24dp
                } else {
                    R.drawable.ic_bookmark_border_24dp
                },
            )

            val drawableColor = ChapterUtil.bookmarkColor(itemView.context, item)

            DrawableCompat.setTint(binding.bookmarkImage.drawable, drawableColor)

            binding.chapterSubtitle.text = statuses.joinToString(" • ")
        }

        override fun unbindView(item: ReaderChapterItem) {
            binding.chapterTitle.text = null
            binding.chapterSubtitle.text = null
        }
    }
}
