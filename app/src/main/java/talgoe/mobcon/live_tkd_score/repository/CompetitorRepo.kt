package talgoe.mobcon.live_tkd_score.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import talgoe.mobcon.live_tkd_score.database.CacheDatabase
import talgoe.mobcon.live_tkd_score.database.tables.Competitor
import talgoe.mobcon.live_tkd_score.database.tables.Fight
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.models.toTable
import talgoe.mobcon.live_tkd_score.network.ScoreApi
import talgoe.mobcon.live_tkd_score.network.asDomainModel
import timber.log.Timber

class CompetitorRepo(private val database: CacheDatabase, private val selectedTournament: TournamentModel) {
	fun allCompetitors():LiveData<List<Competitor>>{
		Timber.d("allCompetitors")
		return database.competitorDao.fetchLiveCompetitors(selectedTournament.id)
	}
	suspend fun fetchCompetitors(tournament: TournamentModel) {
		Timber.i("fetchCompetitors, fresh information-${tournament.unique_key}")
		val getCompetitors = ScoreApi.retrofitService.getCompetitorsAsync(selectedTournament.tournamentId)
		Timber.d("fetchCompetitors returned ${getCompetitors.body()?.divisions?.size} divisions")
		if (getCompetitors.body()?.divisions != null){
			getCompetitors.body()!!.divisions.forEach { division ->
				database.competitorDao.insert(division.athletes.map { athlete ->
					Competitor(tournament.id, athlete.name, athlete.club, athlete.country, division.division_name, athlete.updated)
				})
			}
			tournament.last_fetched=tournament.timestamp
			database.tournamentDao.updateFetchDate(tournament.id, tournament.last_fetched)
		}
		else Timber.w("No fights received")
	}
/*
	suspend fun updateTournamentFetched(tournamentId: Long, lastFetched: Long){
		Timber.i("Writing update fetch info $tournamentId, $lastFetched")
	}
*/
}