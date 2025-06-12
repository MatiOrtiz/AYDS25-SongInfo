package ayds.songinfo.moredetails.fulllogic.data.external

import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography

interface OtherInfoService{
    fun getArticle(artistName: String): ArtistBiography
}

internal class OtherInfoServiceImpl(private val lastFMAPI: LastFMAPI,
                                    private val lastFMtoArtistBiographyResolver: LastFMtoArtistBiographyResolver
                                    ): OtherInfoService {

    override fun getArticle(artistName:String): ArtistBiography {
        var artistBiography = ArtistBiography(artistName, "", "")

        try {
            val callResponse = getSongFromService(artistName)
            artistBiography = lastFMtoArtistBiographyResolver.map(callResponse.body(), artistName)
        } catch (e1:Exception){
            e1.printStackTrace()
        }

        return artistBiography
    }

    private fun getSongFromService(artistName: String) =
        lastFMAPI.getArtistInfo(artistName).execute()
}