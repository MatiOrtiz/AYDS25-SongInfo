package ayds.songinfo.moredetails.fulllogic.data

import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.data.external.OtherInfoService
import ayds.songinfo.moredetails.fulllogic.data.local.OtherInfoLocalStorage
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository


class OtherInfoRepositoryImpl(private val otherInfoLocalStorage: OtherInfoLocalStorage,
    private val otherInfoService: OtherInfoService): OtherInfoRepository {

    override fun getArtistInfo(artistName: String):ArtistBiography {
        val dbArticle = otherInfoLocalStorage.getArticle(artistName)
        val artistBiography : ArtistBiography

        if(dbArticle!=null)
            artistBiography = dbArticle.markItAsLocal()
        else {
            artistBiography = otherInfoService.getArticle(artistName)
            if(artistBiography.biography.isNotEmpty())
                otherInfoLocalStorage.insertArtist(artistBiography)
        }

        return artistBiography
    }

    private fun ArtistBiography.markItAsLocal() = copy(isLocallyStored = true)

}