package moe.xzr.fivegtile.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.ServiceManager
import androidx.annotation.Keep
import com.android.internal.telephony.ITelephony
import moe.xzr.fivegtile.IFivegController

class ShizukuControllerService() : IFivegController.Stub() {

    companion object {
        private val iTelephony by lazy {
            ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE))
        }
        private val reasonUser by lazy {
            Class.forName("android.telephony.TelephonyManager")
                .getDeclaredField("ALLOWED_NETWORK_TYPES_REASON_USER")
                .getInt(null)
        }
        private val typeNr by lazy {
            Class.forName("android.telephony.TelephonyManager")
                .getDeclaredField("NETWORK_TYPE_BITMASK_NR")
                .getLong(null)
        }

        @delegate:SuppressLint("PrivateApi", "BlockedPrivateApi")
        private val modeLte by lazy {
            Class.forName("com.android.internal.telephony.RILConstants")
                .getDeclaredField("NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA")
                .getInt(null)
        }

        @delegate:SuppressLint("PrivateApi", "BlockedPrivateApi")
        private val modeNr by lazy {
            Class.forName("com.android.internal.telephony.RILConstants")
                .getDeclaredField("NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA")
                .getInt(null)
        }
    }

    @Keep
    constructor(context: Context) : this()

    override fun compatibilityCheck(subId: Int): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                reasonUser
                typeNr
                iTelephony.setAllowedNetworkTypesForReason(
                    subId,
                    reasonUser,
                    iTelephony.getAllowedNetworkTypesForReason(
                        subId,
                        reasonUser
                    )
                )
            } else {
                modeLte
                modeNr
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

    override fun getFivegEnabled(subId: Int): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                iTelephony.getAllowedNetworkTypesForReason(
                    subId,
                    reasonUser
                ) and typeNr != 0L
            } else {
                // For Q and R.
                iTelephony.getPreferredNetworkType(subId) ==
                        modeNr
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun setFivegEnabled(subId: Int, enabled: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var curTypes = iTelephony.getAllowedNetworkTypesForReason(
                    subId,
                    reasonUser
                )
                curTypes = if (enabled) {
                    curTypes or typeNr
                } else {
                    curTypes and typeNr.inv()
                }
                iTelephony.setAllowedNetworkTypesForReason(
                    subId,
                    reasonUser,
                    curTypes
                )
            } else {
                // For Q and R.
                if (enabled) {
                    iTelephony.setPreferredNetworkType(
                        subId,
                        modeNr
                    )
                } else {
                    iTelephony.setPreferredNetworkType(
                        subId,
                        modeLte
                    )
                }
            }
        } catch (_: Exception) {

        }
    }

    override fun destroy() {

    }
}
