package ayds.songinfo.moredetails.fulllogic.domain

import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography

interface OtherInfoRepository {
    fun getArtistInfo(artistName:String):ArtistBiography
}