package eu.mkonic.tachiyomi.ui.extension.details

import eu.mkonic.tachiyomi.extension.ExtensionManager
import eu.mkonic.tachiyomi.ui.base.presenter.BaseCoroutinePresenter
import eu.mkonic.tachiyomi.util.system.launchUI
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ExtensionDetailsPresenter(
    val pkgName: String,
    private val extensionManager: ExtensionManager = Injekt.get(),
) : BaseCoroutinePresenter<ExtensionDetailsController>() {

    val extension = extensionManager.installedExtensionsFlow.value.find { it.pkgName == pkgName }

    override fun onCreate() {
        super.onCreate()
        bindToUninstalledExtension()
    }

    private fun bindToUninstalledExtension() {
        extensionManager.installedExtensionsFlow
            .drop(1)
            .onEach { extensions ->
                extensions.filter { it.pkgName == pkgName }
                presenterScope.launchUI { view?.onExtensionUninstalled() }
            }
            .launchIn(presenterScope)
    }

    fun uninstallExtension() {
        val extension = extension ?: return
        extensionManager.uninstallExtension(extension.pkgName)
    }
}
