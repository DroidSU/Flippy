package com.sujoy.flippy.game_engine.repository

interface SoundRepository {
    fun startBackgroundMusic()
    fun pauseBackgroundMusic()
    suspend fun pauseBackgroundMusicTemp(millis: Long)
    fun playBombSound()
    fun playGameOverSound()
    fun release()
}
