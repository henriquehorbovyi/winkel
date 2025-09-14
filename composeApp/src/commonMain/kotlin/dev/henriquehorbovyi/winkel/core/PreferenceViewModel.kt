package dev.henriquehorbovyi.winkel.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.henriquehorbovyi.winkel.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenceViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferencesRepository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleTheme() {
        viewModelScope.launch {
            preferencesRepository.toggleTheme()
        }
    }
}