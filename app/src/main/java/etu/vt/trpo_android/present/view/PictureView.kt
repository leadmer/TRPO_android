package etu.vt.trpo_android.present.view

import com.arellomobile.mvp.MvpView

interface PictureView: MvpView {
    fun showPicture()
    fun pushToast(str: String)
}