package com.sujoy.flippy.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.sujoy.flippy.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundPlayer(context: Context) {

    private var soundPool: SoundPool
    private var bombSoundId: Int = 0
    private var gameOverSoundId: Int = 0
    private var isEffectsLoaded = false

    private var backgroundMusicPlayer: MediaPlayer? = null

    // Coroutine scope for managing audio ducking delays
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        // --- SoundPool for short effects ---
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isEffectsLoaded = true
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            bombSoundId = soundPool.load(context, R.raw.sound_error, 1)
            gameOverSoundId = soundPool.load(context, R.raw.sound_game_over, 1)
        }

        // --- MediaPlayer for background music ---
        backgroundMusicPlayer = MediaPlayer.create(context, R.raw.sound_island_clearing).apply {
            isLooping = true
            setVolume(0.3f, 0.3f) // Default low volume
        }
    }

    fun playBombSound() {
        if (isEffectsLoaded) {
            soundPool.play(bombSoundId, 1.0f, 1.0f, 1, 0, 1.0f)

            // Duck the background music
            duckBackgroundMusic()
        }
    }

    fun playGameOverSound() {
        if (isEffectsLoaded) {
            stopBackgroundMusic() // Stop music completely on game over
            soundPool.play(gameOverSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    // --- Background Music Controls ---

    fun startBackgroundMusic() {
        // Reset volume to default and start playing
        backgroundMusicPlayer?.setVolume(0.3f, 0.3f)
        backgroundMusicPlayer?.takeIf { !it.isPlaying }?.start()
    }

    fun pauseBackgroundMusic() {
        backgroundMusicPlayer?.pause()
    }

    fun stopBackgroundMusic() {
        if (backgroundMusicPlayer?.isPlaying == true) {
            backgroundMusicPlayer?.pause()
            backgroundMusicPlayer?.seekTo(0) // Rewind to the start
        }
    }

    private fun duckBackgroundMusic() {
        scope.launch {
            // Lower the volume
            backgroundMusicPlayer?.setVolume(0.05f, 0.05f)

            // Wait for a second (let the bomb sound finish)
            delay(1000L)

            // Restore the volume
            backgroundMusicPlayer?.setVolume(0.3f, 0.3f)
        }
    }

    fun release() {
        soundPool.release()
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
    }
}
