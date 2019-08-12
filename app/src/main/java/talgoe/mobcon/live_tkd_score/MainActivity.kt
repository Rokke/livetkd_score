package talgoe.mobcon.live_tkd_score

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import timber.log.Timber
import android.os.Build
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {
	companion object{
		lateinit var versionName:String
		var versionNumber=0L
	}
	private lateinit var drawer: DrawerLayout
	private var toggle=false
	private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
//		startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),1001)

		val gUser=GoogleSignIn.getLastSignedInAccount(applicationContext)
		if(gUser==null) {
			val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//			.requestIdToken(getString(R.string.server_client_id))
				.requestEmail()
				.build()
			val mGoogleSignInClient=GoogleSignIn.getClient(this, gso)
			Timber.w("User !signed in")
			startActivityForResult(mGoogleSignInClient.signInIntent, 1001)
		}else Timber.i("User already signed in: ${gUser.email}")
		val pInfo = packageManager.getPackageInfo(packageName, 0)
		versionNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else pInfo.versionCode.toLong()
		versionName = pInfo.versionName
		Timber.i("onCreate ($versionNumber, $versionName)")
		if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
			Timber.i("onCreate - !INTERNET access granted so trying to allow")
			ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), 1001)
		}else Timber.i("onCreate - INTERNET access granted")
		setContentView(R.layout.activity_main)
		drawer = findViewById(R.id.drawer_layout)
		actionBarDrawerToggle= ActionBarDrawerToggle(this, drawer, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_navigate_up_description)
		drawer.addDrawerListener(actionBarDrawerToggle)
		actionBarDrawerToggle.syncState()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)
//		if (FirebaseAuth.getInstance().currentUser != null) Timber.i("Firebase login OK")
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 1001) {
//			val user = FirebaseAuth.getInstance().currentUser
//			Timber.i("User accepted: $user")
			val task = GoogleSignIn.getSignedInAccountFromIntent(data)
			try {
				val account = task.getResult(ApiException::class.java)
				Timber.i("Signed in: $account")
			} catch (e: ApiException) {
				Timber.w("signInResult:failed code=${e.statusCode}")
			}
			Timber.i("TEST: ${GoogleSignIn.getLastSignedInAccount(applicationContext)}")
		}
	}
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if(item.itemId==android.R.id.home){
			Timber.i("Home")
			if(toggle) drawer.closeDrawer(GravityCompat.START)
			else drawer.openDrawer(GravityCompat.START)
			toggle=!toggle
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}
