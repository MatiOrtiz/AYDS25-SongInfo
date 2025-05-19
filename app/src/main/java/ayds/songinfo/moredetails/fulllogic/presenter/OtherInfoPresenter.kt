package ayds.songinfo.moredetails.fulllogic.presenter

import android.os.Bundle
import ayds.songinfo.R
import ayds.songinfo.moredetails.fulllogic.view.OtherInfoView
import ayds.songinfo.moredetails.fulllogic.model.repository.OtherInfoRepository

class OtherInfoPresenter {

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_other_info)

        initViewProperties()
        initDB()
        initLastFMApi()
        initArticleDatabase()
        getArtistInfoAsync()

    }

}