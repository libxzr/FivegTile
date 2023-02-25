package moe.xzr.fivegtile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.ServiceManager
import android.telephony.TelephonyManager
import com.android.internal.telephony.ITelephony
import com.android.internal.telephony.RILConstants
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
    }

    override fun onBind(intent: Intent) = object : IFivegController.Stub() {
        @SuppressLint("InlinedApi")
        override fun compatibilityCheck(subId: Int): Boolean {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    iTelephony.setAllowedNetworkTypesForReason(
                        subId,
                        TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER,
                        iTelephony.getAllowedNetworkTypesForReason(
                            subId,
                            TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER
                        )
                    )
                } else {
                    // For Q and R.
                    iTelephony.setPreferredNetworkType(
                        subId,
                        iTelephony.getPreferredNetworkType(subId)
                    )
                }
            } catch (_: Exception) {
                false
            }
        }

        @SuppressLint("InlinedApi")
        override fun getFivegEnabled(subId: Int): Boolean {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    iTelephony.getAllowedNetworkTypesForReason(
                        subId,
                        TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER
                    ) and TelephonyManager.NETWORK_TYPE_BITMASK_NR != 0L
                } else {
                    // For Q and R.
                    iTelephony.getPreferredNetworkType(subId) ==
                            RILConstants.NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA
                }
            } catch (_: Exception) {
                false
            }
        }

        @SuppressLint("InlinedApi")
        override fun setFivegEnabled(subId: Int, enabled: Boolean) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    var curTypes = iTelephony.getAllowedNetworkTypesForReason(
                        subId,
                        TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER
                    )
                    curTypes = if (enabled) {
                        curTypes or TelephonyManager.NETWORK_TYPE_BITMASK_NR
                    } else {
                        curTypes and TelephonyManager.NETWORK_TYPE_BITMASK_NR.inv()
                    }
                    iTelephony.setAllowedNetworkTypesForReason(
                        subId,
                        TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER,
                        curTypes
                    )
                } else {
                    // For Q and R.
                    if (enabled) {
                        iTelephony.setPreferredNetworkType(
                            subId,
                            RILConstants.NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA
                        )
                    } else {
                        iTelephony.setPreferredNetworkType(
                            subId,
                            RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA
                        )
                    }
                }
            } catch (_: Exception) {

            }
        }
    }
}