package etu.vt.trpo_android.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

data class ImageRequest (
    //@SerializedName("arrPicture")
    //@Expose
    @JsonProperty("arrPicture")
    var arrPicture: File
)

//data class ImageResult (val id: Int, val type: String, val name: String, val probability: Double)
data class ImageResult (
    //@SerializedName("content")
    //@Expose
    @JsonProperty("content")
    var content: String,
    //@SerializedName("arrPicture")
    //@Expose
    @JsonProperty("arrPicture")
    var arrPicture: File
)