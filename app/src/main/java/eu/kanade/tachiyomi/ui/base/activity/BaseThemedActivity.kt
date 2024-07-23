package eu.mkonic.tachiyomi.ui.base.activity

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.util.system.getThemeWithExtras
import eu.mkonic.tachiyomi.util.system.setLocaleByAppCompat
import eu.mkonic.tachiyomi.util.system.setThemeByPref
import uy.kohesive.injekt.injectLazy

abstract class BaseThemedActivity : AppCompatActivity() {

    val preferences: PreferencesHelper by injectLazy()
    private var updatedTheme: Resources.Theme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocaleByAppCompat()
        updatedTheme = null
        setThemeByPref(preferences)
        super.onCreate(savedInstanceState)
    }

    override fun getTheme(): Resources.Theme {
        val newTheme = getThemeWithExtras(super.getTheme(), preferences, updatedTheme)
        updatedTheme = newTheme
        return newTheme
    }
}
