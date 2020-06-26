package com.maurozegarra.app.metronomex

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.maurozegarra.app.metronomex.MainActivity.Companion.KEY_INPUT

class MetronomeService : Service() {

    companion object {
        // This is the number of milliseconds in a minute
        const val ONE_MINUTE = 60_000L

        // This is the number of beats per minute
        const val BPM = 100
    }

    private var handler = Handler()
    private lateinit var runnable: Runnable

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra(KEY_INPUT)

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0)
        val notification =
            NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(input) // do the actual work
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

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getInterval() = ONE_MINUTE / BPM
}
