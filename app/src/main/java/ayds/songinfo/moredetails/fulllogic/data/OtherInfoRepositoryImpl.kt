package ayds.songinfo.moredetails.fulllogic.data

import android.content.Context
import androidx.room.Room.databaseBuilder
import ayds.songinfo.moredetails.fulllogic.ArticleDatabase
import ayds.songinfo.moredetails.fulllogic.ArticleEntity
import ayds.songinfo.moredetails.fulllogic.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.LastFMAPI
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class OtherInfoRepositoryImpl : OtherInfoRepository {
    private lateinit var dataBase: ArticleDatabase
    private lateinit var lastFMAPI: LastFMAPI
    private lateinit var articleDatabase: ArticleDatabase

    override fun initDB(context: Context) {
        dataBase = databaseBuilder(
            context,
            ArticleDatabase::class.java, "database-name-thename"
        ).build()
    }

    override fun initLastFMApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        lastFMAPI = retrofit.create(LastFMAPI::class.java)
    }

    override fun initArticleDatabase(context: Context) {
        articleDatabase = databaseBuilder(
            context,
            ArticleDatabase::class.java, "article-database"
        ).build()
    }

    override fun getArtistInfoAsync(artistName: String, callback: (ArtistBiography) -> Unit) {
        Thread {
            val artistBiography = getArtistInfoFromRepository(artistName)
            callback(artistBiography)
        }.start()
    }

    private fun getArtistInfoFromRepository(artistName: String): ArtistBiography {
        val dbArticle = getArticleFromDB(artistName)
        val artistBiography : ArtistBiography

        if (dbArticle != null) {
            artistBiography = dbArticle.markItAsLocal()
        } else {
            artistBiography = getArticleFromService(artistName)
            if (artistBiography.biography.isNotEmpty()) {
                insertArtistToDB(artistBiography)
            }
        }
        return artistBiography
    }

    private fun ArtistBiography.markItAsLocal() = copy(biography = "[*]$biography")

    private fun getArtistFromExternalData(serviceData:String?, artistName: String): ArtistBiography{
        val gson = Gson()
        val jobj = gson.fromJson(
            serviceData,
            JsonObject::class.java
        )
        val artist = jobj["artist"].asJsonObject
        val bio = artist["bio"].asJsonObject
        val extract = bio["content"]
        val url = artist["url"]

        val text = extract?.asString?: "No Results"

        return ArtistBiography(artistName, text, url.asString)
    }

    private fun getArticleFromDB(artistName:String): ArtistBiography? {
        val artistEntity = articleDatabase.ArticleDao().getArticleByArtistName(artistName)
        return artistEntity?.let {
            ArtistBiography(artistName, artistEntity.biography, artistEntity.articleUrl)
        }
    }

    private fun getArticleFromService(artistName:String): ArtistBiography{
        var artistBiography = ArtistBiography(artistName, "", "")

        try {
            val callResponse = getSongFromService(artistName)
            artistBiography = getArtistFromExternalData(callResponse.body(), artistName)
        } catch (e1:Exception){
            e1.printStackTrace()
        }

        return artistBiography
    }

    private fun getSongFromService(artistName: String) =
        lastFMAPI!!.getArtistInfo(artistName).execute()

    private fun insertArtistToDB(artistBiography: ArtistBiography) {
        Thread {
            dataBase!!.ArticleDao()
                .insertArticle(ArticleEntity(artistBiography.artistName, artistBiography.biography, artistBiography.articleUrl))
        }.start()
    }



}