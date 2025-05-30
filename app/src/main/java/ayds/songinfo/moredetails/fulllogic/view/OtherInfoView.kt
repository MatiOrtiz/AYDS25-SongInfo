package ayds.songinfo.moredetails.fulllogic.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ayds.songinfo.R
import ayds.songinfo.moredetails.fulllogic.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.Injector
import ayds.songinfo.moredetails.fulllogic.OtherInfoWindow
import ayds.songinfo.moredetails.fulllogic.OtherInfoWindow.Companion
import ayds.songinfo.moredetails.fulllogic.OtherInfoWindow.Companion.textToHtml
import com.squareup.picasso.Picasso
import java.util.Locale

class OtherInfoView : Activity() {

    private lateinit var artistBioTextView: TextView
    private lateinit var openUrlButton: Button
    private lateinit var lastFMImageView: ImageView

    val presenter = Injector.provideOtherInfoPresenter()

    val context: Context
        get() = this

    fun initViewProperties() {
        artistBioTextView = findViewById(R.id.textPane1)
        openUrlButton = findViewById(R.id.openUrlButton1)
        lastFMImageView = findViewById(R.id.imageView1)
    }

    fun updateUI(artistBiography: ArtistBiography) {
        runOnUiThread {
            updateOpenUrlButton(artistBiography)
            loadLastFMImage()
            updateArticleText(artistBiography)
        }
    }

    private fun updateOpenUrlButton(artistBiography: ArtistBiography) {
        openUrlButton!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(artistBiography.articleUrl))
            startActivity(intent)
        }
    }

    private fun loadLastFMImage() {
        val imageUrl =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Lastfm_logo.svg/320px-Lastfm_logo.svg.png"
        Log.e("TAG", "Get Image from $imageUrl")
        Picasso.get().load(imageUrl).into(lastFMImageView)
    }

    private fun updateArticleText(artistBiography: ArtistBiography) {
        val text = artistBiography.biography.replace("\\n", "\n")
        artistBioTextView.text = Html.fromHtml(textToHtml(text, artistBiography.artistName))
    }

    fun getArtistName() =
        intent.getStringExtra(OtherInfoWindow.ARTIST_NAME_EXTRA) ?: throw Exception("Missing artist name")

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