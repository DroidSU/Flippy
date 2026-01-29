package com.sujoy.flippy.game_engine.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.sujoy.flippy.game_engine.R
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
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
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

        backgroundMusicPlayer = MediaPlayer.create(context, R.raw.sound_island_clearing).apply {
            isLooping = true
            setVolume(0.3f, 0.3f)
        }
    }

    fun playBombSound() {
        if (isEffectsLoaded) {
            soundPool.play(bombSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
            duckBackgroundMusic()
        }
    }

    fun playGameOverSound() {
        if (isEffectsLoaded) {
            stopBackgroundMusic()
            soundPool.play(gameOverSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun startBackgroundMusic() {
        backgroundMusicPlayer?.setVolume(0.3f, 0.3f)
        backgroundMusicPlayer?.takeIf { !it.isPlaying }?.start()
    }

    fun pauseBackgroundMusic() {
        backgroundMusicPlayer?.pause()
    }

    fun stopBackgroundMusic() {
        if (backgroundMusicPlayer?.isPlaying == true) {
            backgroundMusicPlayer?.pause()
            backgroundMusicPlayer?.seekTo(0)
        }
    }

    private fun duckBackgroundMusic() {
        scope.launch {
            backgroundMusicPlayer?.setVolume(0.05f, 0.05f)
            delay(1000L)
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
