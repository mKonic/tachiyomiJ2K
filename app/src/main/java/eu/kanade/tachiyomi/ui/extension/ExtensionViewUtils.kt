package eu.mkonic.tachiyomi.ui.extension

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import eu.mkonic.tachiyomi.extension.model.Extension

fun Extension.getApplicationIcon(context: Context): Drawable? {
    return try {
        context.packageManager.getApplicationIcon(pkgName)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}
