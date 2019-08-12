package talgoe.mobcon.live_tkd_score.details

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import talgoe.mobcon.live_tkd_score.databinding.CompetitorFragmentBinding
import talgoe.mobcon.live_tkd_score.utils.MyUtils
import timber.log.Timber
import android.widget.RelativeLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import talgoe.mobcon.live_tkd_score.R

class CompetitorFragment : Fragment() {
	var alertDialog:AlertDialog?=null
	private val viewModel: CompetitorViewModel by lazy{
		ViewModelProvider(this,CompetitorViewModel.Factory(activity!!.application,CompetitorFragmentArgs.fromBundle(arguments!!).selectedTournament)).get(CompetitorViewModel::class.java)
	}
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		Timber.d("onCreateView")
		val binding = CompetitorFragmentBinding.inflate(inflater)
		binding.lifecycleOwner = this
		binding.viewModel = viewModel
		binding.recyclerCompetitorsList.adapter = CompetitorListAdapter()
		MyUtils.setHomeButton(this, String.format("Competitors - %s", viewModel.tournament.name))
		Timber.i("onCreateView-${viewModel.tournament}")
		viewModel.waitForInformation.observe(this, Observer {
			if(it.isNotEmpty()){
				val progressBar = ProgressBar(this.context, null, android.R.attr.progressBarStyle)
				val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
				params.addRule(RelativeLayout.ALIGN_END)
				alertDialog=AlertDialog.Builder(this.context).setMessage(it).setCancelable(false).setView(progressBar).show()
			}
			else alertDialog?.dismiss()
		})
		return binding.root
	}
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.competitor_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.menu_competitor_refresh -> {
				Timber.i("Update competitors")
				viewModel.onRefreshCompetitorsSelected()
				return true
			}
			android.R.id.home -> {
				findNavController().navigateUp()
			}
			else -> {
				Timber.w("Unknown item pressed: ${item.itemId}")
			}
		}
		return super.onOptionsItemSelected(item)
	}
}