package ayds.songinfo.moredetails.fulllogic.presentation

import ayds.observer.Observable
import ayds.observer.Subject
import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository

interface OtherInfoPresenter {
    val artistBiographyObservable: Observable<ArtistBiographyUIState>
    fun getArtistInfo(artistName: String)
}

internal class OtherInfoPresenterImpl(private val repository:OtherInfoRepository,
                                      private val artistBiographyDescriptionHelper: ArtistBiographyDescriptionHelper
                                    ) : OtherInfoPresenter {
    override val artistBiographyObservable = Subject<ArtistBiographyUIState>()

    override fun getArtistInfo(artistName: String) {
        val artistBiography = repository.getArtistInfo(artistName)

        val uiState = artistBiography.toUIState()

        artistBiographyObservable.notify(uiState)
    }

    private fun ArtistBiography.toUIState() = ArtistBiographyUIState(artistName,
        artistBiographyDescriptionHelper.getDescription(this), articleUrl)

}