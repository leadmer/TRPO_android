package etu.vt.trpo_android.repository

import com.google.gson.Gson
import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.present.retrofit.interfaces.NnpiServiceApi
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import java.io.File

/**
 *   Picture repository for action with service
 **/
class PictureRepository(val apiServiceApi: NnpiServiceApi) {

    /**
     *   Method for sending request and response data from service
     *   @param {NnpiServiceApi} Interface for service
     *   @return {Observable<ImageResult>}
     **/
    fun sendPictureForResult(imageRequestJson: MultipartBody.Part): Call<ResponseBody> {
        return apiServiceApi.postPictureToServerApi(imageRequestJson)
    }



}