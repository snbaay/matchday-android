package com.example.matchday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.usecase.ObserveSavedClubsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface SavedClubsUiState {
    data object Loading : SavedClubsUiState
    data object Error : SavedClubsUiState
    data class Content(val clubs: List<SavedClub>) : SavedClubsUiState
}

class SavedClubsViewModel(
    observeSavedClubs: ObserveSavedClubsUseCase
) : ViewModel() {
    val uiState = observeSavedClubs().map<List<SavedClub>, SavedClubsUiState> { clubs ->
        SavedClubsUiState.Content(clubs)
    }.catch { error ->
        if (error !is Exception || error is CancellationException) throw error
        emit(SavedClubsUiState.Error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedClubsUiState.Loading)
}
