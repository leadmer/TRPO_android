package etu.vt.trpo_android.repository

import com.google.gson.Gson
import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.present.retrofit.interfaces.NnpiServiceApi
import io.reactivex.Observable
import retrofit2.Call

/**
 *   Picture repository for action with service
 **/
class PictureRepository(val apiServiceApi: NnpiServiceApi) {

    /**
     *   Method for sending request and response data from service
     *   @param {NnpiServiceApi} Interface for service
     *   @return {Observable<ImageResult>}
     **/
    fun sendPictureForResult(imageRequestJson: ImageRequest): Call<ImageResult> {
        return apiServiceApi.postPictureToServerApi(imageRequestJson)
    }



}