package eu.mkonic.tachiyomi.ui.library.display

import android.content.Context
import android.util.AttributeSet
import eu.mkonic.tachiyomi.databinding.LibraryBadgesLayoutBinding
import eu.mkonic.tachiyomi.util.bindToPreference
import eu.mkonic.tachiyomi.widget.BaseLibraryDisplayView

class LibraryBadgesView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    BaseLibraryDisplayView<LibraryBadgesLayoutBinding>(context, attrs) {

    override fun inflateBinding() = LibraryBadgesLayoutBinding.bind(this)
    override fun initGeneralPreferences() {
        binding.unreadBadgeGroup.bindToPreference(preferences.unreadBadgeType()) {
            controller?.presenter?.requestUnreadBadgesUpdate()
        }
        binding.hideReading.bindToPreference(preferences.hideStartReadingButton())
        binding.downloadBadge.bindToPreference(preferences.downloadBadge()) {
            controller?.presenter?.requestDownloadBadgesUpdate()
        }
        binding.languageBadge.bindToPreference(preferences.languageBadge()) {
            controller?.presenter?.requestLanguageBadgesUpdate()
        }
        binding.showNumberOfItems.bindToPreference(preferences.categoryNumberOfItems())
    }
}
