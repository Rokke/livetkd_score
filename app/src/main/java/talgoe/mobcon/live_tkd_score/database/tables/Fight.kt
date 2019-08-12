package talgoe.mobcon.live_tkd_score.database.tables

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "fights", primaryKeys = ["tournament_date_id", "round"], foreignKeys = [ForeignKey(entity = TournamentDate::class, parentColumns = ["id"], childColumns = ["tournament_date_id"], onDelete = ForeignKey.CASCADE)])
data class Fight constructor(
    val tournament_date_id: Long,
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