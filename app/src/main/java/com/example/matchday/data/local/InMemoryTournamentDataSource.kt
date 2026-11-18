package com.example.matchday.data.local

class InMemoryTournamentDataSource {
    private var snapshot = TournamentSnapshotDto()

    internal fun read(): TournamentSnapshotDto = snapshot

    internal fun write(value: TournamentSnapshotDto) {
        snapshot = value
    }
}
