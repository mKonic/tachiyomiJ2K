package eu.mkonic.tachiyomi.ui.extension

import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.preference.minusAssign
import eu.mkonic.tachiyomi.data.preference.plusAssign
import eu.mkonic.tachiyomi.extension.ExtensionManager
import eu.mkonic.tachiyomi.ui.setting.SettingsController
import eu.mkonic.tachiyomi.ui.setting.onChange
import eu.mkonic.tachiyomi.ui.setting.titleRes
import eu.mkonic.tachiyomi.util.system.LocaleHelper
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy

class ExtensionFilterController : SettingsController() {

    private val extensionManager: ExtensionManager by injectLazy()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.extensions

        val activeLangs = preferences.enabledLanguages().get()

        val availableLangs = extensionManager.availableExtensionsFlow.value.groupBy { it.lang }.keys
            .sortedWith(compareBy({ it !in activeLangs }, { LocaleHelper.getSourceDisplayName(it, context) }))

        availableLangs.forEach {
            SwitchPreferenceCompat(context).apply {
                preferenceScreen.addPreference(this)
                title = LocaleHelper.getSourceDisplayName(it, context)
                isPersistent = false
                isChecked = it in activeLangs

                onChange { newValue ->
                    if (newValue as Boolean) {
                        preferences.enabledLanguages() += it
                    } else {
                        preferences.enabledLanguages() -= it
                    }
                    true
                }
            }
        }
    }
}
