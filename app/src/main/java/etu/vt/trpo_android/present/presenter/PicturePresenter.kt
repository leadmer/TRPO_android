package etu.vt.trpo_android.present.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import etu.vt.trpo_android.present.view.PictureView


@InjectViewState
class PicturePresenter : MvpPresenter<PictureView>() {
    fun onShowPicture(){
        viewState.showPicture()
    }

    override fun attachView(view: PictureView?) {
        super.attachView(view)
        viewState.showPicture()
    }
}