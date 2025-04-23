package ayds.songinfo.home.model

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ayds.songinfo.home.model.repository.SongRepository
import ayds.songinfo.home.model.repository.SongRepositoryImpl
import ayds.songinfo.home.model.repository.external.spotify.SpotifyInjector
import ayds.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import ayds.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import ayds.songinfo.home.model.repository.local.spotify.room.SongDatabase
import ayds.songinfo.home.model.repository.local.spotify.room.SpotifyLocalStorageRoomImpl
import ayds.songinfo.home.view.HomeView

object HomeModelInjector {

    private lateinit var homeModel: HomeModel

    fun getHomeModel(): HomeModel = homeModel

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE SongEntity ADD COLUMN release_date_precision TEXT NOT NULL DEFAULT ''"
            )
        }
    }


    fun initHomeModel(homeView: HomeView) {
        val dataBase = Room.databaseBuilder(
            homeView as Context,
            SongDatabase::class.java, "song-database"
        ).addMigrations(MIGRATION_1_2)
            .build()


        val spotifyLocalRoomStorage: SpotifyLocalStorage = SpotifyLocalStorageRoomImpl(dataBase)

        val spotifyTrackService: SpotifyTrackService = SpotifyInjector.spotifyTrackService

        val repository: SongRepository =
            SongRepositoryImpl(spotifyLocalRoomStorage, spotifyTrackService)

        homeModel = HomeModelImpl(repository)
    }
}