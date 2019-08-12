package talgoe.mobcon.live_tkd_score.details

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import talgoe.mobcon.live_tkd_score.R
import talgoe.mobcon.live_tkd_score.SettingsViewModel
import talgoe.mobcon.live_tkd_score.databinding.LiveTrackerFragmentBinding
import talgoe.mobcon.live_tkd_score.utils.MyUtils
import timber.log.Timber

class LiveTrackerFragment : Fragment() {
    private val viewModel: LiveTrackerViewModel by lazy{
        ViewModelProvider(this,LiveTrackerViewModel.Factory(activity!!.application,LiveTrackerFragmentArgs.fromBundle(arguments!!).selectedTournament)).get(LiveTrackerViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("onCreateView")
        val binding = LiveTrackerFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val sharedPref=context?.getSharedPreferences(SettingsViewModel.MY_SHARED_PREF, Context.MODE_PRIVATE)
        binding.recyclerFightsList.adapter = FightListAdapter(FightListAdapter.SearchFilter(
            sharedPref?.getStringSet(SettingsViewModel.PREF_NAMES, mutableSetOf())?.toList() ?: emptyList(),
            sharedPref?.getStringSet(SettingsViewModel.PREF_COUNTRIES, mutableSetOf())?.toList() ?: emptyList(),
                sharedPref?.getString(SettingsViewModel.PREF_CLASSES, "")?.split(",")?.map{ it.trim() } ?: emptyList()
            ))
        setHasOptionsMenu(true)
        MyUtils.setHomeButton(this, String.format(getString(R.string.title_live_score), viewModel.tournament.name))
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.addDateMenu(menu, R.id.group_dates)
        inflater.inflate(R.menu.live_score_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.group_dates){
            viewModel.dateChanged(item.itemId)
            activity!!.invalidateOptionsMenu()
            return true
        }
        else {
            when (item.itemId) {
                R.id.menu_live_score_refresh -> {
                    Timber.i("Update fights")
                    viewModel.onRefreshFightsSelected()
                    return true
                }
                android.R.id.home->{
                    findNavController().navigateUp()
                }
                else -> {
                    Timber.w("Unknown item pressed: ${item.itemId}")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}