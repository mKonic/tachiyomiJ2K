package eu.mkonic.tachiyomi.ui.extension

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.mkonic.tachiyomi.databinding.ExtensionCardHeaderBinding
import eu.mkonic.tachiyomi.extension.model.InstalledExtensionsOrder
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder

class ExtensionGroupHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>) :
    BaseFlexibleViewHolder(view, adapter) {

    private val binding = ExtensionCardHeaderBinding.bind(view)

    init {
        binding.extButton.setOnClickListener {
            (adapter as? ExtensionAdapter)?.listener?.onUpdateAllClicked(bindingAdapterPosition)
        }
        binding.extSort.setOnClickListener {
            (adapter as? ExtensionAdapter)?.listener?.onExtSortClicked(binding.extSort, bindingAdapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: ExtensionGroupItem) {
        binding.title.text = item.name
        binding.extButton.isVisible = item.canUpdate != null
        binding.extButton.isEnabled = item.canUpdate == true
        binding.extSort.isVisible = item.installedSorting != null
        binding.extSort.setText(InstalledExtensionsOrder.fromValue(item.installedSorting ?: 0).nameRes)
        binding.extSort.post {
        }
    }
}
