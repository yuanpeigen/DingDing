package org.czechyuan.dingding

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.os.PowerManager


/**
 *
 * @Description    点亮屏幕并解锁
 * @Author         Czech.Yuan
 * @CreateDate     2021/4/12 9:58
 */
object ScreenAndLockControl {


    /**
     * @Description    亮屏解锁
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 10:07
     */
    fun screenOnAndUnlock(context: Context) {
        screenOn(context)
        unlock(context)
    }


    /**
     * @Description    息屏锁定
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 10:07
     */
    fun screenOffAndLock(context: Context) {
        screenOff(context)
        lock(context)
    }


    /**
     * @Description    亮屏
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 9:50
     */
    @SuppressLint("InvalidWakeLockTag")
    fun screenOn(context: Context) {
        // 获取电源管理器对象
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val screenOn = pm.isScreenOn
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            val wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"
            )
            wl.acquire(10000) // 点亮屏幕
            wl.release() // 释放
        }
    }


    /**
     * @Description    息屏
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 10:01
     */
    fun screenOff(context: Context) {
        val policyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminReceiver = ComponentName(
            context,
            ScreenOffAdminReceiver::class.java
        )
        val admin = policyManager!!.isAdminActive(adminReceiver)
        if (admin) {
            policyManager.lockNow()
        } else {
            LogUtil.i("息屏", "没有设备管理权限")
        }
    }


    /**
     * @Description    解锁
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 10:04
     */
    private fun unlock(context: Context) {
        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("unLock")
        keyguardLock.disableKeyguard()
    }

    /**
     * @Description    锁定
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/13 10:04
     */
    private fun lock(context: Context) {
        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("unLock")
        keyguardLock.reenableKeyguard()
    }


    /**
     * @Description    模拟点击电源键
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/12 10:41
     */
    fun sendKeyCode() {
        try {
            val keyCommand = "input keyevent 26"
            Runtime.getRuntime().exec(keyCommand)
        } catch (e: Exception) {
            LogUtil.i("模拟点击电源键异常", e.toString())
        }
    }
}