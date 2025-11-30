package com.sujoy.flippy.repositories.game

import android.content.Context
import com.sujoy.flippy.utils.SoundPlayer

class SoundRepositoryImpl(context: Context) : SoundRepository {
    private val soundPlayer = SoundPlayer(context)

    override fun startBackgroundMusic() {
        soundPlayer.startBackgroundMusic()
    }

    override fun pauseBackgroundMusic() {
        soundPlayer.pauseBackgroundMusic()
    }

    override fun playBombSound() {
        soundPlayer.playBombSound()
    }

    override fun playGameOverSound() {
        soundPlayer.playGameOverSound()
    }

    override fun release() {
        soundPlayer.release()
    }
}