package talgoe.mobcon.live_tkd_score.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import talgoe.mobcon.live_tkd_score.database.dao.CompetitorDao
import talgoe.mobcon.live_tkd_score.database.dao.FightDao
import talgoe.mobcon.live_tkd_score.database.dao.TournamentDao
import talgoe.mobcon.live_tkd_score.database.tables.Competitor
import talgoe.mobcon.live_tkd_score.database.tables.Fight
import talgoe.mobcon.live_tkd_score.database.tables.Tournament
import talgoe.mobcon.live_tkd_score.database.tables.TournamentDate
import timber.log.Timber

@Database(entities = [Fight::class, Tournament::class, TournamentDate::class, Competitor::class], exportSchema = false, version = 14)
abstract class CacheDatabase:RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: CacheDatabase?=null
        fun getDatabase(context: Context,scope: CoroutineScope): CacheDatabase {
            Timber.i("getDatabase")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,CacheDatabase::class.java,
                    "cache_database").fallbackToDestructiveMigration().addCallback(CacheDatabaseCallback(scope)).build()
                INSTANCE=instance
                instance
            }
        }
        fun getBackgroundDatabase(): CacheDatabase {
            Timber.i("getBackgroundDatabase")
            return INSTANCE!!
        }
        private class CacheDatabaseCallback(private val scope: CoroutineScope): RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                Timber.w(javaClass.name,"database-onCreate()")
                super.onCreate(db)
                INSTANCE?.let {
                    scope.launch{
                        Timber.w("onCreate")
                    }
                }
            }
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Timber.d("onOpen")
    /*                INSTANCE?.let { database ->
                        scope.launch{
                            populateDatabase(database.tournamentDao())
                        }
                    }*/
            }
        }
    }
    abstract val tournamentDao: TournamentDao
    abstract val fightDao: FightDao
    abstract val competitorDao: CompetitorDao
}



