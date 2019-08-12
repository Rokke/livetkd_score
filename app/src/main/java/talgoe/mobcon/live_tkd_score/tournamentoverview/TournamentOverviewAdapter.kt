package talgoe.mobcon.live_tkd_score.tournamentoverview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import talgoe.mobcon.live_tkd_score.R
import talgoe.mobcon.live_tkd_score.databinding.TournamentViewItemBinding
import talgoe.mobcon.live_tkd_score.models.TournamentModel
import timber.log.Timber

class TournamentOverviewAdapter(private val callback: TournamentOverviewViewModel.TournamentClick): ListAdapter<TournamentModel,TournamentOverviewAdapter.TournamentViewHolder>(DiffCallback) {
	class TournamentViewHolder(private var binding: TournamentViewItemBinding, var callback: TournamentOverviewViewModel.TournamentClick):RecyclerView.ViewHolder(binding.root){
		companion object {
			@LayoutRes
			val LAYOUT = R.layout.tournament_view_item
		}
		fun bind(tournament: TournamentModel){
			Timber.v("bind: $tournament")
			binding.tournament=tournament
			binding.tournamentCallback=callback
			binding.executePendingBindings()
		}
	}
	companion object DiffCallback:DiffUtil.ItemCallback<TournamentModel>(){
			override fun areItemsTheSame(oldItem: TournamentModel, newItem: TournamentModel): Boolean {
					return oldItem===newItem
			}
			override fun areContentsTheSame(oldItem: TournamentModel, newItem: TournamentModel): Boolean {
					return oldItem.id==newItem.id
			}
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
		val withDataBinding: TournamentViewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), TournamentViewHolder.LAYOUT, parent, false)
		return TournamentViewHolder(withDataBinding, callback)
	}
	override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
		val tournament=getItem(position)
		holder.bind(tournament)
	}
}
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<TournamentModel>?) {
    Timber.v("bindRecycler ${recyclerView.width}")
    val adapter = recyclerView.adapter as TournamentOverviewAdapter
    adapter.submitList(data)
}