package ayds.songinfo.moredetails.fulllogic.presenter

import ayds.songinfo.R
import ayds.songinfo.moredetails.fulllogic.view.OtherInfoView
import ayds.songinfo.moredetails.fulllogic.model.repository.OtherInfoRepository

class OtherInfoPresenter(private val view: OtherInfoView,
                         private val repository: OtherInfoRepository) {

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