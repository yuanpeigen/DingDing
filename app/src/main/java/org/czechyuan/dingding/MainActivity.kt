package org.czechyuan.dingding

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clockBtn.setOnClickListener {
            setAlarmManager()
            startTimeCheckerService()
        }
    }

    private fun setAlarmManager() {
        setAlarmManagerAM()
//        setAlarmManagerPM()
        setSuccessResult()
    }

    private fun startTimeCheckerService() {
        startService(Intent(applicationContext, DingDingService::class.java))
    }

    /**
     * @Description    设置上午定时任务
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/12 9:50
     */
    private fun setAlarmManagerAM() {
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, REQUEST_ID_AM, intent, 0)
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, AM_HOUR)
            set(Calendar.MINUTE, getRandomMinuteAM())
        }
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
        LogUtil.i(
            "设置上午打卡时间",
            "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        )
    }

    /**
     * @Description    设置下午定时任务
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/12 9:50
     */
    private fun setAlarmManagerPM() {
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, REQUEST_ID_PM, intent, 0)
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, PM_HOUR)
            set(Calendar.MINUTE, getRandomMinutePM())
        }
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

        LogUtil.i(
            "设置下午打卡时间",
            "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        )
    }


    /**
     * @Description    随机分钟
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 14:05
     */
    private fun getRandomMinuteAM(): Int {
        return (20..25).random()
    }

    /**
     * @Description    随机分钟
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 14:05
     */
    private fun getRandomMinutePM(): Int {
        return (1..10).random()
    }

    /**
     * @Description    设置启动结果
     * @Author         Czech.Yuan
     * @CreateDate     2021/4/6 14:45
     */
    private fun setSuccessResult() {
        success_text.isVisible = true
        clockBtn.isVisible = false
    }


    companion object {
        const val PACKAGE_NAME_DING = "com.alibaba.android.rimet"
        const val REQUEST_ID_AM = 1896
        const val REQUEST_ID_PM = 1898
        const val AM_HOUR = 8
        const val PM_HOUR = 18
    }
}