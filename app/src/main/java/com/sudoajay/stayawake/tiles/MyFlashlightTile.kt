package com.sudoajay.stayawake.tiles

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

import com.sudoajay.stayawake.R
import com.sudoajay.stayawake.ui.mainActivity.MainActivity
import com.sudoajay.stayawake.ui.transparent.TransparentClass
import com.sudoajay.stayawake.utill.Toaster


@RequiresApi(Build.VERSION_CODES.N)
class MyFlashlightTile: TileService(){


    override fun onClick() {

        val intent = Intent(this, TransparentClass::class.java)
        // Called when the user click the tile
        super.onClick()
        val tile = qsTile

        val isActive =
            tile.state == Tile.STATE_ACTIVE
        if (isActive) {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.shortcut_flash_text)


            intent.action = MainActivity.stopFlashId

        } else {
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.stop_text)
            intent.action = MainActivity.startFlashId
        }

        tile.icon = Icon.createWithResource(
            this,
            R.drawable.ic_flashlight
        )

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        tile.updateTile()

        // Called when the user click the tile
    }


    override fun onTileAdded() {
        super.onTileAdded()

        Toaster.showToast(
            applicationContext,
            getString(R.string.shortcut_flash_text) +" "+ getString(R.string.added_text)
        )

        // Do something when the user add the Tile
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        Toaster.showToast(
            applicationContext,
            getString(R.string.shortcut_flash_text)+" " + getString(R.string.removed_text)
        )

        // Do something when the user removes the Tile
    }


    override fun onStartListening() {
        super.onStartListening()
        // Called when the Tile becomes visible

        val tile = qsTile
        val value =
            MainActivity.isFlashActive(applicationContext)

        if (value) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = getString(R.string.stop_text)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = getString(R.string.shortcut_flash_text)
        }

        tile.icon = Icon.createWithResource(this, R.drawable.ic_flashlight)

        tile.updateTile()
    }



}