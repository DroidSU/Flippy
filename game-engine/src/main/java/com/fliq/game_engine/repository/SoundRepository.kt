package com.fliq.game_engine.repository

interface SoundRepository {
    fun startBackgroundMusic()
    fun pauseBackgroundMusic()
    fun stopBackgroundMusic()
    fun setMusicAllowed(allowed: Boolean)
    suspend fun pauseBackgroundMusicTemp(millis: Long)
    fun playBombSound()
    fun playGameOverSound()
    fun playBonusSound()
    fun playCoinTapSound()
    fun setMusicActivated(activated: Boolean)
    fun isMusicActivated(): Boolean
    fun release()
}
