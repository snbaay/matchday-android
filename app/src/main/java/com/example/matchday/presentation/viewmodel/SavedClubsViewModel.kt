package com.example.matchday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.usecase.DeleteSavedClubUseCase
import com.example.matchday.domain.usecase.ObserveSavedClubsUseCase
import com.example.matchday.domain.usecase.SaveClubUseCase
import com.example.matchday.domain.usecase.UpdateClubNoteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SavedClubsUiState {
    data object Loading : SavedClubsUiState
    data object Error : SavedClubsUiState
    data class Content(val clubs: List<SavedClub>) : SavedClubsUiState
}

sealed interface SavedClubsMessage {
    data class ClubSaved(val name: String) : SavedClubsMessage
    data object WriteFailed : SavedClubsMessage
}

class SavedClubsViewModel(
    observeSavedClubs: ObserveSavedClubsUseCase,
    private val saveClub: SaveClubUseCase,
    private val updateClubNote: UpdateClubNoteUseCase,
    private val deleteSavedClub: DeleteSavedClubUseCase
) : ViewModel() {
    val uiState = observeSavedClubs().map<List<SavedClub>, SavedClubsUiState> { clubs ->
        SavedClubsUiState.Content(clubs)
    }.catch { error ->
        if (error !is Exception || error is CancellationException) throw error
        emit(SavedClubsUiState.Error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedClubsUiState.Loading)

    private val messageChannel = Channel<SavedClubsMessage>(Channel.BUFFERED)
    val messages = messageChannel.receiveAsFlow()

    fun save(club: Club) = write(SavedClubsMessage.ClubSaved(club.name)) { saveClub(club) }

    fun updateNote(clubId: String, note: String) = write { updateClubNote(clubId, note) }

    fun delete(clubId: String) = write { deleteSavedClub(clubId) }

    private fun write(success: SavedClubsMessage? = null, operation: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                operation()
                success?.let { messageChannel.send(it) }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                messageChannel.send(SavedClubsMessage.WriteFailed)
            }
        }
    }
}
