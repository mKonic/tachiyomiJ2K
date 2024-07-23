package eu.mkonic.tachiyomi.ui.manga.chapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.models.Chapter
import eu.mkonic.tachiyomi.data.database.models.Manga
import eu.mkonic.tachiyomi.ui.manga.MangaDetailsAdapter

class ChapterItem(chapter: Chapter, val manga: Manga) :
    BaseChapterItem<ChapterHolder, AbstractHeaderItem<FlexibleViewHolder>>(chapter) {

    var isLocked = false

    override fun getLayoutRes(): Int {
        return R.layout.chapters_item
    }

    override fun isSelectable(): Boolean {
        return true
    }

    override fun isSwipeable(): Boolean {
        return !isLocked
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ChapterHolder {
        return ChapterHolder(view, adapter as MangaDetailsAdapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ChapterHolder,
        position: Int,
        payloads: MutableList<Any?>?,
    ) {
        holder.bind(this, manga)
    }

    override fun unbindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
        holder: ChapterHolder?,
        position: Int,
    ) {
        super.unbindViewHolder(adapter, holder, position)
        (adapter as MangaDetailsAdapter).controller.dismissPopup(position)
    }
}
