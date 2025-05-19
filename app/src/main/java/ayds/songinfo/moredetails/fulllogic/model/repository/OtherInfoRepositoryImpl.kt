package ayds.songinfo.moredetails.fulllogic.model.repository

import androidx.room.Room.databaseBuilder
import ayds.songinfo.moredetails.fulllogic.ArticleDatabase
import ayds.songinfo.moredetails.fulllogic.ArticleEntity
import ayds.songinfo.moredetails.fulllogic.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.LastFMAPI
import ayds.songinfo.moredetails.fulllogic.OtherInfoWindow.Companion.ARTIST_NAME_EXTRA
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class OtherInfoRepositoryImpl : OtherInfoRepository {
    private lateinit var dataBase: ArticleDatabase
    private lateinit var lastFMAPI: LastFMAPI
    private lateinit var articleDatabase: ArticleDatabase

    override fun initDB() {
        dataBase = databaseBuilder(
            this,
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

    override fun initArticleDatabase() {
        articleDatabase = databaseBuilder(
            this,
            ArticleDatabase::class.java, "article-database"
        ).build()
    }

    override fun getArtistInfoAsync(){
        Thread{
            getArtistInfo()
        }.start()
    }

    private fun getArtistInfo() {
        val artistBiography = getArtistInfoFromRepository()
        updateUI(artistBiography)
    }

    private fun getArtistInfoFromRepository(): ArtistBiography {
        val artistName = getArtistName()
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

    private fun getArtistName() =
        intent.getStringExtra(ARTIST_NAME_EXTRA) ?: throw Exception("Missing artist name")

}