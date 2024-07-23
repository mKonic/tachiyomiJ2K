package eu.mkonic.tachiyomi.ui.source.browse.repos

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder

class InfoRepoMessage : AbstractFlexibleItem<InfoRepoMessage.Holder>() {

    /**
     * Returns the layout resource for this item.
     */
    override fun getLayoutRes(): Int {
        return R.layout.info_repo_message
    }

    /**
     * Returns a new view holder for this item.
     *
     * @param view The view of this item.
     * @param adapter The adapter of this item.
     */
    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): Holder {
        return Holder(view, adapter)
    }

    /**
     * Binds the given view holder with this item.
     *
     * @param adapter The adapter of this item.
     * @param holder The holder to bind.
     * @param position The position of this item in the adapter.
     * @param payloads List of partial changes.
     */
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: Holder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
    }

    /**
     * Returns true if this item is draggable.
     */
    override fun isDraggable(): Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is InfoRepoMessage
    }

    override fun hashCode(): Int {
        return "Info repo message".hashCode()
    }

    class Holder(val view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>) :
        BaseFlexibleViewHolder(view, adapter, true)
}
