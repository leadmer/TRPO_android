package etu.vt.trpo_android.repository

import etu.vt.trpo_android.present.retrofit.interfaces.NnpiServiceApi

object PictureRepositoryProvider{

    /**
     *   Provider (Singleton object) for create Picture Repository
     **/

    fun providePictureRepository(): PictureRepository{
        return PictureRepository(NnpiServiceApi.createService())
    }
}