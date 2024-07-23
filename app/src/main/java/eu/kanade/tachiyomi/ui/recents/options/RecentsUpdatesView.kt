package eu.mkonic.tachiyomi.ui.recents.options

import android.content.Context
import android.util.AttributeSet
import eu.mkonic.tachiyomi.databinding.RecentsUpdatesViewBinding
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.widget.BaseRecentsDisplayView

class RecentsUpdatesView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    BaseRecentsDisplayView<RecentsUpdatesViewBinding>(context, attrs) {

    override fun inflateBinding() = RecentsUpdatesViewBinding.bind(this)
    override fun initGeneralPreferences() {
        binding.showUpdatedTime.bindToPreference(preferences.showUpdatedTime())
        binding.sortFetchedTime.bindToPreference(preferences.sortFetchedTime())
        binding.groupChapters.bindToPreference(preferences.collapseGroupedUpdates()) {
            controller?.presenter?.expandedSectionsMap?.clear()
        }
    }
}
