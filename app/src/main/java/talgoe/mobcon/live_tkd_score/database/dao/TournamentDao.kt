package talgoe.mobcon.live_tkd_score.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import talgoe.mobcon.live_tkd_score.database.tables.Tournament
import talgoe.mobcon.live_tkd_score.database.tables.TournamentAndDates
import talgoe.mobcon.live_tkd_score.database.tables.TournamentDate

@Dao
interface TournamentDao {
/*
    @Query("SELECT * FROM tournaments")
    fun getTournaments(): LiveData<List<Tournament>>
//    @Query("SELECT t.id, t.unique_key, t.name, t.country, t.place, t.timestamp, d.id as date_it, d.date, d.timestamp as date_timestamp FROM tournaments t LEFT JOIN tournament_dates d")
//    fun getTournamentsWithDates(): LiveData<List<TournamentWithDates>>
    @Query("SELECT * FROM tournament_dates where tournament_id=:tournamentId")
    fun getTournamentDates(tournamentId: Long): List<TournamentDate>?
*/
    @Transaction
    @Query("SELECT * FROM tournaments")
    fun getTournamentWithDates(): LiveData<List<TournamentAndDates>>
    //    @Query("SELECT t.*, count(c.tournament_id), sum(d.fights) FROM tournaments t LEFT JOIN competitors c ON c.tournament_id=t.id LEFT JOIN tournament_dates d on d.tournament_id=t.id")
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insert(tournament: Tournament):Long
    @Query("UPDATE tournaments SET last_fetched=:lastFetched WHERE id=:tournamentId")
    suspend fun updateFetchDate(tournamentId: Long, lastFetched: Long)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDates(tournamentDates: List<TournamentDate>)
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insert(tournamentDate: TournamentDate):Long
    @Update
    fun updateTournament(tournamentDate: TournamentDate)
    @Update
    fun updateTournament(tournament: Tournament)
    @Delete
    suspend fun delete(tournament: Tournament)
    @Query("SELECT * FROM tournaments WHERE unique_key = :tournamentKey")
    fun fetchTournamentByKey(tournamentKey: String):Tournament
/*
    data class TournamentWithDates(
        val id: Long,
        val unique_key: String,
        val name: String,
        val country: String,
        val place:String,
        val timestamp:Long,
        val date_id: Long,
        val date: String,
        val date_timestamp: Long
    )
*/
}