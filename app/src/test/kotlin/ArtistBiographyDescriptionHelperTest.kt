import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import ayds.songinfo.moredetails.fulllogic.presentation.ArtistBiographyDescriptionHelper
import ayds.songinfo.moredetails.fulllogic.presentation.ArtistBiographyDescriptionHelperImpl
import org.junit.Assert
import org.junit.Test

class ArtistBiographyDescriptionHelperTest {
    private val artistBiographyDescriptionHelper : ArtistBiographyDescriptionHelper =
        ArtistBiographyDescriptionHelperImpl()

    @Test
    fun `on local stored artist should return biography`(){
        val artistBiography = ArtistBiography("artistName", "biography", "articleUrl", true)
        val result = artistBiographyDescriptionHelper.getDescription(artistBiography)

        Assert.assertEquals(
            "<html><div width=400>[*]biography</div></html>",
            result
        )
    }

    @Test
    fun `on no local stored artist should return biography`(){
        val artistBiography = ArtistBiography("artistName", "biography", "articleUrl", false)
        val result = artistBiographyDescriptionHelper.getDescription(artistBiography)

        Assert.assertEquals(
            "<html><div width=400>biography</div></html>",
            result
        )
    }

    @Test
    fun `should fix on double slash`() {
        val artistBiography = ArtistBiography("artistName", "biography\\n", "articleUrl", false)
        val result = artistBiographyDescriptionHelper.getDescription(artistBiography)

        Assert.assertEquals(
            "<html><div width=400>biography<br></div></html>",
            result
        )
    }

    @Test
    fun `should map break lines`() {
        val artistBiography = ArtistBiography("artistName", "biography\n", "articleUrl", false)
        val result = artistBiographyDescriptionHelper.getDescription(artistBiography)

        Assert.assertEquals(
            "<html><div width=400>biography<br></div></html>",
            result
        )
    }

    @Test
    fun `should set artist name in bold`() {
        val artistBiography = ArtistBiography("artistName", "biography", "articleUrl", false)
        val result = artistBiographyDescriptionHelper.getDescription(artistBiography)

        Assert.assertEquals(
            "<html><div width=400>biography<b>ARTIST</b></div></html>",
            result
        )
    }

}