package ayds.songinfo.moredetails.fulllogic

import ayds.songinfo.moredetails.fulllogic.presenter.OtherInfoPresenter
import ayds.songinfo.moredetails.fulllogic.model.repository.OtherInfoRepositoryImpl
import ayds.songinfo.moredetails.fulllogic.view.OtherInfoView

object Injector {
    fun provideOtherInfoPresenter(): OtherInfoPresenter {
        val repository = OtherInfoRepositoryImpl()
        val view = OtherInfoView()
        return OtherInfoPresenter(view, repository)
    }
}