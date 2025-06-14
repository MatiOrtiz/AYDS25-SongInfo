package ayds.songinfo.moredetails.fulllogic.presentation

import ayds.songinfo.moredetails.fulllogic.domain.ArtistBiography
import java.util.Locale

interface ArtistBiographyDescriptionHelper{
    fun getDescription(artistBiography: ArtistBiography): String
}

private const val  HEADER = "<html><div width=400>"
private const val FOOTER = "</div></html>"

class ArtistBiographyDescriptionHelperImpl : ArtistBiographyDescriptionHelper{
    override fun getDescription(artistBiography: ArtistBiography): String {
        val text = getTextBiography(artistBiography)
        return textToHtml(text, artistBiography.artistName)
    }

    private fun getTextBiography(artistBiography: ArtistBiography):String {
        val prefix = if(artistBiography.isLocallyStored) "[*]" else ""
        val text = artistBiography.biography.replace("\\n","\n")
        return "$prefix$text"
    }

    private fun textToHtml(text:String, term:String):String{
        val builder = StringBuilder()

        builder.append(HEADER)
        builder.append(FOOTER)

        val textWithBold = text
            .replace("'", " ")
            .replace("\n", "<br>")
            .replace(
                ("(?i)$term").toRegex(),
                "<b>" + term.uppercase(Locale.getDefault()) + "</b>"
            )

        builder.append(textWithBold)

        builder.append(FOOTER)

        return builder.toString()
    }

}