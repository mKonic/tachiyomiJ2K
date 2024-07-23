package eu.mkonic.tachiyomi.ui.base.activity

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.ui.main.SearchActivity
import eu.mkonic.tachiyomi.ui.security.SecureActivityDelegate
import eu.mkonic.tachiyomi.util.system.getThemeWithExtras
import eu.mkonic.tachiyomi.util.system.setLocaleByAppCompat
import eu.mkonic.tachiyomi.util.system.setThemeByPref
import uy.kohesive.injekt.injectLazy

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    val preferences: PreferencesHelper by injectLazy()
    lateinit var binding: VB
    val isBindingInitialized get() = this::binding.isInitialized

    private var updatedTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocaleByAppCompat()
        updatedTheme = null
        setThemeByPref(preferences)
        super.onCreate(savedInstanceState)
        SecureActivityDelegate.setSecure(this)
    }

    override fun onResume() {
        super.onResume()
        if (this !is SearchActivity) {
            SecureActivityDelegate.promptLockIfNeeded(this)
        }
    }

    override fun getTheme(): Resources.Theme {
        val newTheme = getThemeWithExtras(super.getTheme(), preferences, updatedTheme)
        updatedTheme = newTheme
        return newTheme
    }
}
