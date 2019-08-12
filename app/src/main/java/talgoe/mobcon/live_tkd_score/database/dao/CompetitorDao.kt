package talgoe.mobcon.live_tkd_score.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import talgoe.mobcon.live_tkd_score.database.tables.Competitor

@Dao
interface CompetitorDao {
	@Query("SELECT * FROM competitors WHERE tournament_id = :tournamentId ORDER BY updated DESC, weight_class")
	fun fetchLiveCompetitors(tournamentId: Long): LiveData<List<Competitor>>
	@Insert(onConflict= OnConflictStrategy.IGNORE)
	suspend fun insert(competitors: List<Competitor>)
}