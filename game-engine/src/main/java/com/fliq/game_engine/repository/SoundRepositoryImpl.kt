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
    private var isMusicActivated = false

    private fun isSoundEnabled() = settingsRepository.getGameSoundEnabled()

    override fun startBackgroundMusic() {
        isMusicActivated = true
        if (isSoundEnabled() && isMusicAllowed) {
            soundPlayer.startBackgroundMusic()
        }
    }

    override fun pauseBackgroundMusic() {
        soundPlayer.pauseBackgroundMusic()
    }

    override fun stopBackgroundMusic() {
        isMusicActivated = false
        soundPlayer.stopBackgroundMusic()
    }

    override fun setMusicAllowed(allowed: Boolean) {
        isMusicAllowed = allowed
    }

    override fun setMusicActivated(activated: Boolean) {
        isMusicActivated = activated
    }

    override fun isMusicActivated(): Boolean = isMusicActivated

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

    override fun playBonusSound() {
        if (isSoundEnabled()) {
            soundPlayer.playBonusSound()
        }
    }

    override fun playCoinTapSound() {
        if (isSoundEnabled()) {
            soundPlayer.playCoinTapSound()
        }
    }

    override fun release() {
        soundPlayer.release()
    }
}
