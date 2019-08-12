package talgoe.mobcon.live_tkd_score.network

import talgoe.mobcon.live_tkd_score.database.tables.Competitor

data class CompetitorApi(
    val divisions: List<Division>
){
    data class Division(
        var division_name: String,
        val athletes: List<Athlete>
    )
    data class Athlete(
        val name: String,
        val club: String="",
        val country: String="",
        var updated: Long=0L
    )
}/*
    val timestamp: Long,
    val competitors: List<CompetitorList>
){
    data class CompetitorList(
        val name: String,
        val club: String="",
        val country: String="",
        val weightClass: String="",
        var updated: Long=0L
    )
}
fun List<CompetitorApi.CompetitorList>.asDomainModel(tournamentId: Long): List<Competitor> {
    return map {
        Competitor(
            tournament_id = tournamentId,
            name = it.name,
            club = it.club,
            country = it.country,
            weight_class = it.weightClass,
            updated = it.updated
        )
    }.toList()
}
*/
