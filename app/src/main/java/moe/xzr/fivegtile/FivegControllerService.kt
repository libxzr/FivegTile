package moe.xzr.fivegtile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService

/**
 * TelephonyManager is not properly initialized in this context, thus
 * everything is done by directly calling telephony service.
 */
class FivegControllerService : RootService() {
    companion object {
        private val iTelephony by lazy {
            Class.forName("com.android.internal.telephony.ITelephony\$Stub")
                .getDeclaredMethod("asInterface", IBinder::class.java)
                .invoke(
                    null,
                    Class.forName("android.os.ServiceManager")
                        .getDeclaredMethod("getService", String::class.java)
                        .invoke(null, Context.TELEPHONY_SERVICE)
                )
        }
        private val getAllowedNetworkTypesForReasonMethod by lazy {
            Class.forName("com.android.internal.telephony.ITelephony")
                .getDeclaredMethod(
                    "getAllowedNetworkTypesForReason",
                    Int::class.java,
                    Int::class.java
                )
        }
        private val getAllowedNetworkTypesMethod by lazy {
            Class.forName("com.android.internal.telephony.ITelephony")
                .getDeclaredMethod(
                    "getAllowedNetworkTypes",
                    Int::class.java
                )
        }
        private val setAllowedNetworkTypesForReasonMethod by lazy {
            Class.forName("com.android.internal.telephony.ITelephony")
                .getDeclaredMethod(
                    "setAllowedNetworkTypesForReason",
                    Int::class.java,
                    Int::class.java,
                    Long::class.java
                )
        }
        private val setAllowedNetworkTypesMethod by lazy {
            Class.forName("com.android.internal.telephony.ITelephony")
                .getDeclaredMethod(
                    "setAllowedNetworkTypes",
                    Int::class.java,
                    Long::class.java
                )
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

        private fun getAllowedNetworkTypes(subId: Int): Long {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getAllowedNetworkTypesForReasonMethod.invoke(
                    iTelephony,
                    subId,
                    reasonUser
                ) as Long
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                getAllowedNetworkTypesMethod.invoke(
                    iTelephony,
                    subId
                ) as Long
            } else {
                throw RuntimeException("not compatible")
            }
        }

        private fun setAllowedNetworkTypes(subId: Int, types: Long): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setAllowedNetworkTypesForReasonMethod.invoke(
                    iTelephony,
                    subId,
                    reasonUser,
                    types
                ) as Boolean
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                setAllowedNetworkTypesMethod.invoke(
                    iTelephony,
                    subId,
                    types
                ) as Boolean
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

        override fun getFivegEnabled(subId: Int): Boolean {
            return try {
                getAllowedNetworkTypes(subId) and typeNr != 0L
            } catch (_: Exception) {
                false
            }
        }

        override fun setFivegEnabled(subId: Int, enabled: Boolean) {
            try {
                var curTypes = getAllowedNetworkTypes(subId)
                curTypes = if (enabled) {
                    curTypes or typeNr
                } else {
                    curTypes and typeNr.inv()
                }
                setAllowedNetworkTypes(subId, curTypes)
            } catch (_: Exception) {

            }
        }
    }
}