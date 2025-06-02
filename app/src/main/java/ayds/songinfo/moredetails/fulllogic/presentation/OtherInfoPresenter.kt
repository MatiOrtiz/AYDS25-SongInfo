package ayds.songinfo.moredetails.fulllogic.presentation

import ayds.songinfo.R
import ayds.songinfo.moredetails.fulllogic.domain.OtherInfoRepository

class OtherInfoPresenter(private val view: OtherInfoActivity,
                         private val repository: OtherInfoRepository
) {

    fun onCreate() {
        val artistName = view.getArtistName()

        view.setContentView(R.layout.activity_other_info)

        view.initViewProperties()
        repository.initDB(view.context)
        repository.initLastFMApi()
        repository.initArticleDatabase(view.context)
        repository.getArtistInfoAsync(artistName) {artistBiography ->
            view.updateUI(artistBiography)
        }
    }

}