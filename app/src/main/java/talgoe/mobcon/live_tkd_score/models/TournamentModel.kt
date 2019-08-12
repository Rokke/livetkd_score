package talgoe.mobcon.live_tkd_score.models

import android.os.Parcel
import android.os.Parcelable
import talgoe.mobcon.live_tkd_score.database.tables.Tournament

data class TournamentModel(
	val id:Long,
	val unique_key: String,
	val name: String,
	val country: String="",
	val place:String="",
	val timestamp:Long=0,
	var dates: List<TournamentDateModel>?=null,
	var competitors: Int=0,
	var last_fetched:Long=0L
):Parcelable{
	val fights:String get()="0"
	val joinedDates:String get()= if(dates?.count() ?: 0>2) "${dates!!.first().date}-${dates!!.last().date}" else dates?.joinToString(",") { it.date } ?: ""
	val tournamentId:String get()= if(unique_key.startsWith("http")) "MAReg_${unique_key.split("/")[4]}" else unique_key
	val totalFights:Int get()=dates?.sumBy { it.fights } ?: 0
	val textInfo:String get()=
		if(totalFights>0) dates!!.sumBy { it.fights }.toString()
		else competitors.toString()
	constructor(parcel: Parcel) : this(
		parcel.readLong(),
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readString()!!,
		parcel.readLong(),
		parcel.createTypedArrayList(TournamentDateModel),
		parcel.readInt(),
		parcel.readLong()
	)

	data class TournamentDateModel(
		val id:Long,
		val date: String,
		val timestamp: Long,
		val fights: Int
	):Parcelable {
		constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString()!!,
			parcel.readLong(),
			parcel.readInt()
		)
		override fun writeToParcel(parcel: Parcel, flags: Int) {
			parcel.writeLong(id)
			parcel.writeString(date)
			parcel.writeLong(timestamp)
			parcel.writeInt(fights)
		}
		override fun describeContents(): Int {
			return 0
		}
		companion object CREATOR : Parcelable.Creator<TournamentDateModel> {
			override fun createFromParcel(parcel: Parcel): TournamentDateModel {
				return TournamentDateModel(parcel)
			}
			override fun newArray(size: Int): Array<TournamentDateModel?> {
				return arrayOfNulls(size)
			}
		}
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(unique_key)
		parcel.writeString(name)
		parcel.writeString(country)
		parcel.writeString(place)
		parcel.writeLong(timestamp)
		parcel.writeTypedList(dates)
		parcel.writeInt(competitors)
		parcel.writeLong(last_fetched)
	}
	override fun describeContents(): Int {
		return 0
	}
	companion object CREATOR : Parcelable.Creator<TournamentModel> {
		override fun createFromParcel(parcel: Parcel): TournamentModel {
			return TournamentModel(parcel)
		}
		override fun newArray(size: Int): Array<TournamentModel?> {
			return arrayOfNulls(size)
		}
	}
}
fun TournamentModel.toTable(): Tournament {
	return Tournament(id, unique_key, name, country, place, timestamp, competitors=competitors, last_fetched = last_fetched)
}