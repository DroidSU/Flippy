package com.sujoy.flippy.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sujoy.flippy.auth.repository.AuthRepositoryImpl
import com.sujoy.flippy.auth.viewmodel.AuthViewModel
import com.sujoy.flippy.game_engine.repository.SoundRepository
import com.sujoy.flippy.game_engine.viewmodel.GameViewModel

class ViewModelFactory(
    private val soundRepository: SoundRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                GameViewModel(soundRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AuthViewModel(AuthRepositoryImpl()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
