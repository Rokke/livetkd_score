package talgoe.mobcon.live_tkd_score.database.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import talgoe.mobcon.live_tkd_score.database.CacheDatabase
import talgoe.mobcon.live_tkd_score.models.TournamentModel

@Entity(tableName = "tournament_dates", foreignKeys = [ForeignKey(entity = Tournament::class, parentColumns = ["id"], childColumns = ["tournament_id"], onDelete = ForeignKey.CASCADE)], indices = [Index(value=["tournament_id"])])
data class TournamentDate(
		@PrimaryKey(autoGenerate = true) val id: Long,
		var tournament_id: Long,
		val date: String,
		val timestamp: Long,
		val fights: Int
)
fun TournamentDate.toModel():TournamentModel.TournamentDateModel=TournamentModel.TournamentDateModel(id,date,timestamp,fights)
