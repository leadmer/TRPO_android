package etu.vt.trpo_android.present.retrofit.interfaces

import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.repository.PictureRepository
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface NnpiServiceApi {

    /**
     *   Method for sending POST request to service by path (baseUrl + @POST(value)) and receiving
     *   JSON data
     *   @param {@Body ImageRequest} Data class contained in request which converting to JSON
     *   @return {Observable<ImageResult>}
     **/
    @POST("/api/picture")
    fun postPictureToServerApi(@Body imageRequest: ImageRequest): Observable<ImageResult>

    /**
     *   Singleton to get Retrofit object
     **/
    companion object Factory{
        val baseUrl = ""
        fun createService(): NnpiServiceApi{
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(NnpiServiceApi::class.java)
        }
    }


}