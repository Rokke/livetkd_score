package talgoe.mobcon.live_tkd_score.database.tables

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "competitors", primaryKeys = ["tournament_id", "name", "club"], foreignKeys = [ForeignKey(entity = Tournament::class, parentColumns = ["id"], childColumns = ["tournament_id"], onDelete = ForeignKey.CASCADE)])
data class Competitor constructor(
    val tournament_id: Long,
    val name: String,
    val club: String="",
    val country: String="",
    val weight_class: String="",
    var updated: Long=0L
){
    val updateText:String get()=updated.toString()
}