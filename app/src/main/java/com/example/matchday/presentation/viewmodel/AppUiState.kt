package com.example.matchday.presentation.viewmodel

import com.example.matchday.domain.model.AppData

data class AppUiState(val data: AppData) {
    val isEmpty: Boolean get() = data.tournaments.isEmpty()
}
