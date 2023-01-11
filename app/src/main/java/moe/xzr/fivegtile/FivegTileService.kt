package moe.xzr.fivegtile

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.telephony.SubscriptionManager
import com.topjohnwu.superuser.ipc.RootService

class FivegTileService : TileService() {
    private var fivegController: IFivegController? = null

    private fun runWithFivegController(what: (IFivegController?) -> Unit) {
        if (!Utils.isRootGranted()) {
            what(null)
            return
        }
        if (fivegController != null) {
            what(fivegController)
        } else {
            RootService.bind(
                Intent(this@FivegTileService, FivegControllerService::class.java),
                object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        fivegController = IFivegController.Stub.asInterface(service)
                        what(fivegController)
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {

                    }
                })
        }
    }

    private fun refreshState() = runWithFivegController {
        if (it == null) {
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.updateTile()
            return@runWithFivegController
        }
        val subId = SubscriptionManager.getDefaultDataSubscriptionId()
        qsTile.state = if (!it.compatibilityCheck(subId)) {
            Tile.STATE_UNAVAILABLE
        } else {
            if (it.getFivegEnabled(subId))
                Tile.STATE_ACTIVE
            else
                Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }


    override fun onStartListening() {
        super.onStartListening()
        refreshState()
    }

    override fun onClick() {
        super.onClick()
        runWithFivegController {
            val subId = SubscriptionManager.getDefaultDataSubscriptionId()
            if (qsTile.state == Tile.STATE_INACTIVE) {
                it?.setFivegEnabled(subId, true)
            } else if (qsTile.state == Tile.STATE_ACTIVE) {
                it?.setFivegEnabled(subId, false)
            }
            refreshState()
        }
    }
}