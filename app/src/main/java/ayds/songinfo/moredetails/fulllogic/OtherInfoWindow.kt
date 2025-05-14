package ayds.songinfo.moredetails.fulllogic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.room.Room.databaseBuilder
import ayds.songinfo.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Locale

data class ArtistBiography(val artistName:String, val biography:String, val articleUrl:String)

class OtherInfoWindow : Activity() {
    private var artistBioTextView: TextView? = null
    private var openUrlButton: Button? = null
    private var lastFMImageView: ImageView? = null
    private var dataBase: ArticleDatabase? = null
    private var lastFMAPI: LastFMAPI? = null

    private lateinit var articleDatabase: ArticleDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_other_info)

        initViewProperties()
        initDB()
        initLastFMApi()
        initArticleDatabase()
        getArtistInfoAsync()

    }

    private fun initDB() {
        dataBase = databaseBuilder(
            this,
            ArticleDatabase::class.java, "database-name-thename"
        ).build()
    }

    private fun initLastFMApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        lastFMAPI = retrofit.create(LastFMAPI::class.java)
    }

    private fun initViewProperties() {
        artistBioTextView = findViewById(R.id.textPane1)
        openUrlButton = findViewById(R.id.openUrlButton1)
        lastFMImageView = findViewById(R.id.imageView1)
    }

    private fun initArticleDatabase() {
        articleDatabase = databaseBuilder(
            this,
            ArticleDatabase::class.java, "article-database"
        ).build()
    }

    private fun getArtistInfoAsync(){
        Thread{
            getArtistInfo()
        }.start()
    }

    fun getArtistInfo() {
        val artistBiography = getArtistInfoFromRepository()
        var text = ""
        val finalText = text
        loadImage()
        showError(finalText)
    }

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

    private fun getArtistInfoFromRepository():ArtistBiography{
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

    private fun setOpenUrlButton(urlString: String) {
        openUrlButton!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(urlString))
            startActivity(intent)
        }
    }

    private fun getArtistName() =
        intent.getStringExtra(ARTIST_NAME_EXTRA) ?: throw Exception("Missing artist name")

    private fun loadImage() {
        val imageUrl =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Lastfm_logo.svg/320px-Lastfm_logo.svg.png"
        Log.e("TAG", "Get Image from $imageUrl")
        Picasso.get().load(imageUrl).into(lastFMImageView)
    }

    private fun showError(finalText: String) {
        artistBioTextView!!.text = Html.fromHtml(finalText)
    }


    companion object {
        const val ARTIST_NAME_EXTRA: String = "artistName"

        fun textToHtml(text: String, term: String): String {
            val builder = StringBuilder()

            builder.append("<html><div width=400>")
            builder.append("<font face=\"arial\">")

            val textWithBold = text
                .replace("'", " ")
                .replace("\n", "<br>")
                .replace(
                    ("(?i)$term").toRegex(),
                    "<b>" + term.uppercase(Locale.getDefault()) + "</b>"
                )

            builder.append(textWithBold)

            builder.append("</font></div></html>")

            return builder.toString()
        }
    }
}
