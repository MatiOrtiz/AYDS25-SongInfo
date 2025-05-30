package ayds.songinfo.moredetails.fulllogic.model.repository

import android.content.Context
import ayds.songinfo.moredetails.fulllogic.ArtistBiography

interface OtherInfoRepository {

    fun initDB(context: Context)

    fun initLastFMApi()

    fun initArticleDatabase(context: Context)

    fun getArtistInfoAsync(artistName:String, callback: (ArtistBiography) -> Unit)
}