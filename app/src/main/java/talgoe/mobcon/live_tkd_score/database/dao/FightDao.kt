package talgoe.mobcon.live_tkd_score.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import talgoe.mobcon.live_tkd_score.database.tables.Fight

@Dao
interface FightDao {
    @Query("SELECT * FROM fights WHERE tournament_date_id = :tournamentDateId")
    fun fetchLiveFights(tournamentDateId: Long): LiveData<List<Fight>>
    @Insert(onConflict= OnConflictStrategy.IGNORE)
    suspend fun insert(fights: List<Fight>)
}