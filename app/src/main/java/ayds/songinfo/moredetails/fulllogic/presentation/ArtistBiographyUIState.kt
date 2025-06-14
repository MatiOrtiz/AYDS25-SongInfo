package ayds.songinfo.moredetails.fulllogic.presentation

data class ArtistBiographyUIState(
    val artistName: String,
    val biography: String,
    val articleUrl : String,
    val imageUrl: String = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Lastfm_logo.svg/320px-Lastfm_logo.svg.png",
)
