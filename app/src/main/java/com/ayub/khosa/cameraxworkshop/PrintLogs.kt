package com.ayub.khosa.cameraxworkshop


import android.util.Log

object PrintLogs {
    private const val logs_enable = true
    private const val TAG = "AYUB"
    fun printInfo(message: String) {
        if (logs_enable) Log.i(TAG, "" + message)
    }

    fun printInfo(tag: String, message: String) {
        if (logs_enable) Log.i("" + tag, "" + message)
    }

    fun printE(message: String) {
        if (logs_enable) Log.e(TAG, "" + message)
    }

    fun printE(tag: String, message: String) {
        if (logs_enable) Log.e("" + tag, "" + message)
    }

    fun printD(message: String) {
        if (logs_enable) Log.d(TAG, "" + message)
    }

    fun printD(tag: String, message: String) {
        if (logs_enable) Log.d("" + tag, "" + message)
    }
}
