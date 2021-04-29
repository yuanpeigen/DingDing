package org.czechyuan.dingding

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

/**
 *
 * @Description     息屏管理的广播接收器
 * @Author         Czech.Yuan
 * @CreateDate     2021/4/13 9:55
 */
class ScreenOffAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}