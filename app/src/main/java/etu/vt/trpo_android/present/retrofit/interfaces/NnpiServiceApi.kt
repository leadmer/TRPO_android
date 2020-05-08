package etu.vt.trpo_android.present.retrofit.interfaces

import com.google.gson.Gson
import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.repository.PictureRepository
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*
import java.util.concurrent.TimeUnit

interface NnpiServiceApi {

    /**
     *   Method for sending POST request to service by path (baseUrl + @POST(value)) and receiving
     *   JSON data
     *   @param {@Body ImageRequest} Data class contained in request which converting to JSON
     *   @return {Observable<ImageResult>}
     **/
    @POST("/api/picture")
    @Headers(
        "Accept-Encoding: gzip,deflate",
        "Content-Type: Application/Json;charset=UTF-8",
        "Accept: Application/Json",
        "User-Agent: Retrofit 2.3.0"
     )
    fun postPictureToServerApi(@Body imageRequestJson: ImageRequest): Call<ImageResult>

    /**
     *   Singleton to get Retrofit object
     **/
    companion object Factory{
        private const val baseUrl = "https://polar-taiga-40515.herokuapp.com"
        fun createService(): NnpiServiceApi{
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(NnpiServiceApi::class.java)
        }
    }


}