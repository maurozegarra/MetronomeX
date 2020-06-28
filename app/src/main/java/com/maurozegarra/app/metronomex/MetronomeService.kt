package com.maurozegarra.app.metronomex

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.service.quicksettings.TileService.requestListeningState

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MetronomeService : Service() {

    companion object {
        const val KEY_IS_BEATING = "key_is_beating"
        const val SHARED_PREFERENCES = "shared_preferences"
        const val ACTION_IS_BEATING = "com.maurozegarra.app.IS_BEATING"

        // This is the number of milliseconds in a minute
        const val ONE_MINUTE = 60_000L

        // This is the number of beats per minute
        const val BPM = 100
    }

    private var handler = Handler()
    private lateinit var runnable: Runnable
    private var isBeating = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isBeating = true

        /* Start Broadcast */
        val myIntent = Intent(ACTION_IS_BEATING)
        myIntent.putExtra(KEY_IS_BEATING, isBeating)
        //Log.d(TAG, "onStartCommand: Called: isBeating = $isBeating")
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent)

        val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_BEATING, isBeating)
        editor.apply()

        requestListeningState(this, ComponentName(this, BeatTile::class.java))
        Log.d(TAG, "Called: onStartCommand:requestListeningState: isBeating = $isBeating")

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
        val notification =
            NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText("Beating") // do the actual work
                .setSmallIcon(R.drawable.ic_metronome)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification)

        /* do heavy work on a background thread */
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        val tick = soundPool.load(this, R.raw.tick, 1)

        val interval = getInterval()

        runnable = Runnable {
            soundPool.play(tick, 1.0F, 1.0F, 0, 0, 1.0F)
            handler.postDelayed(runnable, interval)
        }

        handler.postDelayed(runnable, interval)
        //Toast.makeText(applicationContext, "Beating", Toast.LENGTH_LONG).show();

        // stopSelf();
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        isBeating = false

        val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_BEATING, isBeating)
        editor.apply()

        requestListeningState(this, ComponentName(this, BeatTile::class.java))

        handler.removeCallbacks(runnable)

        /* Start Broadcast */
        val myIntent = Intent(ACTION_IS_BEATING)
        myIntent.putExtra(KEY_IS_BEATING, isBeating)
        Log.d(TAG, "onDestroy: Called: isBeating = $isBeating")
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent)

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getInterval() = ONE_MINUTE / BPM
}
