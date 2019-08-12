package talgoe.mobcon.live_tkd_score

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import talgoe.mobcon.live_tkd_score.databinding.SettingsFragmentBinding
import talgoe.mobcon.live_tkd_score.utils.MyUtils
import timber.log.Timber


class SettingsFragment : Fragment() {
	companion object {
		const val ID_CHIP_START = 1000
	}

	private val viewModel: SettingsViewModel by lazy {
		ViewModelProvider(this, SettingsViewModel.Factory(activity!!.application)).get(SettingsViewModel::class.java)
	}
	private var arrayAdapter: ArrayAdapter<String>? = null
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val binding = SettingsFragmentBinding.inflate(inflater)
		binding.lifecycleOwner = this
		binding.viewModel = viewModel
		MyUtils.setHomeButton(this, getString(R.string.title_settings))
		arrayAdapter = ArrayAdapter(this.context!!, android.R.layout.simple_list_item_1, viewModel.names)
		binding.buttonAddName.setOnClickListener {
			if (viewModel.addNewName(binding.editTextName.text.toString())) {
				binding.editTextName.text?.clear()
			}
		}
		viewModel.countries.forEach {
			addNewCountryCheckBox(binding, it)
		}
		binding.buttonAddCountry.setOnClickListener {
			if (binding.editTextCountry.length()==3 && viewModel.addNewCountry(binding.editTextCountry.text.toString(), true)) {
				addNewCountryCheckBox(binding, binding.editTextCountry.text.toString())
				binding.editTextCountry.text.clear()
			}else if (binding.editTextCountry.length()==3) binding.checkGroupSelectCountries.findViewWithTag<CheckBox>(binding.editTextCountry.text).isChecked=true
		}
		viewModel.weightClasses.forEach{ weightClass ->
			RadioButton(this.context).let{ newMainRadio ->
				newMainRadio.text=viewModel.fetchWeightClassName(weightClass.key)
				newMainRadio.tag=weightClass.key
				binding.radioGroupClasses.addView(newMainRadio)
			}
		}
		binding.radioGroupClasses.setOnCheckedChangeListener { group, checkId ->
			Timber.d("radioGroupClasses selected $group, $checkId")
			val selectedClass = group.findViewById<RadioButton>(checkId)
			binding.radioGroupSelectClasses.removeAllViews()
			if(checkId>-1){
				viewModel.weightClasses[selectedClass.tag]?.forEach { it ->
					val newCheckBox = CheckBox(this.context)
					newCheckBox.text = it.text
					newCheckBox.isChecked = it.selected
					newCheckBox.setOnCheckedChangeListener { _, isChecked ->
						Timber.d("newRadio.setOnCheckedChangeListener checked $isChecked ($selectedClass)")
						it.selected=isChecked
						selectedClass.text = viewModel.fetchWeightClassName(selectedClass.tag.toString())
						viewModel.weightClasses.forEach { key, value ->
							Timber.v("$key: ${value.joinToString("") { if (it.selected) "1" else "0" }} => ${selectedClass.text}")
						}
					}
					binding.radioGroupSelectClasses.addView(newCheckBox)
				}
			}
		}
		binding.listViewNames.setOnItemClickListener { _, _, position, _ ->
			binding.editTextName.setText(viewModel.removeName(position))
			arrayAdapter!!.notifyDataSetChanged()
		}
		binding.editTextName.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				if (viewModel.addNewName(binding.editTextName.text.toString())) binding.editTextName.text?.clear()
				true
			} else false
		}
		binding.listViewNames.adapter = arrayAdapter
		setHasOptionsMenu(true)
		return binding.root
	}

	private fun addNewCountryCheckBox(binding: SettingsFragmentBinding, text: String) {
		val newCheckBox = CheckBox(this.context)
		newCheckBox.text = text
		newCheckBox.isChecked = true
		newCheckBox.tag = text
		newCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
			Timber.d("chipGroupSelectCountry removed $text")
			viewModel.addNewCountry(buttonView.text.toString(), isChecked)
		}
		binding.checkGroupSelectCountries.addView(newCheckBox)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.settings_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId){
			R.id.menu_settings_save->{
				Timber.i("Save filters")
				viewModel.savePrefs()
			}
			android.R.id.home->{
				findNavController().navigateUp()
			}
			else->{
				Timber.w("Unknown item pressed: ${item.itemId}")
			}
		}
		return super.onOptionsItemSelected(item)
	}
}