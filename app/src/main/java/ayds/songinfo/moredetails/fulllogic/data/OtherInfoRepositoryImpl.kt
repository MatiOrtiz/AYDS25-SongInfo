package ayds.songinfo.moredetails.fulllogic.data

import android.content.Context
import androidx.room.Room.databaseBuilder
import ayds.songinfo.moredetails.fulllogic.data.local.ArticleDatabase
import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.data.external.LastFMAPI
import ayds.songinfo.moredetails.fulllogic.data.external.OtherInfoService
import ayds.songinfo.moredetails.fulllogic.data.local.OtherInfoLocalStorage
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class OtherInfoRepositoryImpl(private val otherInfoLocalStorage: OtherInfoLocalStorage,
    private val otherInfoService: OtherInfoService): OtherInfoRepository {

    override fun getArtistInfo(artistName: String):ArtistBiography {
        val dbArticle = otherInfoLocalStorage.getArticle(artistName)
        val artistBiography : ArtistBiography

        if(dbArticle!=null)
            artistBiography = dbArticle.markItAsLocal()
        else {
            artistBiography = otherInfoService.getArticle(artistName)
            if(artistBiography!=null)
                otherInfoLocalStorage.insertArtist(artistBiography)
        }

        return artistBiography
    }

    private fun ArtistBiography.markItAsLocal() = copy(biography = "[*]$biography")

}