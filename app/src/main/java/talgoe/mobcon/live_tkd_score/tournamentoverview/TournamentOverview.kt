package talgoe.mobcon.live_tkd_score.tournamentoverview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import talgoe.mobcon.live_tkd_score.R
import talgoe.mobcon.live_tkd_score.databinding.TournamentOverviewFragmentBinding
import timber.log.Timber

class TournamentOverview : Fragment() {
	init{
		Timber.d("init")
	}
	private val viewModel: TournamentOverviewViewModel by lazy{
		ViewModelProvider(this,TournamentOverviewViewModel.Factory(activity!!.application)).get(TournamentOverviewViewModel::class.java)
	}
	private val viewModelAdapter:TournamentOverviewAdapter by lazy{
		TournamentOverviewAdapter(TournamentOverviewViewModel.TournamentClick {
			Timber.i("Tournament $it clicked")
			if(it.totalFights>0) findNavController().navigate(TournamentOverviewDirections.actionTournamentOverviewToLiveTrackerFragment(it))
			else findNavController().navigate(TournamentOverviewDirections.actionTournamentOverviewToCompetitorFragment(it))
		})
	}
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		Timber.i("onCreateView()-${Thread.currentThread().id}")
		val binding: TournamentOverviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.tournament_overview_fragment, container, false)
		binding.lifecycleOwner = viewLifecycleOwner
		binding.viewModel = viewModel
		binding.recyclerTournamentList.adapter = viewModelAdapter
		setHasOptionsMenu(true)
		activity?.title=getString(R.string.title_tournament)
		Timber.i("onCreate-Tournaments: ${viewModel.tournaments.value?.count()}")
		return binding.root
	}
	override fun onStop() {
		Timber.i("onStop()")
		super.onStop()
	}
	override fun onStart() {
		Timber.i("onStart-Tournaments: ${viewModel.tournaments.value?.count()}")
		super.onStart()
	}
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.tournament_overview_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId){
			R.id.menu_settings->{
				Timber.i("Settings")
				findNavController().navigate(TournamentOverviewDirections.actionTournamentOverviewToSettingsFragment())
			}
			android.R.id.home->{
				Timber.i("Home")
			}
			else->{
				Timber.w("Unknown item pressed: ${item.itemId}")
			}
		}
		return super.onOptionsItemSelected(item)
	}
}
