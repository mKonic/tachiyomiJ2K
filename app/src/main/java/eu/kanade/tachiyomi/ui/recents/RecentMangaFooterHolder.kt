package eu.mkonic.tachiyomi.ui.recents

import android.view.View
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.databinding.RecentsFooterItemBinding
import eu.mkonic.tachiyomi.ui.manga.chapter.BaseChapterHolder

class RecentMangaFooterHolder(
    view: View,
    val adapter: RecentMangaAdapter,
) : BaseChapterHolder(view, adapter) {
    private val binding = RecentsFooterItemBinding.bind(view)

    fun bind(recentsType: Int) {
        when (recentsType) {
            RecentMangaHeaderItem.CONTINUE_READING -> {
                binding.title.setText(R.string.view_history)
            }
            RecentMangaHeaderItem.NEW_CHAPTERS -> {
                binding.title.setText(R.string.view_all_updates)
            }
        }
    }

    override fun onLongClick(view: View?): Boolean {
        super.onLongClick(view)
        return false
    }
}
