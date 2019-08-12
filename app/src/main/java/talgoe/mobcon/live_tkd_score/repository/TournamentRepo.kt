package talgoe.mobcon.live_tkd_score.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import talgoe.mobcon.live_tkd_score.database.*
import talgoe.mobcon.live_tkd_score.database.tables.TournamentDate
import talgoe.mobcon.live_tkd_score.database.tables.toModel
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import talgoe.mobcon.live_tkd_score.models.toTable
import talgoe.mobcon.live_tkd_score.network.*
import timber.log.Timber

class TournamentRepo(database: CacheDatabase) {
	private val _tournaments:LiveData<List<TournamentModel>> = Transformations.map(database.tournamentDao.getTournamentWithDates()){ list -> list.map { it.toModel() } }
	val tournaments get() = _tournaments
}