package ayds.songinfo.moredetails.fulllogic.injector

import ayds.songinfo.moredetails.fulllogic.presentation.OtherInfoPresenter
import ayds.songinfo.moredetails.fulllogic.data.OtherInfoRepositoryImpl
import ayds.songinfo.moredetails.fulllogic.presentation.OtherInfoActivity

object OtherInfoInjector {
    fun provideOtherInfoPresenter(): OtherInfoPresenter {
        val repository = OtherInfoRepositoryImpl()
        val view = OtherInfoActivity()
        return OtherInfoPresenter(view, repository)
    }
}