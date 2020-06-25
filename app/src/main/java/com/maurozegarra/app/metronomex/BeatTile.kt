package com.maurozegarra.app.metronomex

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class BeatTile : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()

        qsTile.state = Tile.STATE_INACTIVE

        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()

        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_metronome)
        qsTile.label = getString(R.string.label)
        qsTile.contentDescription = getString(R.string.label)

        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (qsTile.state == Tile.STATE_INACTIVE) {
            // Turn on
            qsTile.state = Tile.STATE_ACTIVE
            startBeat()
        } else {
            // Turn off
            qsTile.state = Tile.STATE_INACTIVE
            stopBeat()
        }

        qsTile.updateTile()
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
