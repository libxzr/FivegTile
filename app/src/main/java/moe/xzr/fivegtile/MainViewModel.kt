package moe.xzr.fivegtile

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.telephony.SubscriptionManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.AndroidViewModel
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class CompatibilityState {
        PENDING,
        ROOT_DENIED,
        NOT_COMPATIBLE,
        COMPATIBLE,
    }

    @Composable
    fun getCompatibilityState(): CompatibilityState {
        val ret by produceState(initialValue = CompatibilityState.PENDING) {
            if (!withContext(Dispatchers.IO) {
                    Utils.isRootGranted()
                }) {
                value = CompatibilityState.ROOT_DENIED
            } else {
                RootService.bind(
                    Intent(getApplication(), FivegControllerService::class.java),
                    object : ServiceConnection {
                        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                            val fivegController = IFivegController.Stub.asInterface(service)
                            value =
                                if (fivegController.compatibilityCheck(SubscriptionManager.getDefaultDataSubscriptionId()))
                                    CompatibilityState.COMPATIBLE
                                else
                                    CompatibilityState.NOT_COMPATIBLE
                        }

                        override fun onServiceDisconnected(name: ComponentName?) {

                        }

                    })
            }
        }
        return ret
    }
}