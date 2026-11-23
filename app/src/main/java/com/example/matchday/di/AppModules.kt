package com.example.matchday.di

import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.data.remote.ClubSearchApi
import com.example.matchday.data.remote.RemoteClubSearchRepository
import com.example.matchday.domain.repository.ClubSearchRepository
import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.domain.usecase.AddTeamUseCase
import com.example.matchday.domain.usecase.CreateMatchUseCase
import com.example.matchday.domain.usecase.CreateTournamentUseCase
import com.example.matchday.domain.usecase.CalculateStandingsUseCase
import com.example.matchday.domain.usecase.GetStandingsUseCase
import com.example.matchday.domain.usecase.ObserveTournamentsUseCase
import com.example.matchday.domain.usecase.SaveScoreUseCase
import com.example.matchday.domain.usecase.SearchClubsUseCase
import com.example.matchday.presentation.viewmodel.AppViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {
    singleOf(::InMemoryTournamentDataSource)
    singleOf(::InMemoryTournamentRepository) bind TournamentRepository::class
    single<ClubSearchApi> {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .baseUrl("https://api.openligadb.de/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ClubSearchApi::class.java)
    }
    singleOf(::RemoteClubSearchRepository) bind ClubSearchRepository::class
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
}

val presentationModule = module {
    viewModelOf(::AppViewModel)
}
