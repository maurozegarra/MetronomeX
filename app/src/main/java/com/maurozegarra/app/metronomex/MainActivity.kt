package com.maurozegarra.app.metronomex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_INPUT = "key_input"
    }

    private lateinit var editTextInput: EditText
    private lateinit var buttonToggle: Button
    private var eventPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.edit_text_input)
        buttonToggle = findViewById(R.id.button_toggle)
    }

    fun toggleService(view: View) {
        // toggle previous state
        eventPlay = !eventPlay

        if (eventPlay)
            startService()
        else
            pauseService()
    }

    private fun startService() {
        val input = editTextInput.text.toString()

        val beatIntent = Intent(this, MetronomeService::class.java)
        beatIntent.putExtra(KEY_INPUT, input)

        startService(beatIntent)
        buttonToggle.text = getString(R.string.pause)
    }

    private fun pauseService() {
        val beatIntent = Intent(this, MetronomeService::class.java)
        stopService(beatIntent)
        buttonToggle.text = getString(R.string.start)
    }
}
