package eu.mkonic.tachiyomi.ui.base.controller

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import eu.mkonic.tachiyomi.ui.base.presenter.BaseCoroutinePresenter

abstract class BaseCoroutineController<VB : ViewBinding, PS : BaseCoroutinePresenter<*>>(bundle: Bundle? = null) :
    BaseController<VB>(bundle) {

    abstract val presenter: PS
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        presenter.takeView(this)
        presenter.onCreate()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <View> BaseCoroutinePresenter<View>.takeView(view: Any) = attachView(view as? View)

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
