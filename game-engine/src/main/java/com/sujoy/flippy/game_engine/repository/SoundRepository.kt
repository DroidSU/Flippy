package com.sujoy.flippy.game_engine.repository

interface SoundRepository {
    fun startBackgroundMusic()
    fun pauseBackgroundMusic()
    fun playBombSound()
    fun playGameOverSound()
    fun release()
}
