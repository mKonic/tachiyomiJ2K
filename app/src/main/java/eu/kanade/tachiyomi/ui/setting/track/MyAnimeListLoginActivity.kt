package eu.mkonic.tachiyomi.ui.setting.track

import android.net.Uri
import androidx.lifecycle.lifecycleScope
import eu.mkonic.tachiyomi.util.system.launchIO

class MyAnimeListLoginActivity : BaseOAuthLoginActivity() {

    override fun handleResult(data: Uri?) {
        val code = data?.getQueryParameter("code")
        if (code != null) {
            lifecycleScope.launchIO {
                trackManager.myAnimeList.login(code)
                returnToSettings()
            }
        } else {
            trackManager.myAnimeList.logout()
            returnToSettings()
        }
    }
}
