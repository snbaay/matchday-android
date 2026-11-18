package com.example.matchday.di

import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.domain.usecase.AddTeamUseCase
import com.example.matchday.domain.usecase.CreateMatchUseCase
import com.example.matchday.domain.usecase.CreateTournamentUseCase
import com.example.matchday.domain.usecase.CalculateStandingsUseCase
import com.example.matchday.domain.usecase.GetStandingsUseCase
import com.example.matchday.domain.usecase.ObserveTournamentsUseCase
import com.example.matchday.domain.usecase.SaveScoreUseCase
import com.example.matchday.presentation.viewmodel.AppViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::InMemoryTournamentDataSource)
    singleOf(::InMemoryTournamentRepository) bind TournamentRepository::class
}

val domainModule = module {
    singleOf(::ObserveTournamentsUseCase)
    singleOf(::CreateTournamentUseCase)
    singleOf(::AddTeamUseCase)
    singleOf(::CreateMatchUseCase)
    singleOf(::SaveScoreUseCase)
    singleOf(::CalculateStandingsUseCase)
    singleOf(::GetStandingsUseCase)
}

val presentationModule = module {
    viewModelOf(::AppViewModel)
}
