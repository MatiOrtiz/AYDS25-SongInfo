package ayds.songinfo.moredetails.fulllogic.injector

import android.content.Context
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import ayds.songinfo.moredetails.fulllogic.presentation.OtherInfoPresenter
import ayds.songinfo.moredetails.fulllogic.data.OtherInfoRepositoryImpl
import ayds.songinfo.moredetails.fulllogic.data.external.LastFMAPI
import ayds.songinfo.moredetails.fulllogic.data.external.LastFMtoArtistBiographyResolverImpl
import ayds.songinfo.moredetails.fulllogic.data.external.OtherInfoServiceImpl
import ayds.songinfo.moredetails.fulllogic.data.local.ArticleDatabase
import ayds.songinfo.moredetails.fulllogic.data.local.OtherInfoLocalStorage
import ayds.songinfo.moredetails.fulllogic.data.local.OtherInfoLocalStorageImpl
import ayds.songinfo.moredetails.fulllogic.presentation.ArtistBiographyDescriptionHelperImpl
import ayds.songinfo.moredetails.fulllogic.presentation.OtherInfoActivity
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const  val ARTICLE_BD_NAME = "database-article"
private const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"


object OtherInfoInjector {
    lateinit var presenter: OtherInfoPresenter

    fun initGraph(context: Context) {
        val articleDatabase =
            databaseBuilder(context, ArticleDatabase::class.java, ARTICLE_BD_NAME).build()

        val retrofit = Retrofit.Builder().baseUrl(LASTFM_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()).build()
        val lastFMAPI = retrofit.create(LastFMAPI::class.java)

        val lastFMtoArtistBiographyResolver = LastFMtoArtistBiographyResolverImpl()
        val otherInfoService = OtherInfoServiceImpl(lastFMAPI, lastFMtoArtistBiographyResolver)
        val articleLocalStorage = OtherInfoLocalStorageImpl(articleDatabase)

        val repository = OtherInfoRepositoryImpl(articleLocalStorage, otherInfoService)

        val artistBiographyDescriptionHelper = ArtistBiographyDescriptionHelperImpl()

    }

}