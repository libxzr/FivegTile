package moe.xzr.fivegtile

import com.topjohnwu.superuser.Shell

object Utils {
    fun isRootGranted(): Boolean {
        Shell.getShell()
        return Shell.isAppGrantedRoot() == true
    }
}