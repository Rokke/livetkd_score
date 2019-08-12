package talgoe.mobcon.live_tkd_score.tournamentoverview

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import talgoe.mobcon.live_tkd_score.network.FetchInformation
import talgoe.mobcon.live_tkd_score.network.ScoreApi
import timber.log.Timber
import android.app.PendingIntent
import android.content.Intent
import android.app.Notification
import android.provider.Settings.Secure.ANDROID_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import talgoe.mobcon.live_tkd_score.MainActivity


class TournamentWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
	companion object{
		const val RESULT_NAME="TournamentLastChanged"
		const val CHANNEL_ID="talgoe.mobcon.live_tkd_score.channelId"
	}
	override fun doWork():Result{
		Timber.i("Starting the TournamentWorker-Thread:${Thread.currentThread().id}")
		val oldValue=PreferenceManager.getDefaultSharedPreferences(applicationContext).getFloat(TournamentOverviewViewModel.PREF_TOURNAMENT_UPDATE,0f)
		val stateApi=ScoreApi.retrofitService.getStateAsync("User-Agent:TKDLiveScore/${GoogleSignIn.getLastSignedInAccount(applicationContext)?.email}")
		val resp= stateApi.execute()
		Timber.i("Tournament state: ${resp.body()}!=$oldValue")
		if(oldValue < resp.body() ?: 0f) {
			PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putFloat(TournamentOverviewViewModel.PREF_TOURNAMENT_UPDATE,resp.body()!!).apply()
			val info=FetchInformation.fetchTournaments()
			sendNotification("Det har skjedd turneringsendring(${resp.body()})"+info.filter { it.change==FetchInformation.CHANGE_TYPE.NEW }.joinToString(",", "\nNye: "){it.name} + info.filter { it.change == FetchInformation.CHANGE_TYPE.UPDATED }.joinToString(",","\nEndret: "){ it.name }, "Det har skjedd en endring på minst en av turneringene")
		} else Timber.v("No changes(${resp.body()})?")
		val output = workDataOf(RESULT_NAME to resp.body())
		return Result.success(output)
	}
	private fun sendNotification(message: String, shortMessage: String) {
		val notificationManager: NotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		Timber.i("Sending a notification(${notificationManager.isNotificationPolicyAccessGranted}, ${notificationManager.areNotificationsEnabled()})")
		val notificationIntent = Intent(applicationContext, MainActivity::class.java)
		notificationIntent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

		val intent = PendingIntent.getActivity(applicationContext, 0,notificationIntent, 0)
		val notify = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
			.setSmallIcon(talgoe.mobcon.live_tkd_score.R.drawable.ic_notification)
			.setContentTitle("TKD Live Score")
			.setContentText(shortMessage)
			.setStyle(NotificationCompat.BigTextStyle().bigText(message))
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(intent)
			.build()
		notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
		(applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(1, notify)
		notificationManager.notify(1, notify)
	}
}