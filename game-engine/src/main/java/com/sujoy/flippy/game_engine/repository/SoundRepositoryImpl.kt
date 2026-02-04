package com.sujoy.flippy.game_engine.repository

import android.content.Context
import com.sujoy.flippy.game_engine.utils.SoundPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SoundRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SoundRepository {
    private val soundPlayer = SoundPlayer(context)

    override fun startBackgroundMusic() = soundPlayer.startBackgroundMusic()
    override fun pauseBackgroundMusic() = soundPlayer.pauseBackgroundMusic()
    override suspend fun pauseBackgroundMusicTemp(seconds: Long) = soundPlayer.pauseBackgroundMusicTemp(seconds)
    override fun playBombSound() = soundPlayer.playBombSound()
    override fun playGameOverSound() = soundPlayer.playGameOverSound()
    override fun release() = soundPlayer.release()
}
