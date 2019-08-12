package talgoe.mobcon.live_tkd_score.network

import talgoe.mobcon.live_tkd_score.database.tables.Tournament
import talgoe.mobcon.live_tkd_score.database.tables.TournamentAndDates
import talgoe.mobcon.live_tkd_score.database.tables.TournamentDate

data class TournamentApi(
    val timestamp: Long,
    val tournaments: List<TournamentList>
){
    data class TournamentList(
        val key: String,
        val name: String?="",
        val country: String?="",
        val place:String?="",
        val timestamp:Long?=0L,
        val fight_dates:List<TournamentDate>?=null,
        val competitors:Int=0
    )
    data class TournamentDate(
        val date:String?="",
        val fights:Int=0,
        val timestamp:Long?=0
    )
}
fun TournamentApi.TournamentList.asTournamentTable(tournament_id: Long=0): Tournament = Tournament(
    id=tournament_id,
    unique_key = key,
    name = name ?: "",
    country = country ?: "",
    place = place ?: "",
    timestamp = timestamp ?: 0L,
    competitors = competitors
)
fun TournamentApi.TournamentDate.asTournamentDateTable(tournament_id: Long=0, tournament_date_id: Long=0): TournamentDate = TournamentDate(
    id=tournament_date_id,
    tournament_id = tournament_id,
    date = date ?: "",
    timestamp = timestamp ?: 0L,
    fights = fights
)
