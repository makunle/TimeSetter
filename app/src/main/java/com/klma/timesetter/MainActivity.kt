package com.klma.timesetter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.format.DateFormat
import android.text.format.DateUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.lang.ref.WeakReference
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val timeUrl = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
    }

    object timeHandler : Handler() {
        var activity: WeakReference<MainActivity>? = null
        fun setActivity(activity: MainActivity) {
            this.activity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message?) {
            val tActivity = activity
            if (tActivity != null) {
                val mainActivity = tActivity.get() ?: return
                mainActivity.updateTimeShow()
                sendEmptyMessageDelayed(0, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        add1d.setOnClickListener {
            shiftDate(0, 1)
        }
        add1h.setOnClickListener {
            shiftDate(1, 0)
        }
        del1d.setOnClickListener {
            shiftDate(0, -1)
        }
        del1h.setOnClickListener {
            shiftDate(-1, 0)
        }
        resetTime.setOnClickListener {
            doAsync {
                val date = URL(timeUrl).openConnection().date
                runOnUiThread {
                    setTime(date)
                }
            }
        }

        RootShell.runCmd("")

        timeHandler.setActivity(this)

        updateTimeShow()
    }

    override fun onResume() {
        super.onResume()
        timeHandler.sendEmptyMessage(0)
    }

    fun shiftDate(hour: Int = 0, day: Int = 0) {
        var millis = System.currentTimeMillis()
        millis += hour * DateUtils.HOUR_IN_MILLIS + day * DateUtils.DAY_IN_MILLIS
        setTime(millis)
    }

    fun setTime(millis: Long) {
        val format = SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault()).format(millis)
        val cmd = "date -s \"$format\""
        RootShell.runCmd(cmd)
        updateTimeShow()
    }

    fun updateTimeShow() {
        timeShow.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
    }

    override fun onPause() {
        super.onPause()
        timeHandler.removeCallbacksAndMessages(null)
    }
}
