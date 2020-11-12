package com.bullfrog.framemonitor

import android.content.Context
import android.util.Log
import android.view.Choreographer

class FrameMonitor private constructor() {

    companion object {
        val instance: FrameMonitor by lazy { FrameMonitor() }
    }

    var context: Context? = null
    var callback: MonitorFrameCallback? = null

    fun install(context: Context) {
        this.context = context
    }

    fun uninstall() {
        context = null
    }

    fun turnOn() {
        if (context == null) {
            Log.w("FrameMonitor", "need a context to add view, but the context passed in is null when function install called")
            return
        }
        if (callback == null) {
            callback = MonitorFrameCallback()
        }
        callback!!.initPostTime()
        Choreographer.getInstance().postFrameCallback(callback)
        callback!!.showFrameWindow(context!!)
    }

    fun turnOff() {
        Choreographer.getInstance().removeFrameCallback(callback)
        callback = null
    }
}