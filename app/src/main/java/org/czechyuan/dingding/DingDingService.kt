package org.czechyuan.dingding

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import java.util.*

class DingDingService : Service() {

    private val mBinder = DingDingServiceBinder()
    private val mHandler: Handler = Handler()
    private var mNotificationManager: NotificationManager? = null
    private var isChecked = false

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class DingDingServiceBinder : Binder() {
        val service: DingDingService
            get() = this@DingDingService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTimeChecker()
        try {
            //设置service为前台服务，提高优先级
            if (Build.VERSION.SDK_INT < 18) {
                //Android4.3以下 ，隐藏Notification上的图标
                startForeground(GRAY_SERVICE_ID, Notification())
            } else if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) {
                //Android4.3 - Android7.0，隐藏Notification上的图标
                val innerIntent = Intent(this, GrayInnerService::class.java)
                startService(innerIntent)
                startForeground(GRAY_SERVICE_ID, Notification())
            } else {
                //Android7.0以上app启动后通知栏会出现一条"正在运行"的通知
                startForegroundService()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        acquireWakeLock()
        return START_STICKY
    }

    /**
     * @Description    初始化时间使者
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/14 13:39
     */
    private fun initTimeChecker() {
        mHandler.postDelayed(timeCheckerBeatRunnable, HEART_BEAT_RATE_FAST)
    }

    /**
     * @Description    使者工作
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/14 13:46
     */
    private fun timeCheckerWorking() {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val week = calendar.get(Calendar.DAY_OF_WEEK)
        if (week != 7 && week != 1 && !isChecked && currentHour == TRIGGER_TIME_HOUR && currentMinute in (TRIGGER_TIME_MINUTE_MIN..TRIGGER_TIME_MINUTE_MAX)) {
            ScreenAndLockControl.screenOnAndUnlock(applicationContext)
            LogUtil.i("使者-亮屏解锁", getCurrentTime())
            startDingDingApp(applicationContext)
            Handler().postDelayed(Runnable {
                ScreenAndLockControl.screenOffAndLock(applicationContext)
                LogUtil.i("使者-息屏锁定", getCurrentTime())
            }, 2000)
            isChecked = true
        }
        if (!isChecked && currentHour == 23) {
            isChecked = false
        }
    }


    private val timeCheckerBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            timeCheckerWorking()
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
            val rate =
                if ((currentHour == TRIGGER_TIME_HOUR - 1 && currentMinute >= TRIGGER_TIME_MINUTE_MAX) || (currentHour == TRIGGER_TIME_HOUR && currentMinute < TRIGGER_TIME_MINUTE_MAX)) HEART_BEAT_RATE_FAST else HEART_BEAT_RATE
            LogUtil.i("使者-当前心跳：${rate}", getCurrentTime())
            mHandler.postDelayed(this, rate)
        }
    }

    /**
     * @Description    启动打卡
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 11:45
     */
    private fun startDingDingApp(context: Context) {
        LogUtil.i("使者-开始打卡", getCurrentTime())
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


    /**
     * @description 启动前台服务
     * @date 2021/4/14 17:11
     * @author Czech.Yuan
     */
    private fun startForegroundService() {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_ID,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager!!.createNotificationChannel(channel)
        }
        startForeground(1, getNotification())
    }

    var wakeLock: PowerManager.WakeLock? = null //锁屏唤醒

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    private fun acquireWakeLock() {
        if (null == wakeLock) {
            val pm = this.getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                "DingDingService"
            )
            wakeLock?.acquire()
        }
    }

    //灰色保活
    class GrayInnerService : Service() {
        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            startForeground(GRAY_SERVICE_ID, Notification())
            stopForeground(true)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onBind(intent: Intent): IBinder? {
            return null
        }
    }

    //服务后台运行通知
    private fun getNotification(): Notification {
        val builder = Notification.Builder(this)
            .setContentTitle(NOTIFICATION_NAME)
            .setAutoCancel(false)
            .setContentText("相思断肠红")
        //设置通知图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.MODEL != "P80X(G5K4)") {
            builder.setColor(Color.parseColor("#fdb951"))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
        } else {
            builder.setSmallIcon(R.drawable.ic_launcher_background)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_ID)
        }
        return builder.build()
    }

    companion object {
        private const val HEART_BEAT_RATE = (60 * 60 * 1000).toLong()
        private const val HEART_BEAT_RATE_FAST = (60 * 1000).toLong()
        private const val GRAY_SERVICE_ID = 1001
        private const val TRIGGER_TIME_HOUR = 8
        private const val TRIGGER_TIME_MINUTE_MIN = 23
        private const val TRIGGER_TIME_MINUTE_MAX = 26
        private const val NOTIFICATION_NAME = "望穿秋水露"
        private const val NOTIFICATION_ID = "DingDingService"
    }
}