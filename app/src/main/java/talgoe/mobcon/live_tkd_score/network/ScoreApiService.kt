package talgoe.mobcon.live_tkd_score.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL="http://mobcon.hopto.org:3044/"

private val moshi = Moshi.Builder()
	.add(KotlinJsonAdapterFactory())
	.build()

private val retrofit = Retrofit.Builder()
	.addConverterFactory(MoshiConverterFactory.create(moshi))
	.baseUrl(BASE_URL)
	.build()

object ScoreApi {
	val retrofitService : ScoreApiService by lazy {
		retrofit.create(ScoreApiService::class.java)
	}
}

interface ScoreApiService {
	@GET("fights/{key}/{date}")
	suspend fun getFightsAsync(@Path("key") key: String, @Path("date") date: String): Response<FightApi>
	@GET("competitors/{key}")
	suspend fun getCompetitorsAsync(@Path("key") key: String): Response<CompetitorApi>
	@GET("tournaments")
	fun getTournamentsAsync(): Call<TournamentApi>
//	@Headers("User-Agent:TKDLiveScore")
	@GET("state")
	fun getStateAsync(@Header("User-Agent") userAgent:String="TKDLiveScore"): Call<Float>
}

data class StateApi(
	val modified: Float
)