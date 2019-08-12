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
import kotlinx.android.synthetic.main.fight_view_item.view.*
import talgoe.mobcon.live_tkd_score.R
import talgoe.mobcon.live_tkd_score.database.tables.Fight
import talgoe.mobcon.live_tkd_score.databinding.FightViewItemBinding
import timber.log.Timber

class FightListAdapter(private val filter: SearchFilter):ListAdapter<Fight, FightListAdapter.FightViewHolder>(DiffCallback) {
	class FightViewHolder(private var binding: FightViewItemBinding):RecyclerView.ViewHolder(binding.root){
		companion object {
			@LayoutRes
			val LAYOUT = R.layout.fight_view_item
		}
		fun bind(fight: Fight, filter: SearchFilter){
			Timber.v("bind: $fight")
			binding.fight=fight
			if(filter.isFavorite(fight)){
//                binding.root.setPadding(0,80,0,80)
				binding.root.scaleY=1f
				binding.root.label_class.setBackgroundColor(filter.classColor(fight))
				binding.root.scaleX=1f
			}
			else{
//                binding.root.setPadding(0,0,0,0)
				binding.root.scaleX=0.95f
				binding.root.scaleY=0.9f
			}
			binding.executePendingBindings()
		}
	}
	companion object DiffCallback:DiffUtil.ItemCallback<Fight>(){
		override fun areItemsTheSame(oldItem: Fight, newItem: Fight): Boolean {
			return oldItem===newItem
		}
		override fun areContentsTheSame(oldItem: Fight, newItem: Fight): Boolean {
			return oldItem.tournament_date_id==newItem.tournament_date_id && oldItem.round==newItem.round
		}
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FightViewHolder {
		val withDataBinding: FightViewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), FightViewHolder.LAYOUT, parent, false)
		return FightViewHolder(withDataBinding)
//        return FightViewHolder(TestFullWidthBinding.inflate(LayoutInflater.from(parent.context)))
	}
	override fun onBindViewHolder(holder: FightViewHolder, position: Int) {
		val fight=getItem(position)
		holder.bind(fight, filter)
	}
	class SearchFilter(private val names: List<String>, private val countries: List<String>, private val classes: List<String>){
		fun isFavorite(fight: Fight):Boolean{
			return countries.contains(fight.hongcountry) || countries.contains(fight.chongcountry) ||
					classes.contains(fight.classname) ||
					names.any { fight.chong.contentEquals(it) || fight.hong.contentEquals(it) }
		}
		fun classColor(fight: Fight): Int {
			return if(classes.contains(fight.classname)) Color.RED else Color.BLUE
		}
	}
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Fight>?) {
	Timber.v("bindRecycler ${recyclerView.width}")
	val adapter = recyclerView.adapter as FightListAdapter
	adapter.submitList(data)
}
@BindingAdapter("chongColor")
fun bindChongView(textView: TextView, data: Fight?) {
	Timber.v("bindRecycler $data")
	if(data?.winner=="B") {     //Winner so changing format
		textView.setBackgroundColor(textView.context.getColor(R.color.colorChongWinner))
		textView.typeface = Typeface.DEFAULT_BOLD
	}else {
		textView.setBackgroundColor(textView.context.getColor(R.color.colorChong))
		textView.typeface = Typeface.DEFAULT
	}
}
@BindingAdapter("hongColor")
fun bindHongTextView(textView: TextView, data: Fight?) {
	Timber.v("bindRecycler $data")
	if(data?.winner=="R") {     //Winner so changing format
		textView.setBackgroundColor(textView.context.getColor(R.color.colorHongWinner))
		textView.typeface = Typeface.DEFAULT_BOLD
	}else {
		textView.setBackgroundColor(textView.context.getColor(R.color.colorHong))
		textView.typeface = Typeface.DEFAULT
	}
}