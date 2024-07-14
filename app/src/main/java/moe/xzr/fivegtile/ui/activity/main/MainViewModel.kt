package moe.xzr.fivegtile.ui.activity.main

import android.app.Application
import android.telephony.SubscriptionManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import moe.xzr.fivegtile.repository.ShizukuServiceRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val checkCompatibility: Flow<CompatibilityState> = flow {
        val hasPermission = ShizukuServiceRepository.isSupported(getApplication())
        val service = ShizukuServiceRepository.userService

        if (hasPermission && service != null) {
            if (service.compatibilityCheck(SubscriptionManager.getDefaultDataSubscriptionId())) {
                CompatibilityState.COMPATIBLE
            } else {
                CompatibilityState.NOT_COMPATIBLE
            }
        } else {
            CompatibilityState.SHIZUKU_DENIED
        }.also { emit(it) }
    }

    enum class CompatibilityState {
        PENDING,
        SHIZUKU_DENIED,
        NOT_COMPATIBLE,
        COMPATIBLE,
    }
}
