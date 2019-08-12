package talgoe.mobcon.live_tkd_score.details

import android.app.Application
import android.view.Menu
import androidx.lifecycle.*
import kotlinx.coroutines.*
import talgoe.mobcon.live_tkd_score.database.CacheDatabase.Companion.getDatabase
import talgoe.mobcon.live_tkd_score.database.tables.Fight
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.repository.FightRepo
import timber.log.Timber

class LiveTrackerViewModel(application: Application, val tournament: TournamentModel) : ViewModel() {
	private var viewModelJob= SupervisorJob()
	private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)
	private val fightRepo: FightRepo = FightRepo(getDatabase(application.applicationContext, coroutineScope), tournament)
	private val sDate=MutableLiveData<TournamentModel.TournamentDateModel>()
	private val _fights:LiveData<List<Fight>> = Transformations.switchMap(sDate){
		fightRepo.changeDateSelected(sDate.value!!)
	}
	val fights:LiveData<List<Fight>> get()= _fights
	init {
		Timber.i("init-${Thread.currentThread().id}")
		fetchFights(tournament.dates!!.first())
		if(tournament.timestamp>tournament.last_fetched) updateFights()
	}
	private fun fetchFights(tournamentDate: TournamentModel.TournamentDateModel) {
		if(sDate.value!=tournamentDate) {
			sDate.value=tournamentDate
		}else Timber.w("Same date selected: ${sDate.value}")
	}
	fun addDateMenu(menu:Menu, groupId: Int){
		if(!tournament.dates?.isNullOrEmpty()!!) {
			val dateMenu=menu.addSubMenu("Dato")
			tournament.dates?.forEachIndexed { index, date ->
				val newMenuItem=dateMenu.add(groupId, index, index, date.date)
				newMenuItem.isCheckable = true
				if(sDate.value == date){
					newMenuItem.isChecked = true
					dateMenu.setHeaderTitle("Dato (${date.date})")
				}
				Timber.v("addMenu: $date, $index, ${sDate.value}")
			}
		}
	}
	fun dateChanged(index: Int):Boolean{
		Timber.d("User changed date: $index")
		sDate.value=tournament.dates!![index]
		if(tournament.dates?.get(index)!=null) fetchFights(tournament.dates!![index])
		else Timber.w("Illegal date: $index, ${tournament.dates}")
		return true
	}
	override fun onCleared() {
		super.onCleared()
		viewModelJob.cancel()
		Timber.i("onCleared(${fights.value?.count()}")
	}
	private fun updateFights(){
		coroutineScope.launch {
			Timber.i("updateFights: ${sDate.value}, ${tournament.unique_key}")
			async(Dispatchers.IO) {
				fightRepo.fetchFights(sDate.value!!)
			}
			Timber.i("init.fetchFights() updated now it has ${fights.value?.size} rows")
		}
	}
	fun onRefreshFightsSelected(){
		updateFights()
	}
	class Factory(val app: Application, val tournament: TournamentModel) : ViewModelProvider.Factory {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			Timber.d("create of factory called")
			if (modelClass.isAssignableFrom(LiveTrackerViewModel::class.java)) {
				@Suppress("UNCHECKED_CAST")
				return LiveTrackerViewModel(app, tournament) as T
			}
			throw IllegalArgumentException("Unable to construct viewmodel")
		}
	}
}
