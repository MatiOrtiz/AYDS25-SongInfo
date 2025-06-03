package ayds.songinfo.moredetails.fulllogic.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ayds.songinfo.R
import ayds.songinfo.moredetails.fulllogic.injector.OtherInfoInjector
import com.squareup.picasso.Picasso

class OtherInfoActivity : Activity() {

    private lateinit var artistBioTextView: TextView
    private lateinit var openUrlButton: Button
    private lateinit var lastFMImageView: ImageView

    private lateinit var presenter : OtherInfoPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_info)

        initViewProperties()
        initPresenter()

        observePresenter()
        getArtistInfoAsync()
    }

    private fun initPresenter(){
        OtherInfoInjector.initGraph(this)
        presenter = OtherInfoInjector.presenter
    }

    private fun observePresenter(){
        presenter.artistBiographyObservable.subscribe{artistBiography -> updateUI(artistBiography)}
    }

    private fun initViewProperties() {
        artistBioTextView = findViewById(R.id.textPane1)
        openUrlButton = findViewById(R.id.openUrlButton1)
        lastFMImageView = findViewById(R.id.imageView1)
    }

    private fun getArtistInfoAsync(){
        Thread {
            getArtistInfo()
        }.start()
    }

    private fun getArtistInfo() {
        val artistName = getArtistName()
        presenter.getArtistInfo(artistName)
    }

    private fun updateUI(uiState: ArtistBiographyUIState) {
        runOnUiThread {
            updateOpenUrlButton(uiState.articleUrl)
            loadLastFMImage(uiState.imageUrl)
            updateArticleText(uiState.articleUrl)
        }
    }

    private fun updateOpenUrlButton(url:String) {
        openUrlButton.setOnClickListener {
            navigateToUrl(url)
        }
    }

    private fun navigateToUrl(url:String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun loadLastFMImage(url:String) {
        Picasso.get().load(url).into(lastFMImageView)
    }

    private fun updateArticleText(infoHtml:String) {
        artistBioTextView.text = Html.fromHtml(infoHtml)
    }

    private fun getArtistName() =
        intent.getStringExtra(ARTIST_NAME_EXTRA) ?: throw Exception("Missing artist name")

    companion object{
        const val ARTIST_NAME_EXTRA = "artistName"
    }

}