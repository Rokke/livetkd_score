package talgoe.mobcon.live_tkd_score

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import timber.log.Timber

class SettingsViewModel(application: Application) : ViewModel() {
	val sharedPref=application.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE)
	companion object{
		const val MY_SHARED_PREF="LiveTrackerPref"
		const val PREF_NAMES="names"
		const val PREF_COUNTRIES="countries"
		const val PREF_CLASSES="classes"
		val Cadet_Female=arrayOf("-29","-33","-37","-41","-44","-47","-51","-55","-59","+59")
		val Cadet_Male=arrayOf("-33","-37","-41","-45","-49","-53","-57","-61","-65","+65")
		val Junior_Female=arrayOf("-42","-44","-46","-49","-52","-55","-59","-63","-68","+68")
		val Junior_Male=arrayOf("-45","-48","-51","-55","-59","-63","-68","-73","-78","+78")
		val Senior_Female=arrayOf("-46","-49","-53","-57","-62","-67","-73","+73")
		val Senior_Male=arrayOf("-54","-58","-63","-68","-74","-80","-87","+87")
		class WeightClass(val text:String, var selected:Boolean=false)
	}
	val weightClasses= linkedMapOf(
		"CF" to Array(Cadet_Female.size){index-> WeightClass(Cadet_Female[index],false) },
		"CM" to Array(Cadet_Male.size){index-> WeightClass(Cadet_Male[index],false) },
		"JF" to Array(Junior_Female.size){index-> WeightClass(Junior_Female[index],false) },
		"JM" to Array(Junior_Male.size){index-> WeightClass(Junior_Male[index],false) },
		"SF" to Array(Senior_Female.size){index-> WeightClass(Senior_Female[index],false) },
		"SM" to Array(Senior_Male.size){index-> WeightClass(Senior_Male[index],false) })
	fun fetchWeightClassName(weightClass: String):String{
		Timber.v("fetchWeightClassName($weightClass)")
		val count=weightClasses[weightClass]?.count{ Timber.v("X: ${it.selected}")
			it.selected } ?: 0
		return if(count>0) "$weightClass ($count)" else weightClass
	}
	private val _names = sharedPref.getStringSet(PREF_NAMES, mutableSetOf())!!
//	private val _countries = sharedPref.getStringSet(PREF_COUNTRIES, mutableSetOf())!!
	val names:ArrayList<String> = ArrayList(_names)
	val countries:ArrayList<String> = ArrayList(sharedPref.getStringSet(PREF_COUNTRIES, mutableSetOf())!!)
	init{
		Timber.d("init()=${sharedPref.getString(PREF_CLASSES,null)}")
		sharedPref.getString(PREF_CLASSES,null)?.split(",")?.forEach {category ->
			weightClasses[category.trimStart().substring(0,2)]?.firstOrNull { it.text == category.substring(2) }?.selected=true
		}
		Timber.d("init()-after(${weightClasses.map { cls->
			"${cls.key}: ${cls.value.joinToString(",") { it.selected.toString()
			}}"
		}}")
	}
	fun savePrefs(){
		val saveClassInfo=weightClasses.mapNotNull { category ->
			val selectedValues=category.value.mapNotNull { selectedValue ->
				if(selectedValue.selected) "${category.key}${selectedValue.text}" else null
			}.joinToString()
			if(selectedValues.isNotEmpty()) selectedValues else null
		}.joinToString()
		Timber.i("Saving new Prefs: $_names, $countries: $saveClassInfo")
		sharedPref.edit().putStringSet(PREF_NAMES, _names).putStringSet(PREF_COUNTRIES, countries.toMutableSet()).putString(PREF_CLASSES, saveClassInfo).apply()
	}
	fun addNewName(name: String):Boolean{
		Timber.d("Adding new filter name: $name")
		names.add(name)
		_names.add(name)
		return true
	}
	fun addNewCountry(country: String, added: Boolean):Boolean{
		Timber.d("Adding new filter country: $country=$added")
		return if(added) !countries.contains(country) && countries.add(country)
		else countries.remove(country)
	}
	fun removeName(position: Int):String{
		Timber.d("Removed filter name: ${names[position]}-$position")
		return names.removeAt(position)
	}
	class Factory(val app: Application) : ViewModelProvider.Factory {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			Timber.d("create of factory called")
			if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
				@Suppress("UNCHECKED_CAST")
				return SettingsViewModel(app) as T
			}
			throw IllegalArgumentException("Unable to construct viewmodel")
		}
	}
}
