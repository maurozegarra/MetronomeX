package com.maurozegarra.app.metronomex.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maurozegarra.app.metronomex.BeatService
import com.maurozegarra.app.metronomex.BeatService.Companion.ACTION_STOP

class StopReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_STOP -> context?.stopService(Intent(context, BeatService::class.java))
        }
    }
}
