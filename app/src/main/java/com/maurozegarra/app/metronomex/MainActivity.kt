package com.maurozegarra.app.metronomex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_INPUT = "key_input"
    }

    private lateinit var editTextInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.edit_text_input)
    }

    fun startService(view: View) {
        val input = editTextInput.text.toString()

        val serviceIntent = Intent(this, MetronomeService::class.java)
        serviceIntent.putExtra(KEY_INPUT, input)

        startService(serviceIntent)
    }

    fun pauseService(view: View) {
        val serviceIntent = Intent(this, MetronomeService::class.java)
        stopService(serviceIntent)
    }
}
