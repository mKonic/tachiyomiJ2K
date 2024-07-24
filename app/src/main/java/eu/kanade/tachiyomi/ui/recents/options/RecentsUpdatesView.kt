package eu.mkonic.tachiyomi.ui.recents.options

import android.content.Context
import android.util.AttributeSet
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.databinding.RecentsUpdatesViewBinding
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.util.system.materialAlertDialog
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
        binding.clearUpdates.setOnClickListener {
            val activity = controller?.activity ?: return@setOnClickListener
            activity.materialAlertDialog()
                .setMessage(R.string.clear_updates_confirmation)
                .setPositiveButton(R.string.clear) { _, _ ->
                    controller?.presenter?.deleteAllUpdates()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}
