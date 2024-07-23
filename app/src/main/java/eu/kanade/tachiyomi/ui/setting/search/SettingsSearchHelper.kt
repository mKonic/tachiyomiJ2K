package eu.mkonic.tachiyomi.ui.setting.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceManager
import eu.mkonic.tachiyomi.ui.setting.SettingsAdvancedController
import eu.mkonic.tachiyomi.ui.setting.SettingsAppearanceController
import eu.mkonic.tachiyomi.ui.setting.SettingsBackupController
import eu.mkonic.tachiyomi.ui.setting.SettingsBrowseController
import eu.mkonic.tachiyomi.ui.setting.SettingsController
import eu.mkonic.tachiyomi.ui.setting.SettingsDownloadController
import eu.mkonic.tachiyomi.ui.setting.SettingsGeneralController
import eu.mkonic.tachiyomi.ui.setting.SettingsLibraryController
import eu.mkonic.tachiyomi.ui.setting.SettingsReaderController
import eu.mkonic.tachiyomi.ui.setting.SettingsSecurityController
import eu.mkonic.tachiyomi.ui.setting.SettingsTrackingController
import eu.mkonic.tachiyomi.util.system.isLTR
import eu.mkonic.tachiyomi.util.system.launchNow
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object SettingsSearchHelper {
    private var prefSearchResultList: MutableList<SettingsSearchResult> = mutableListOf()

    /**
     * All subclasses of `SettingsController` should be listed here, in order to have their preferences searchable.
     */
    private val settingControllersList: List<KClass<out SettingsController>> = listOf(
        SettingsAdvancedController::class,
        SettingsBackupController::class,
        SettingsBrowseController::class,
        SettingsDownloadController::class,
        SettingsGeneralController::class,
        SettingsAppearanceController::class,
        SettingsSecurityController::class,
        SettingsLibraryController::class,
        SettingsReaderController::class,
        SettingsTrackingController::class,
    )

    /**
     * Must be called to populate `prefSearchResultList`
     */
    @SuppressLint("RestrictedApi")
    fun initPreferenceSearchResultCollection(context: Context) {
        val preferenceManager = PreferenceManager(context)
        prefSearchResultList.clear()

        launchNow {
            settingControllersList.forEach { kClass ->
                val ctrl = kClass.createInstance()
                val settingsPrefScreen = ctrl.setupPreferenceScreen(preferenceManager.createPreferenceScreen(context))
                val prefCount = settingsPrefScreen.preferenceCount
                for (i in 0 until prefCount) {
                    val rootPref = settingsPrefScreen.getPreference(i)
                    if (rootPref.title == null) continue // no title, not a preference. (note: only info notes appear to not have titles)
                    getSettingSearchResult(ctrl, rootPref, "${settingsPrefScreen.title}")
                }
            }
        }
    }

    fun getFilteredResults(query: String): List<SettingsSearchResult> {
        return prefSearchResultList.filter {
            val inTitle = it.title.contains(query, true)
            val inSummary = it.summary.contains(query, true)
            val inBreadcrumb = it.breadcrumb.replace(">", "").contains(query, true)

            return@filter inTitle || inSummary || inBreadcrumb
        }
    }

    /**
     * Extracts the data needed from a `Preference` to create a `SettingsSearchResult`, and then adds it to `prefSearchResultList`
     * Future enhancement: make bold the text matched by the search query.
     */
    private fun getSettingSearchResult(
        ctrl: SettingsController,
        pref: Preference,
        breadcrumbs: String = "",
    ) {
        val resources = ctrl.resources
        when {
            pref is PreferenceGroup -> {
                val breadcrumbsStr = addLocalizedBreadcrumb(breadcrumbs, "${pref.title}", resources)

                for (x in 0 until pref.preferenceCount) {
                    val subPref = pref.getPreference(x)
                    getSettingSearchResult(ctrl, subPref, breadcrumbsStr) // recursion
                }
            }
            pref is PreferenceCategory -> {
                val breadcrumbsStr = addLocalizedBreadcrumb(breadcrumbs, "${pref.title}", resources)

                for (x in 0 until pref.preferenceCount) {
                    val subPref = pref.getPreference(x)
                    getSettingSearchResult(ctrl, subPref, breadcrumbsStr) // recursion
                }
            }
            (pref.title != null && pref.isVisible) -> {
                // Is an actual preference
                val title = pref.title.toString()
                // ListPreferences occasionally run into ArrayIndexOutOfBoundsException issues
                val summary = try { pref.summary?.toString() ?: "" } catch (e: Throwable) { "" }

                prefSearchResultList.add(
                    SettingsSearchResult(
                        key = pref.key,
                        title = title,
                        summary = summary,
                        breadcrumb = breadcrumbs,
                        searchController = ctrl,
                    ),
                )
            }
        }
    }

    private fun addLocalizedBreadcrumb(path: String, node: String, resources: Resources?): String {
        return if ((resources ?: Resources.getSystem()).isLTR) {
            // This locale reads left to right.
            "$path > $node"
        } else {
            // This locale reads right to left.
            "$node < $path"
        }
    }

    data class SettingsSearchResult(
        val key: String?,
        val title: String,
        val summary: String,
        val breadcrumb: String,
        val searchController: SettingsController,
    )
}
