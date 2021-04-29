package org.czechyuan.dingding

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * @Description     日志类
 * @Author         Czech.Yuan
 * @CreateDate     2021/4/12 9:34
 */
object LogUtil {
    private const val LOG_SWITCH = true // 日志文件总开关
    private const val LOG_WRITE_TO_FILE = true // 日志写入文件开关
    private const val LOG_TYPE = 'v' // 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

    @SuppressLint("SdCardPath")
    private const val LOG_PATH_SDCARD_DIR = "/sdcard/dingding/log" // 日志文件在sdcard中的路径
    private const val SDCARD_LOG_FILE_SAVE_DAYS = 0 // sd卡中日志文件的最多保存天数
    private const val LOGFILEName = "_log.txt" // 本类输出的日志文件名称

    @SuppressLint("SimpleDateFormat")
    private val myLogSdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 日志的输出格式

    @SuppressLint("SimpleDateFormat")
    private val logfile: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd") // 日志文件格式
    fun w(tag: String, msg: Any) { // 警告信息
        log(tag, msg.toString(), 'w')
    }

    fun e(tag: String, msg: Any) { // 错误信息
        log(tag, msg.toString(), 'e')
    }

    fun d(tag: String, msg: Any) { // 调试信息
        log(tag, msg.toString(), 'd')
    }

    fun i(tag: String, msg: Any) { //
        log(tag, msg.toString(), 'i')
    }

    fun v(tag: String, msg: Any) {
        log(tag, msg.toString(), 'v')
    }

    fun w(tag: String, text: String) {
        log(tag, text, 'w')
    }

    fun e(tag: String, text: String) {
        log(tag, text, 'e')
    }

    fun d(tag: String, text: String) {
        log(tag, text, 'd')
    }

    fun i(tag: String, text: String) {
        log(tag, text, 'i')
    }

    fun v(tag: String, text: String) {
        log(tag, text, 'v')
    }

    /**
     * 根据tag, msg和等级，输出日志
     * @param tag
     * @param msg
     * @param level
     */
    private fun log(tag: String, msg: String, level: Char) {
        if (LOG_SWITCH) { //日志文件总开关
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) { // 输出错误信息
                Log.e(tag, msg)
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(tag, msg)
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(tag, msg)
            } else if ('i' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.i(tag, msg)
            } else {
                Log.v(tag, msg)
            }
            if (LOG_WRITE_TO_FILE) //日志写入文件开关
                writeLogToFile(level.toString(), tag, msg)
        }
    }

    /**
     * 打开日志文件并写入日志
     * @param logType
     * @param tag
     * @param text
     */
    private fun writeLogToFile(logType: String, tag: String, text: String) { // 新建或打开日志文件
        val nowTime = Date()
        val needWriteFile: String = logfile.format(nowTime)
        val needWriteMessage: String = myLogSdf.format(nowTime)
            .toString() + "    " + logType + "    " + tag + "    " + text
        val dirPath: File = Environment.getExternalStorageDirectory()
        val dirsFile = File(LOG_PATH_SDCARD_DIR)
        if (!dirsFile.exists()) {
            dirsFile.mkdirs()
        }
        //Log.i("创建文件","创建文件");
        val file =
            File(dirsFile.toString(), needWriteFile + LOGFILEName) // LOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile()
            } catch (e: Exception) {
            }
        }
        try {
            val filerWriter = FileWriter(file, true) // 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            val bufWriter = BufferedWriter(filerWriter)
            bufWriter.write(needWriteMessage)
            bufWriter.newLine()
            bufWriter.close()
            filerWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 删除制定的日志文件
     */
    fun delFile() { // 删除日志文件
        val needDelFile: String = logfile.format(dateBefore)
        val dirPath: File = Environment.getExternalStorageDirectory()
        val file = File(dirPath, needDelFile + LOGFILEName) // LOG_PATH_SDCARD_DIR
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private val dateBefore: Date
        get() {
            val nowTime = Date()
            val now: Calendar = Calendar.getInstance()
            now.time = nowTime
            now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS)
            return now.time
        }
}