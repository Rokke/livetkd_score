package talgoe.mobcon.live_tkd_score.tournamentoverview

import android.app.Application
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.lifecycle.*
import androidx.work.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.*
import talgoe.mobcon.live_tkd_score.database.CacheDatabase.Companion.getDatabase
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.network.ScoreApi
import talgoe.mobcon.live_tkd_score.repository.TournamentRepo
import timber.log.Timber
import java.time.Duration

class TournamentOverviewViewModel(application: Application) : AndroidViewModel(application) {
    companion object{
        const val PREF_TOURNAMENT_UPDATE="LastTournamentUpdate"
        const val UNIQUE_WORK_TOURNAMENT="Talgoe_Unique_Work_Tournament_ID"
    }
    private var viewModelJob= SupervisorJob()
    private val coroutineScope= CoroutineScope(viewModelJob + Dispatchers.Main)
    private val tournamentRepo: TournamentRepo = TournamentRepo(getDatabase(application.applicationContext, coroutineScope))
    init {
        val workRequest= PeriodicWorkRequestBuilder<TournamentWorker>(Duration.ofMinutes(15))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(application).enqueueUniquePeriodicWork(UNIQUE_WORK_TOURNAMENT, ExistingPeriodicWorkPolicy.KEEP, workRequest)
        Timber.i("init")
        coroutineScope.launch {
            async(Dispatchers.IO) {
                ScoreApi.retrofitService.getStateAsync("TKDLiveScore/${GoogleSignIn.getLastSignedInAccount(application)}").execute()
            }
        }
    }
    val tournaments: LiveData<List<TournamentModel>> = tournamentRepo.tournaments
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("onCleared(${tournaments.value?.count()}")
    }
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TournamentOverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TournamentOverviewViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
    class TournamentClick(val block: (TournamentModel) -> Unit) {
        fun onClick(tournament: TournamentModel) = block(tournament)
    }
}
