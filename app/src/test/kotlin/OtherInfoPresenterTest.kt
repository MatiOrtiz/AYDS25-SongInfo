import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository
import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.presentation.ArtistBiographyDescriptionHelper
import ayds.songinfo.moredetails.fulllogic.presentation.ArtistBiographyUIState
import ayds.songinfo.moredetails.fulllogic.presentation.OtherInfoPresenterImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class OtherInfoPresenterTest {
    private val otherInfoRepository : OtherInfoRepository = mockk()
    private val artistBiographyDescriptionHelper: ArtistBiographyDescriptionHelper = mockk()
    private val otherInfoPresenter =
        OtherInfoPresenterImpl(otherInfoRepository, artistBiographyDescriptionHelper)

    @Test
    fun `getArtistInfo should return artist biography ui state`() {
        val artistBiography = ArtistBiography("artistName", "description", "articleUrl")
        every { otherInfoRepository.getArtistInfo("artistName") } returns artistBiography
        every { artistBiographyDescriptionHelper.getDescription(artistBiography) } returns "description"

        val artistBiographyTester : (ArtistBiographyUIState) -> Unit = mockk(relaxed = true)

        otherInfoPresenter.artistBiographyObservable.subscribe(artistBiographyTester)
        otherInfoPresenter.getArtistInfo("artistName")

        val result = ArtistBiographyUIState("artistName", "description", "articleUrl")
        verify{artistBiographyTester(result)}
    }

}