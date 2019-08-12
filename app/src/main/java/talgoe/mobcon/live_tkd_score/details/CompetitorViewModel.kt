package talgoe.mobcon.live_tkd_score.details

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import talgoe.mobcon.live_tkd_score.database.CacheDatabase
import talgoe.mobcon.live_tkd_score.database.CacheDatabase.Companion.getDatabase
import talgoe.mobcon.live_tkd_score.database.tables.Competitor
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.repository.CompetitorRepo
import timber.log.Timber

class CompetitorViewModel(application: Application, val tournament: TournamentModel) : ViewModel() {
	private var viewModelJob= SupervisorJob()
	private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)
	private val competitorRepo: CompetitorRepo = CompetitorRepo(getDatabase(application.applicationContext, coroutineScope), tournament)
	private val _competitors:LiveData<List<Competitor>> = competitorRepo.allCompetitors()
	val waitForInformation=MutableLiveData<String>()
	val competitors: LiveData<List<Competitor>> get()= _competitors
	init {
		Timber.i("init-$tournament")
		if(tournament.last_fetched==0L || tournament.last_fetched<tournament.timestamp){
			waitForInformation.value="Fetching new competitors"
			updateCompetitors()
		}
	}
	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
		Timber.i("onCleared(${competitors.value?.count()}")
	}
	private fun updateCompetitors(){
		coroutineScope.launch {
			Timber.i("updateCompetitors: ${tournament.unique_key}")
			async(Dispatchers.IO) {
				competitorRepo.fetchCompetitors(tournament)
				waitForInformation.postValue("")
			}
			Timber.i("init.fetchFights() updated now it has ${competitors.value?.size} rows")
		}
	}
	fun onRefreshCompetitorsSelected(){
		updateCompetitors()
	}
	class Factory(val app: Application, val tournament: TournamentModel) : ViewModelProvider.Factory {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			Timber.d("create of factory called")
			if (modelClass.isAssignableFrom(CompetitorViewModel::class.java)) {
				@Suppress("UNCHECKED_CAST")
				return CompetitorViewModel(app, tournament) as T
			}
			throw IllegalArgumentException("Unable to construct viewmodel")
		}
	}
}
