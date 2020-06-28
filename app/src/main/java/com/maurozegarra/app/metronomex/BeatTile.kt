package com.maurozegarra.app.metronomex

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import com.maurozegarra.app.metronomex.MainActivity.Companion.KEY_FROM_ACTIVITY
import com.maurozegarra.app.metronomex.MetronomeService.Companion.KEY_IS_BEATING

@RequiresApi(Build.VERSION_CODES.N)
class BeatTile : TileService() {

    private var isBeating = false
    //private var fromActivity = false

    override fun onTileAdded() {
        Log.d(TAG, "Called: onTileAdded")
        super.onTileAdded()

        qsTile.state = Tile.STATE_INACTIVE

        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()

        val prefs = getSharedPreferences(MetronomeService.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        isBeating = prefs.getBoolean(KEY_IS_BEATING, false)
        //fromActivity = prefs.getBoolean(KEY_FROM_ACTIVITY, false)

        Log.e(TAG, "onStartListening: Called, isBeating = $isBeating")

        if (isBeating) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Beating..."
            //qsTile.subtitle = "100 BPM"
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Beat"
        }

        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_metronome)
        // Apply for accessibility readers
        //qsTile.contentDescription = if (eventPlay) "Beating.." else "Beat"

        //qsTile.state = if (eventPlay) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

        qsTile.updateTile()
    }

    override fun onClick() {
        Log.d(TAG, "onClick: Called")
        super.onClick()

        if (isBeating) {
            stopBeat()
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Beat"
        } else {
            startBeat()
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Beating..."
            qsTile.subtitle = "100 BPM"
        }

        qsTile.updateTile()
        /*
        if (qsTile.state == Tile.STATE_INACTIVE) {
            // Turn on
            startBeat()
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Beating..."
        } else {
            // Turn off
            stopBeat()
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Beat"
        }
        */


        /*

        val prefs = getSharedPreferences(MetronomeService.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        eventPlay = prefs.getBoolean(KEY_EVENT_PLAY, false)

        if (eventPlay) {
            stopBeat()
        } else {
            startBeat()
        }

        qsTile.updateTile()
        //Toast.makeText(applicationContext, getString(R.string.display_msg), Toast.LENGTH_LONG).show();
        */
    }

    private fun startBeat() {
        val beatIntent = Intent(this, MetronomeService::class.java)
        startService(beatIntent)
    }

    private fun stopBeat() {
        val beatIntent = Intent(this, MetronomeService::class.java)
        stopService(beatIntent)
    }
}
