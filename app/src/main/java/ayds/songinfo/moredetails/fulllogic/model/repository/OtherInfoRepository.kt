package ayds.songinfo.moredetails.fulllogic.model.repository

interface OtherInfoRepository {

    fun initDB()

    fun initLastFMApi()

    fun initArticleDatabase()

    fun getArtistInfoAsync()
}