package eu.mkonic.tachiyomi.ui.base.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference

open class BaseCoroutinePresenter<T> {
    var presenterScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var weakView: WeakReference<T>? = null
    protected val view: T?
        get() = weakView?.get()

    /**
     * Attaches a view to the presenter.
     *
     * @param view a view to attach.
     */
    open fun attachView(view: T?) {
        weakView = WeakReference(view)
    }

    open fun onCreate() {
    }

    open fun onDestroy() {
        presenterScope.cancel()
        weakView = null
    }
}
