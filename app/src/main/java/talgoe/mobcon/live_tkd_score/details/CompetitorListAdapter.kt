package talgoe.mobcon.live_tkd_score.details

import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.competitor_view_item.view.*
import talgoe.mobcon.live_tkd_score.R
import talgoe.mobcon.live_tkd_score.database.tables.Competitor
import talgoe.mobcon.live_tkd_score.databinding.CompetitorViewItemBinding
import timber.log.Timber

class CompetitorListAdapter:ListAdapter<Competitor, CompetitorListAdapter.CompetitorViewHolder>(DiffCallback) {
	class CompetitorViewHolder(private var binding: CompetitorViewItemBinding):RecyclerView.ViewHolder(binding.root){
		companion object {
			@LayoutRes
			val LAYOUT = R.layout.competitor_view_item
		}
		fun bind(competitor: Competitor){
			Timber.v("bind: $competitor")
			binding.competitor=competitor
			binding.executePendingBindings()
		}
	}
	companion object DiffCallback:DiffUtil.ItemCallback<Competitor>(){
		override fun areItemsTheSame(oldItem: Competitor, newItem: Competitor): Boolean {
			return oldItem===newItem
		}
		override fun areContentsTheSame(oldItem: Competitor, newItem: Competitor): Boolean {
			return oldItem.name==newItem.name
		}
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitorViewHolder {
		val withDataBinding: CompetitorViewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), CompetitorViewHolder.LAYOUT, parent, false)
		return CompetitorViewHolder(withDataBinding)
//        return CompetitorViewHolder(TestFullWidthBinding.inflate(LayoutInflater.from(parent.context)))
	}
	override fun onBindViewHolder(holder: CompetitorViewHolder, position: Int) {
		val competitor=getItem(position)
		holder.bind(competitor)
	}
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Competitor>?) {
	Timber.v("bindRecycler ${recyclerView.width}")
	val adapter = recyclerView.adapter as CompetitorListAdapter
	adapter.submitList(data)
}