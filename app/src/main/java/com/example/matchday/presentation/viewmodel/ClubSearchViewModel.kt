package com.example.matchday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import com.example.matchday.domain.usecase.SearchClubsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClubSearchUiState(
    val query: String = "",
    val result: ClubSearchResult = ClubSearchResult.Idle
)

sealed interface ClubSearchResult {
    data object Idle : ClubSearchResult
    data class Success(val clubs: List<Club>) : ClubSearchResult
}

class ClubSearchViewModel(
    private val searchClubs: SearchClubsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClubSearchUiState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search() {
        submit(uiState.value.query.trim())
    }

    private fun submit(query: String) {
        if (query.isBlank()) return
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            val result = try {
                ClubSearchResult.Success(searchClubs(query))
            } catch (_: ClubSearchException) {
                ClubSearchResult.Idle
            }
            _uiState.update { it.copy(result = result) }
        }
    }
}
