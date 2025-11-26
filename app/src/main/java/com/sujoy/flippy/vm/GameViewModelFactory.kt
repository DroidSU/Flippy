package com.sujoy.flippy.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sujoy.flippy.utils.SoundPlayer

// Update the factory to accept SoundPlayer
class GameViewModelFactory(
    private val application: Application,
    private val soundPlayer: SoundPlayer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass the SoundPlayer to the ViewModel
            return GameViewModel(application, soundPlayer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
