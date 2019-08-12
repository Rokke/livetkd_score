package talgoe.mobcon.live_tkd_score.network

import talgoe.mobcon.live_tkd_score.database.CacheDatabase
import talgoe.mobcon.live_tkd_score.database.tables.TournamentDate
import timber.log.Timber
import java.lang.StringBuilder

class FetchInformation {
	data class TournamentChanges(
		val name: String,
		val change: CHANGE_TYPE
	)
	enum class CHANGE_TYPE{
		NEW,
		UPDATED
	}
	companion object{
/*
		private fun insertNewTournament(database: CacheDatabase, tournament: TournamentApi.TournamentList){
			Timber.d("insertingNewTournament($tournament)")
			val newRowId=database.tournamentDao.insert(tournament.asTournamentTable())
			if(tournament.fight_dates!=null) {
				val dates=tournament.fight_dates.mapNotNull { td ->
					Timber.d("Adding date: $td")
					if(td.date!=null) TournamentDate(0,newRowId,td.date,td.timestamp!!,td.fights)
					else null
				}
				Timber.d("Adding dates to tournament: $dates")
				database.tournamentDao.insertDates(dates)
			}
		}
*/
		fun fetchTournaments():List<TournamentChanges>{
			Timber.i("fetchTournaments, fresh information-${Thread.currentThread().id}")
			val updates= mutableListOf<TournamentChanges>()
			try {
					val database= CacheDatabase.getBackgroundDatabase()
					val getTournamentsAsync = ScoreApi.retrofitService.getTournamentsAsync().execute()
					Timber.i("RESP: $getTournamentsAsync")
					if (getTournamentsAsync.body()?.tournaments != null) {
						val fetchedTournaments=getTournamentsAsync.body()!!.tournaments
						fetchedTournaments.forEach{ tournament ->
							val found=if(tournament.name!=null) database.tournamentDao.fetchTournamentByKey(tournament.key) else null
							when {
								found==null ->{
									val newRowId=database.tournamentDao.insert(tournament.asTournamentTable())
									if(tournament.fight_dates!=null) {
										val dates=tournament.fight_dates.mapNotNull { td ->
											Timber.d("Adding date: $td")
											if(td.date!=null) TournamentDate(0,newRowId,td.date,td.timestamp!!,td.fights)
											else null
										}
										Timber.d("Adding dates to tournament: $dates")
										database.tournamentDao.insertDates(dates)
									}
									Timber.d("Tournament !exist insert: $tournament")
									updates.add(TournamentChanges(tournament.name ?: tournament.key, CHANGE_TYPE.NEW))
								}
								found.timestamp!=tournament.timestamp -> {
									database.tournamentDao.updateTournament(tournament.asTournamentTable(found.id))
									Timber.d("Tournament exist but changed: $found -> $tournament")
									tournament.fight_dates?.forEach { fight_date ->
										val foundDate=found.dates?.firstOrNull{ date ->
											date.date==fight_date.date
										}
										when {
											foundDate==null -> {
												val insertId=database.tournamentDao.insert(fight_date.asTournamentDateTable(found.id))
												Timber.d("Tournament date !exist insert: $fight_date, id: ${found.id} = $insertId")
											}
											foundDate.timestamp!=fight_date.timestamp -> {
												database.tournamentDao.updateTournament(fight_date.asTournamentDateTable(found.id, foundDate.id))
												Timber.d("Tournament date exist but changed: $foundDate -> $fight_date (${foundDate.id})")
											}
											else -> Timber.v("Tournament date already exist so no update: $foundDate($fight_date)")
										}
									}
									updates.add(TournamentChanges(tournament.name ?: tournament.key, CHANGE_TYPE.UPDATED))
								}
								else -> Timber.v("The tournament already exist and there are no changes: $found($tournament)")
							}
						}
						Timber.i("New tournaments fetched: ${getTournamentsAsync.body()?.tournaments?.size}-${Thread.currentThread().id}")
					}
			} catch (t: Throwable) {
				Timber.e(t, "Error: $t")
			}
			return updates
		}
	}
}