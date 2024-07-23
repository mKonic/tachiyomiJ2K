package eu.mkonic.tachiyomi.extension.util

import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class TrustExtension(
    private val preferences: PreferencesHelper = Injekt.get(),
) {

    fun isTrusted(pkgInfo: PackageInfo, signatureHash: String): Boolean {
        val key = "${pkgInfo.packageName}:${PackageInfoCompat.getLongVersionCode(pkgInfo)}:$signatureHash"
        return key in preferences.trustedExtensions().get()
    }

    fun trust(pkgName: String, versionCode: Long, signatureHash: String) {
        preferences.trustedExtensions().let { exts ->
            // Remove previously trusted versions
            val removed = exts.get().filterNot { it.startsWith("$pkgName:") }.toMutableSet()

            removed += "$pkgName:$versionCode:$signatureHash"
            exts.set(removed)
        }
    }

    fun revokeAll() {
        preferences.trustedExtensions().delete()
    }
}
