package eu.mkonic.tachiyomi.ui.setting

import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceScreen
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.preference.PreferenceKeys
import eu.mkonic.tachiyomi.data.preference.PreferenceValues
import eu.mkonic.tachiyomi.data.preference.asImmediateFlowIn
import eu.mkonic.tachiyomi.ui.security.SecureActivityDelegate
import eu.mkonic.tachiyomi.util.system.AuthenticatorUtil.isAuthenticationSupported

class SettingsSecurityController : SettingsController() {
    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.security

        if (context.isAuthenticationSupported()) {
            switchPreference {
                key = PreferenceKeys.useBiometrics
                titleRes = R.string.lock_with_biometrics
                defaultValue = false

                requireAuthentication(
                    activity as? FragmentActivity,
                    context.getString(R.string.lock_with_biometrics),
                    confirmationRequired = false,
                )
            }
            intListPreference(activity) {
                key = PreferenceKeys.lockAfter
                titleRes = R.string.lock_when_idle
                val values = listOf(0, 2, 5, 10, 20, 30, 60, 90, 120, -1)
                entries = values.mapNotNull {
                    when (it) {
                        0 -> context.getString(R.string.always)
                        -1 -> context.getString(R.string.never)
                        else -> resources?.getQuantityString(
                            R.plurals.after_minutes,
                            it,
                            it,
                        )
                    }
                }
                entryValues = values
                defaultValue = 0

                preferences.useBiometrics().asImmediateFlowIn(viewScope) { isVisible = it }
            }
        }

        switchPreference {
            key = PreferenceKeys.hideNotificationContent
            titleRes = R.string.hide_notification_content
            defaultValue = false
        }

        listPreference(activity) {
            bindTo(preferences.secureScreen())
            titleRes = R.string.secure_screen
            entriesRes = PreferenceValues.SecureScreenMode.entries.map { it.titleResId }.toTypedArray()
            entryValues = PreferenceValues.SecureScreenMode.entries.map { it.name }

            onChange {
                it as String
                SecureActivityDelegate.setSecure(activity)
                true
            }
        }

        infoPreference(R.string.secure_screen_summary)
    }
}
