package com.fliq.game_engine.repository

interface SoundRepository {
    fun startBackgroundMusic()
    fun pauseBackgroundMusic()
    fun setMusicAllowed(allowed: Boolean)
    suspend fun pauseBackgroundMusicTemp(millis: Long)
    fun playBombSound()
    fun playGameOverSound()
    fun release()
}
