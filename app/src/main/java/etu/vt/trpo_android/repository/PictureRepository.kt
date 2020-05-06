package etu.vt.trpo_android.repository

import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.present.retrofit.interfaces.NnpiServiceApi
import io.reactivex.Observable

 /**
 *   Picture repository for action with service
 **/
class PictureRepository(val apiServiceApi: NnpiServiceApi) {

    /**
     *   Method for sending request and response data from service
     *   @param {NnpiServiceApi} Interface for service
     *   @return {Observable<ImageResult>}
     **/
    fun sendPictureForResult(imageRequest: ImageRequest): Observable<ImageResult> {
        return apiServiceApi.postPictureToServerApi(imageRequest)
    }



}