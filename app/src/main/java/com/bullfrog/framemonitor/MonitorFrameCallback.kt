package com.bullfrog.framemonitor

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MonitorFrameCallback : Choreographer.FrameCallback {

    companion object {
        const val NANOS_PER_MILLIS = 1000000
        const val MILLIS_PER_SECOND = 1000
        const val NORMAL_FRAME_DURATION = 16 // 16ms
    }

    var postTime: Long = 0
    var wm: WindowManager? = null
    var frameView: View? = null
    var tvFrame: TextView? = null
    var clFrameRoot: ConstraintLayout? = null

    fun initPostTime() {
        postTime = System.currentTimeMillis()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (postTime == 0L) return
        val frameTimeMillis = frameTimeNanos / NANOS_PER_MILLIS
        val duration = frameTimeMillis - postTime
        val missedFrame = duration / NORMAL_FRAME_DURATION
        val curFPS = MILLIS_PER_SECOND / duration
        Log.w("MonitorFrameCallback", "cur fps = ${curFPS}, missed Frames = $missedFrame")
        postTime = frameTimeMillis

        updateFrameView(curFPS)

        Choreographer.getInstance().postFrameCallback(this)
    }

    fun updateFrameView(curFPS: Long) {
        tvFrame?.text = curFPS.toString()
    }

    fun showFrameWindow(context: Context) {
        frameView = LayoutInflater.from(context).inflate(R.layout.layout_frame_window, null)
        clFrameRoot = frameView!!.findViewById(R.id.clFrameRoot)
        tvFrame = frameView!!.findViewById(R.id.tvFrame)

        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams()
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        params.flags = params.flags or flags
        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.TOP or Gravity.START
        params.dimAmount = 0f // sdk30 及以上默认是 1.0f，会使背景全黑
        params.width = 280
        params.height = 280

        wm!!.addView(frameView, params)

        /**
         * 待解决问题：
         * 1、返回键无法返回
         * 2、权限似乎不需要申请？
         * 待做：
         * 1、frameView 要可拖动，直接封装成一个自定义 View
         */
    }
}