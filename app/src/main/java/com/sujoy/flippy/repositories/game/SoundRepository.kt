package com.sujoy.flippy.repositories.game

interface SoundRepository {
    fun startBackgroundMusic()
    fun pauseBackgroundMusic()
    fun playBombSound()
    fun playGameOverSound()
    fun release()
}
