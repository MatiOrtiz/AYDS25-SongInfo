import ayds.songinfo.moredetails.fulllogic.data.OtherInfoRepositoryImpl
import ayds.songinfo.moredetails.fulllogic.data.external.OtherInfoService
import ayds.songinfo.moredetails.fulllogic.data.local.OtherInfoLocalStorage
import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class OtherInfoRepositoryTest {
    private val otherInfoLocalStorage : OtherInfoLocalStorage = mockk()
    private val otherInfoService : OtherInfoService = mockk()
    private val otherInfoRepository : OtherInfoRepository =
        OtherInfoRepositoryImpl(otherInfoLocalStorage, otherInfoService)

    @Test
    fun `on getArtistInfo call getArticle from local storage`() {
        val artistBiography = ArtistBiography("artistName", "biography", "imageUrl", true)
        every { otherInfoLocalStorage.getArticle("artistName") } returns artistBiography

        val result = otherInfoRepository.getArtistInfo("artistName")

        Assert.assertEquals(artistBiography.copy(isLocallyStored = true), result)
        Assert.assertTrue(result.isLocallyStored)
    }

    @Test
    fun `on getArtistInfo call getArticle from service`() {
        val artistBiography = ArtistBiography("artistName", "biography", "imageUrl", false)
        every { otherInfoLocalStorage.getArticle("artistName") } returns null
        every { otherInfoService.getArticle("artistName") } returns artistBiography
        every { otherInfoLocalStorage.insertArtist(artistBiography) } returns Unit

        val result = otherInfoRepository.getArtistInfo("artistName")

        Assert.assertEquals(artistBiography, result)
        Assert.assertFalse(result.isLocallyStored)
    }

    @Test
    fun `on empty bio getArtistInfo call getArticle from service`() {
        val artistBiography = ArtistBiography("artist", "", "url", false)
        every { otherInfoLocalStorage.getArticle("artist") } returns null
        every { otherInfoService.getArticle("artist") } returns artistBiography

        val result = otherInfoRepository.getArtistInfo("artist")

        Assert.assertEquals(artistBiography, result)
        Assert.assertFalse(result.isLocallyStored)
        verify(inverse=true) {otherInfoLocalStorage.insertArtist(artistBiography)}
    }
}