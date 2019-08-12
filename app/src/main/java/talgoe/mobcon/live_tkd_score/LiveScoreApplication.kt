package talgoe.mobcon.live_tkd_score

import android.app.Application
import timber.log.Timber

class LiveScoreApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.d("Timber logging created ${System.currentTimeMillis()}")
    }
}