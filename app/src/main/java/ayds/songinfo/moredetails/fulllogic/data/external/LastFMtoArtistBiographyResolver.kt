package ayds.songinfo.moredetails.fulllogic.data.external

import ayds.songinfo.moredetails.fulllogic.ArtistBiography
import com.google.gson.Gson
import com.google.gson.JsonObject

interface LastFMtoArtistBiographyResolver{
    fun map(serviceData:String?, artistName:String): ArtistBiography
}

private const val ARTIST = "artist"
private const val BIO = "bio"
private const val CONTENT = "content"
private const val URL = "url"

private const val NO_RESULTS = "No Results"

internal class LastFMtoArtistBiographyResolverImpl:LastFMtoArtistBiographyResolver{
    override fun map(serviceData:String?, artistName: String): ArtistBiography{
        val gson = Gson()
        val jobj = gson.fromJson(serviceData, JsonObject::class.java)
        val artist = jobj[ARTIST].asJsonObject
        val bio = artist[BIO].asJsonObject
        val extract = bio[CONTENT]
        val url = artist[URL]

        val text = extract?.asString?: NO_RESULTS

        return ArtistBiography(artistName, text, url.asString)
    }
}