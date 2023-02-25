package moe.xzr.fivegtile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.ServiceManager
import android.telephony.TelephonyManager
import com.android.internal.telephony.ITelephony
import com.topjohnwu.superuser.ipc.RootService

/**
 * TelephonyManager is not properly initialized in this context, thus
 * everything is done by directly calling telephony service.
 */
class FivegControllerService : RootService() {
    companion object {
        private val iTelephony by lazy {
            ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE))
        }

        @SuppressLint("InlinedApi")
        private fun getAllowedNetworkTypes(subId: Int): Long {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                iTelephony.getAllowedNetworkTypesForReason(
                    subId,
                    TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER
                )
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                iTelephony.getAllowedNetworkTypes(subId)
            } else {
                throw RuntimeException("not compatible")
            }
        }

        @SuppressLint("InlinedApi")
        private fun setAllowedNetworkTypes(subId: Int, types: Long): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                iTelephony.setAllowedNetworkTypesForReason(
                    subId,
                    TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER,
                    types
                )
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                iTelephony.setAllowedNetworkTypes(subId, types)
            } else {
                throw RuntimeException("not compatible")
            }
        }
    }

    override fun onBind(intent: Intent) = object : IFivegController.Stub() {
        override fun compatibilityCheck(subId: Int): Boolean {
            return try {
                setAllowedNetworkTypes(subId, getAllowedNetworkTypes(subId))
            } catch (_: Exception) {
                false
            }
        }

        @SuppressLint("InlinedApi")
        override fun getFivegEnabled(subId: Int): Boolean {
            return try {
                getAllowedNetworkTypes(subId) and TelephonyManager.NETWORK_TYPE_BITMASK_NR != 0L
            } catch (_: Exception) {
                false
            }
        }

        @SuppressLint("InlinedApi")
        override fun setFivegEnabled(subId: Int, enabled: Boolean) {
            try {
                var curTypes = getAllowedNetworkTypes(subId)
                curTypes = if (enabled) {
                    curTypes or TelephonyManager.NETWORK_TYPE_BITMASK_NR
                } else {
                    curTypes and TelephonyManager.NETWORK_TYPE_BITMASK_NR.inv()
                }
                setAllowedNetworkTypes(subId, curTypes)
            } catch (_: Exception) {

            }
        }
    }
}