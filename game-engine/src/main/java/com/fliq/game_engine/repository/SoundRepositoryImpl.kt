package com.fliq.game_engine.repository

import android.content.Context
import com.fliq.core.settings.SettingsRepository
import com.fliq.game_engine.utils.SoundPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SoundRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsRepository: SettingsRepository
) : SoundRepository {
    private val soundPlayer = SoundPlayer(context)
    private var isMusicAllowed = true

    private fun isSoundEnabled() = settingsRepository.getGameSoundEnabled()

    override fun startBackgroundMusic() {
        if (isSoundEnabled() && isMusicAllowed) {
            soundPlayer.startBackgroundMusic()
        }
    }

    override fun pauseBackgroundMusic() {
        soundPlayer.pauseBackgroundMusic()
    }

    override fun setMusicAllowed(allowed: Boolean) {
        isMusicAllowed = allowed
    }

    override suspend fun pauseBackgroundMusicTemp(millis: Long) {
        if (isSoundEnabled()) {
            soundPlayer.pauseBackgroundMusicTemp(millis)
        }
    }

    override fun playBombSound() {
        if (isSoundEnabled()) {
            soundPlayer.playBombSound()
        }
    }

    override fun playGameOverSound() {
        if (isSoundEnabled()) {
            soundPlayer.playGameOverSound()
        }
    }

    override fun release() {
        soundPlayer.release()
    }
}
