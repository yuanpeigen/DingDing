package org.czechyuan.dingding

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import java.util.*

/**
 *
 * @Description     定时打卡广播
 * @Author         Czech.Yuan
 * @CreateDate     2021/4/6 11:59
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //收到打卡广播
        LogUtil.i("收到打卡广播", getCurrentTime())
        context?.let {
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val week = calendar.get(Calendar.DAY_OF_WEEK)
            if (week != 7 && week != 1 && currentHour <= MainActivity.AM_HOUR) {
                ScreenAndLockControl.screenOnAndUnlock(context)
                LogUtil.i("亮屏解锁", getCurrentTime())
                startDingDingApp(context)
                Handler().postDelayed(Runnable {
                    ScreenAndLockControl.screenOffAndLock(context)
                    LogUtil.i("息屏锁定", getCurrentTime())
                }, 2000)
            }
        }
    }


    /**
     * @Description    启动打卡
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 11:45
     */
    private fun startDingDingApp(context: Context) {
        LogUtil.i("开始打卡", getCurrentTime())
        val intent =
            context.packageManager.getLaunchIntentForPackage(MainActivity.PACKAGE_NAME_DING)
        intent?.let {
            context.startActivity(it)
        }
    }


    /**
     * @Description    获取当前时间
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 14:23
     */
    private fun getCurrentTime(): String {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        return "${calendar.get(Calendar.HOUR_OF_DAY)}:${if (calendar.get(Calendar.MINUTE) > 9) "" else "0"}${
            calendar.get(
                Calendar.MINUTE
            )
        }:${calendar.get(Calendar.SECOND)}"
    }

}