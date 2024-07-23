package eu.mkonic.tachiyomi.ui.recents.options

import android.content.Context
import android.util.AttributeSet
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.databinding.RecentsHistoryViewBinding
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.util.system.materialAlertDialog
import eu.mkonic.tachiyomi.widget.BaseRecentsDisplayView

class RecentsHistoryView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    BaseRecentsDisplayView<RecentsHistoryViewBinding>(context, attrs) {

    override fun inflateBinding() = RecentsHistoryViewBinding.bind(this)
    override fun initGeneralPreferences() {
        binding.groupChapters.bindToPreference(preferences.groupChaptersHistory())
        binding.collapseGroupedChapters.bindToPreference(preferences.collapseGroupedHistory()) {
            controller?.presenter?.expandedSectionsMap?.clear()
        }
        binding.clearHistory.setOnClickListener {
            val activity = controller?.activity ?: return@setOnClickListener
            activity.materialAlertDialog()
                .setMessage(R.string.clear_history_confirmation)
                .setPositiveButton(R.string.clear) { _, _ ->
                    controller?.presenter?.deleteAllHistory()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }
}
