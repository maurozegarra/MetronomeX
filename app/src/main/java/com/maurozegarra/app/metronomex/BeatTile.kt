package com.maurozegarra.app.metronomex

import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.maurozegarra.app.metronomex.MetronomeService.Companion.KEY_IS_BEATING

@RequiresApi(Build.VERSION_CODES.N)
class BeatTile : TileService() {
    private var isBeating = false

    override fun onTileAdded() {
        super.onTileAdded()

        qsTile.state = Tile.STATE_INACTIVE

        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()

        val prefs = getSharedPreferences(MetronomeService.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        isBeating = prefs.getBoolean(KEY_IS_BEATING, false)

        if (isBeating) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Beating..."
            qsTile.subtitle = "100 BPM"
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Beat"
            qsTile.subtitle = ""
        }

        // El ícono se podría cambiar cada 0.5 segundos, logrando una animación
        //qsTile.icon = Icon.createWithResource(this, R.drawable.ic_metronome)

        // for accessibility readers
        //qsTile.contentDescription = if (eventPlay) "Beating.." else "Beat"

        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (isBeating) {
            stopBeat()
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Beat"
            qsTile.subtitle = ""
        } else {
            startBeat()
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Beating..."
            qsTile.subtitle = "100 BPM"
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
