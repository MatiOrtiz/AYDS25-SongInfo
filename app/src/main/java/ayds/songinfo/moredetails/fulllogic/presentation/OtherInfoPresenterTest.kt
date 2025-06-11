package ayds.songinfo.moredetails.fulllogic.presentation

import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository
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
    fun `getArtistInfo should return artist biography UI state`() {
        val artistBiography = ArtistBiography("artistName", "description", "articleUrl")
        every { otherInfoRepository.getArtistInfo("artistName") } returns artistBiography
        every { artistBiographyDescriptionHelper.getDescription(artistBiography) } returns "description"

        val artisBiographyTester : ArtistBiographyUIState -> Unit = mockk(relaxed = true)

        otherInfoPresenter.artistBiographyObservable.subscribe(artistBiographyTester)
        otherInfoPresenter.getArtistInfo("artistName")

        val result = ArtisBiogrpahyUIState("artistName", "description", "articleUrl")
        verify{artistBiographyTester(result)}
    }

}