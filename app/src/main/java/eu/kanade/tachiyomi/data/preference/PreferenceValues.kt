package eu.mkonic.tachiyomi.data.preference

import androidx.annotation.StringRes
import eu.mkonic.tachiyomi.R

// Library
const val MANGA_NON_COMPLETED = "manga_ongoing"
const val MANGA_HAS_UNREAD = "manga_fully_read"
const val MANGA_NON_READ = "manga_started"

// Device
const val DEVICE_ONLY_ON_WIFI = "wifi"
const val DEVICE_CHARGING = "ac"
const val DEVICE_BATTERY_NOT_LOW = "battery_not_low"

object PreferenceValues {
    enum class SecureScreenMode(val titleResId: Int) {
        ALWAYS(R.string.always),
        INCOGNITO(R.string.incognito_mode),
        NEVER(R.string.never),
    }

    enum class ReaderHideThreshold(val titleResId: Int, val threshold: Int) {
        HIGHEST(R.string.pref_highest, 5),
        HIGH(R.string.pref_high, 13),
        LOW(R.string.pref_low, 31),
        LOWEST(R.string.pref_lowest, 47),
    }

    enum class MigrationSourceOrder(val value: Int, @StringRes val titleResId: Int) {
        Alphabetically(0, R.string.alphabetically),
        MostEntries(1, R.string.most_entries),
        Obsolete(2, R.string.obsolete),
        ;

        companion object {
            fun fromValue(preference: Int) = entries.find { it.value == preference } ?: Alphabetically
            fun fromPreference(pref: PreferencesHelper) = fromValue(pref.migrationSourceOrder().get())
        }
    }
}
