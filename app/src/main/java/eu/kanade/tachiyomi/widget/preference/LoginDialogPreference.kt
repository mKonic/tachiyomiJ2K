package eu.mkonic.tachiyomi.widget.preference

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import eu.mkonic.tachiyomi.data.preference.PreferencesHelper
import eu.mkonic.tachiyomi.databinding.PrefAccountLoginBinding
import eu.mkonic.tachiyomi.ui.base.controller.DialogController
import eu.mkonic.tachiyomi.util.system.materialAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import rx.Subscription
import uy.kohesive.injekt.injectLazy

abstract class LoginDialogPreference(
    @StringRes private val usernameLabelRes: Int? = null,
    bundle: Bundle? = null,
) :
    DialogController(bundle) {

    var v: View? = null
        private set

    protected lateinit var binding: PrefAccountLoginBinding
    val preferences: PreferencesHelper by injectLazy()

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    var requestSubscription: Subscription? = null

    open var canLogout = false

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = PrefAccountLoginBinding.inflate(activity!!.layoutInflater)
        val dialog = activity!!.materialAlertDialog().apply {
            setView(binding.root)
        }
        onViewCreated(binding.root)

        return dialog.create()
    }

    fun onViewCreated(view: View) {
        v = view.apply {
            if (usernameLabelRes != null) {
                binding.usernameInput.hint = view.context.getString(usernameLabelRes)
            }

            binding.login.setOnClickListener {
                checkLogin()
            }

            setCredentialsOnView(this)
        }
    }

    override fun onChangeStarted(handler: ControllerChangeHandler, type: ControllerChangeType) {
        super.onChangeStarted(handler, type)
        if (!type.isEnter) {
            onDialogClosed()
        }
    }

    open fun onDialogClosed() {
        scope.cancel()
        requestSubscription?.unsubscribe()
    }

    protected abstract fun checkLogin()

    protected abstract fun setCredentialsOnView(view: View)
}
