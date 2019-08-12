package talgoe.mobcon.live_tkd_score.utils

import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import timber.log.Timber

abstract class MyUtils {
	companion object{
		fun setHomeButton(fragment: Fragment, title: String, enable: Boolean=true){
			fragment.activity?.onBackPressedDispatcher?.addCallback(fragment) {
				Timber.d("onBackPressed")
				fragment.findNavController().navigateUp()
			}
			(fragment.activity as AppCompatActivity).supportActionBar?.let {
				it.setDisplayHomeAsUpEnabled(enable)
				if(title.isNotEmpty()) it.title=title
			}
		}
	}
}