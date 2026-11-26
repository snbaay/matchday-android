package com.example.matchday.data.local

class InMemoryTournamentDataSource {
    private var snapshot = TournamentSnapshotDto()

    fun read(): TournamentSnapshotDto = snapshot

    fun write(value: TournamentSnapshotDto) {
        snapshot = value
    }
}
