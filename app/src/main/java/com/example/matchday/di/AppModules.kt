package com.example.matchday.di

import androidx.room.Room
import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.data.local.LocalSavedClubsRepository
import com.example.matchday.data.local.SavedClubDao
import com.example.matchday.data.local.SavedClubsDatabase
import com.example.matchday.data.remote.ClubSearchApi
import com.example.matchday.data.remote.RemoteClubSearchRepository
import com.example.matchday.data.remote.createClubSearchApi
import com.example.matchday.domain.repository.ClubSearchRepository
import com.example.matchday.domain.repository.SavedClubsRepository
import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.domain.usecase.AddTeamUseCase
import com.example.matchday.domain.usecase.CreateMatchUseCase
import com.example.matchday.domain.usecase.CreateTournamentUseCase
import com.example.matchday.domain.usecase.CalculateStandingsUseCase
import com.example.matchday.domain.usecase.GetStandingsUseCase
import com.example.matchday.domain.usecase.ObserveSavedClubsUseCase
import com.example.matchday.domain.usecase.ObserveTournamentsUseCase
import com.example.matchday.domain.usecase.SaveScoreUseCase
import com.example.matchday.domain.usecase.SearchClubsUseCase
import com.example.matchday.presentation.viewmodel.AppViewModel
import com.example.matchday.presentation.viewmodel.ClubSearchViewModel
import com.example.matchday.presentation.viewmodel.SavedClubsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::InMemoryTournamentDataSource)
    singleOf(::InMemoryTournamentRepository) bind TournamentRepository::class
    single<ClubSearchApi> { createClubSearchApi() }
    singleOf(::RemoteClubSearchRepository) bind ClubSearchRepository::class
    single {
        Room.databaseBuilder(
            androidContext(),
            SavedClubsDatabase::class.java,
            SavedClubsDatabase.DATABASE_NAME
        ).build()
    }
    single<SavedClubDao> { get<SavedClubsDatabase>().savedClubDao() }
    singleOf(::LocalSavedClubsRepository) bind SavedClubsRepository::class
}

val domainModule = module {
    singleOf(::ObserveTournamentsUseCase)
    singleOf(::CreateTournamentUseCase)
    singleOf(::AddTeamUseCase)
    singleOf(::CreateMatchUseCase)
    singleOf(::SaveScoreUseCase)
    singleOf(::CalculateStandingsUseCase)
    singleOf(::GetStandingsUseCase)
    singleOf(::SearchClubsUseCase)
    singleOf(::ObserveSavedClubsUseCase)
}

val presentationModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::ClubSearchViewModel)
    viewModelOf(::SavedClubsViewModel)
}
