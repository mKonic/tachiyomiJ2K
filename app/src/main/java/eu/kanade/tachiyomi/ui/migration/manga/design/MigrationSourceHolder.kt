package eu.mkonic.tachiyomi.ui.migration.manga.design

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View
import eu.mkonic.tachiyomi.databinding.MigrationSourceItemBinding
import eu.mkonic.tachiyomi.source.icon
import eu.mkonic.tachiyomi.source.online.HttpSource
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder

class MigrationSourceHolder(view: View, val adapter: MigrationSourceAdapter) :
    BaseFlexibleViewHolder(view, adapter) {

    private val binding = MigrationSourceItemBinding.bind(view)
    init {
        setDragHandleView(binding.reorder)
    }

    fun bind(source: HttpSource, sourceEnabled: Boolean) {
        binding.title.text = source.nameBasedOnEnabledLanguages(adapter.enabledLanguages, adapter.extensionManager)
        // Update circle letter image.
        itemView.post {
            val icon = source.icon()
            if (icon != null) binding.sourceImage.setImageDrawable(icon)
        }

        if (sourceEnabled) {
            binding.title.alpha = 1.0f
            binding.sourceImage.alpha = 1.0f
            binding.title.paintFlags = binding.title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        } else {
            binding.title.alpha = DISABLED_ALPHA
            binding.sourceImage.alpha = DISABLED_ALPHA
            binding.title.paintFlags = binding.title.paintFlags or STRIKE_THRU_TEXT_FLAG
        }
    }

    /**
     * Called when an item is released.
     *
     * @param position The position of the released item.
     */
    override fun onItemReleased(position: Int) {
        super.onItemReleased(position)
        adapter.updateItems()
    }

    companion object {
        private const val DISABLED_ALPHA = 0.3f
    }
}
