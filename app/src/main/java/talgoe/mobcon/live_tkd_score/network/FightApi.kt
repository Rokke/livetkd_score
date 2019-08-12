package talgoe.mobcon.live_tkd_score.network

import talgoe.mobcon.live_tkd_score.database.tables.Fight

data class FightApi(
    val timestamp: Long,
    val fights: List<FightList>
){
    data class FightList(
        val round: String,
        val chong: String,
        val hong: String="",
        val chongcountry: String="",
        val hongcountry: String="",
        val chongclub: String="",
        val hongclub: String="",
        val points: String="",
        val result: String="",
        val winner: String="",
        val fighttime: String="",
        val roundtype: String="",
        val classname: String=""
    )
}
fun List<FightApi.FightList>.asDomainModel(tournamentDateId: Long): List<Fight> {
    return map {
        Fight(
            tournament_date_id = tournamentDateId,
            round = it.round,
            chong = it.chong,
            hong = it.hong,
            chongcountry = it.chongcountry,
            hongcountry = it.hongcountry,
            chongclub = it.chongclub,
            hongclub = it.hongclub,
            points = it.points,
            result = it.result,
            winner = it.winner,
            fighttime = it.fighttime,
            roundtype = it.roundtype,
            classname = it.classname
        )
    }.toList()
}
