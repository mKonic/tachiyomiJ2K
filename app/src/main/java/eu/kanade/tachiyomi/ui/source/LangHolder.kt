package eu.mkonic.tachiyomi.ui.source

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.mkonic.tachiyomi.databinding.SourceHeaderItemBinding
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.mkonic.tachiyomi.util.system.LocaleHelper

class LangHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>) :
    BaseFlexibleViewHolder(view, adapter) {

    fun bind(item: LangItem) {
        val binding = SourceHeaderItemBinding.bind(itemView)
        binding.title.text = LocaleHelper.getSourceDisplayName(item.code, itemView.context)
    }
}
