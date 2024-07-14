package moe.xzr.fivegtile.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.telephony.SubscriptionManager
import kotlinx.coroutines.runBlocking
import moe.xzr.fivegtile.IFivegController
import moe.xzr.fivegtile.repository.ShizukuServiceRepository

class FivegTileService : TileService() {

    private fun runWithFivegController(what: (IFivegController?) -> Unit) = runBlocking {
        if (ShizukuServiceRepository.isSupported(this@FivegTileService)) {
            what(ShizukuServiceRepository.userService)
        } else {
            what(null)
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
