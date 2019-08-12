package talgoe.mobcon.live_tkd_score.database.tables


import androidx.room.*
import talgoe.mobcon.live_tkd_score.models.TournamentModel

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) var id: Long=0,
    var unique_key: String="",
    var name: String="",
    var country: String="",
    var place:String="",
    var timestamp:Long=0L,
    var competitors:Int=0,
    var last_fetched:Long=0L,
    @Ignore var dates: List<TournamentDate>?=null,
    @Ignore var downloaded_fights:Int ?=0,
    @Ignore var downloaded_competitors:Int ?=0
)
class TournamentAndDates {
    @Embedded
    var tournament: Tournament? = null

    @Relation(parentColumn = "id", entityColumn = "tournament_id")
    var dates: List<TournamentDate> = ArrayList()
}
fun TournamentAndDates.toModel():TournamentModel{
    return TournamentModel(tournament!!.id,tournament!!.unique_key,tournament!!.name,tournament!!.country,tournament!!.place,tournament!!.timestamp,dates.map{ it.toModel() },tournament!!.competitors,last_fetched = tournament!!.last_fetched)
}