package moe.xzr.fivegtile.repository

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import moe.xzr.fivegtile.BuildConfig
import moe.xzr.fivegtile.IFivegController
import moe.xzr.fivegtile.R
import moe.xzr.fivegtile.service.ShizukuControllerService
import rikka.shizuku.Shizuku
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ShizukuServiceRepository {

    var userService: IFivegController? = null
        private set

    private const val SHIZUKU_PERMISSION_REQUEST_ID = 8

    suspend fun isSupported(context: Context) = suspendCoroutine<Boolean> {
        when {
            !Shizuku.pingBinder() -> it.resume(false)
            Shizuku.isPreV11() -> it.resume(false)

            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> it.resumeWithServiceBinding(context)
            Shizuku.shouldShowRequestPermissionRationale() -> it.resume(false)

            else -> {
                val listener = object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (requestCode != SHIZUKU_PERMISSION_REQUEST_ID) return

                        when (grantResult == PackageManager.PERMISSION_GRANTED) {
                            true -> it.resumeWithServiceBinding(context)

                            else -> it.resume(false)
                        }

                        Shizuku.removeRequestPermissionResultListener(this)
                    }
                }

                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_ID)
            }
        }
    }

    private fun Continuation<Boolean>.resumeWithServiceBinding(context: Context) {
        if (userService != null) {
            resume(true)
            return
        }

        var resumed = false
        object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                if (binder == null || !binder.pingBinder()) {
                    if (resumed.not()) {
                        resume(false)
                        resumed = true
                    }
                    return
                }

                userService = IFivegController.Stub.asInterface(binder)

                if (resumed.not()) {
                    resume(true)
                    resumed = true
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                userService = null
            }
        }.also {
            Shizuku.bindUserService(buildServiceArgs(context), it)
        }
    }

    private fun buildServiceArgs(context: Context) =
        Shizuku.UserServiceArgs(ComponentName(context, ShizukuControllerService::class.java))
            .processNameSuffix("service")
            .debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE)
            .tag(context.getString(R.string.app_name))
}
