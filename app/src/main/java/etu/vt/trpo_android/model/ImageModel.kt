package etu.vt.trpo_android.model

data class ImageRequest (val imageByteArray: ByteArray)

data class ImageResult (val id: Int, val type: String, val name: String, val probability: Double)