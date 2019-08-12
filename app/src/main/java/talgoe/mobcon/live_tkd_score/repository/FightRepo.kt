package talgoe.mobcon.live_tkd_score.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import talgoe.mobcon.live_tkd_score.database.CacheDatabase
import talgoe.mobcon.live_tkd_score.database.tables.Fight
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.network.ScoreApi
import talgoe.mobcon.live_tkd_score.network.asDomainModel
import timber.log.Timber

class FightRepo(private val database: CacheDatabase, private val selectedTournament: TournamentModel) {
	fun changeDateSelected(tournamentDate: TournamentModel.TournamentDateModel):LiveData<List<Fight>>{
		Timber.d("changeDate to $tournamentDate")
		return database.fightDao.fetchLiveFights(tournamentDate.id)
	}
	suspend fun fetchFights(tournamentDate: TournamentModel.TournamentDateModel) {
		Timber.i("fetchFights, fresh information-${tournamentDate.date}")
		val getFights = ScoreApi.retrofitService.getFightsAsync(selectedTournament.unique_key, tournamentDate.date)
		if(getFights.isSuccessful) {
			Timber.d("fetchFights returned ${getFights.body()?.fights?.size}")
			if (getFights.body()?.fights != null) database.fightDao.insert(
				getFights.body()!!.fights.asDomainModel(
					tournamentDate.id
				)
			)
			else Timber.w("No fights received")
			selectedTournament.last_fetched = selectedTournament.timestamp
			database.tournamentDao.updateFetchDate(selectedTournament.id, selectedTournament.last_fetched)
		}else Timber.w("Error receiving fight changes: ${getFights.errorBody()}")
	}
}