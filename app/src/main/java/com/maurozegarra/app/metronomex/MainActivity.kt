package com.maurozegarra.app.metronomex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.maurozegarra.app.metronomex.BeatService.Companion.ACTION_IS_BEATING
import com.maurozegarra.app.metronomex.BeatService.Companion.KEY_IS_BEATING
import com.maurozegarra.app.metronomex.BeatService.Companion.SHARED_PREFERENCES

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_FROM_ACTIVITY = "key_from_activity"
    }

    private lateinit var editTextInput: EditText
    private lateinit var buttonToggle: Button
    private var isBeating = false
    private var fromActivity = false

    /* Receiver */
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                isBeating = intent.getBooleanExtra(KEY_IS_BEATING, false)

                val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean(KEY_IS_BEATING, isBeating)
                editor.apply()

                updateButton()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.edit_text_input)
        buttonToggle = findViewById(R.id.button_toggle)

        /* Register Receiver */
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, IntentFilter(ACTION_IS_BEATING))

        updateButton()
    }

    private fun updateButton() {
        val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        isBeating = prefs.getBoolean(KEY_IS_BEATING, false)
        buttonToggle.text = if (isBeating) "Pause" else "Start"
    }

    fun startPauseButton(view: View) {
        // toggle previous state
        isBeating = !isBeating
        fromActivity = true

        val prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_BEATING, isBeating)
        editor.putBoolean(KEY_FROM_ACTIVITY, fromActivity)
        editor.apply()

        if (isBeating)
            startBeatService()
        else
            pauseBeatService()
    }

    private fun startBeatService() {
        val beatIntent = Intent(this, BeatService::class.java)
        startService(beatIntent)

        updateButton()
    }

    private fun pauseBeatService() {
        val beatIntent = Intent(this, BeatService::class.java)
        stopService(beatIntent)

        updateButton()
    }

    override fun onDestroy() {
        /* Unregister Receiver */
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(receiver)

        super.onDestroy()
    }
}
