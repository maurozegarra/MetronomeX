package com.maurozegarra.app.metronomex

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.service.quicksettings.TileService.requestListeningState
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.maurozegarra.app.metronomex.receiver.StopReceiver

class BeatService : Service() {

    companion object {
        const val KEY_IS_BEATING = "key_is_beating"
        const val SHARED_PREFERENCES = "shared_preferences"
        const val ACTION_IS_BEATING = "com.maurozegarra.app.IS_BEATING"
        const val PREF_VOLUME = 6
        const val ACTION_STOP = "action_stop"

        // This is the number of beats per minute
        const val PREF_BPM = 100
    }

    private var handler = Handler()
    private lateinit var runnable: Runnable
    private var isBeating = false
    private val REQUEST_CODE = 0
    private val FLAGS = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isBeating = true

        /* Start Broadcast */
        val myIntent = Intent(ACTION_IS_BEATING)
        myIntent.putExtra(KEY_IS_BEATING, isBeating)
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent)

        val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_BEATING, isBeating)
        editor.apply()

        /* For QS Tile Update */
        requestListeningState(this, ComponentName(this, BeatTile::class.java))

        val openActivityIntent = Intent(this, MainActivity::class.java)
        val openActivityPendingIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE,
            openActivityIntent,
            FLAGS
        )

        /* Stop Action Button */
        val actionStopIntent = Intent(this, StopReceiver::class.java)
        actionStopIntent.action = ACTION_STOP
        val actionStopPendingIntent = PendingIntent.getBroadcast(
            this,
            REQUEST_CODE,
            actionStopIntent,
            FLAGS
        )

        /* Notification */
        val notification =
            NotificationCompat.Builder(this, getString(R.string.beats_notification_channel_id))
                .setSmallIcon(R.drawable.ic_metronome)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText("Beating")
                .setContentIntent(openActivityPendingIntent)
                .addAction(
                    R.mipmap.ic_launcher,
                    getString(R.string.action_button_stop),
                    actionStopPendingIntent
                )
                .build()

        startForeground(1, notification)

        /* Adjust system volume to 6 % */
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //Log.e(TAG, "Called: onStartCommand:isVolumeFixed ${am.isVolumeFixed}")

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            PREF_VOLUME,//am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )

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
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent)

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getInterval() = DateUtils.MINUTE_IN_MILLIS / PREF_BPM
}
