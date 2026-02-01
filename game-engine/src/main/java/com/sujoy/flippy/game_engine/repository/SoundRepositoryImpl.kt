package com.sujoy.flippy.game_engine.repository

import android.content.Context
import com.sujoy.flippy.game_engine.utils.SoundPlayer

class SoundRepositoryImpl(context: Context) : SoundRepository {
    private val soundPlayer = SoundPlayer(context)

    override fun startBackgroundMusic() = soundPlayer.startBackgroundMusic()
    override fun pauseBackgroundMusic() = soundPlayer.pauseBackgroundMusic()
    override suspend fun pauseBackgroundMusicTemp(seconds: Long) = soundPlayer.pauseBackgroundMusicTemp(seconds)
    override fun playBombSound() = soundPlayer.playBombSound()
    override fun playGameOverSound() = soundPlayer.playGameOverSound()
    override fun release() = soundPlayer.release()
}
