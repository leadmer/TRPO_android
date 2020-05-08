package etu.vt.trpo_android.present.presenter


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.MvpPresenter
import com.google.gson.Gson
import etu.vt.trpo_android.model.ImageRequest
import etu.vt.trpo_android.model.ImageResult
import etu.vt.trpo_android.present.view.PictureView
import etu.vt.trpo_android.repository.PictureRepository
import etu.vt.trpo_android.ui.Fragment.PictureFragment
import etu.vt.trpo_android.util.DecodeBitmapFromInputStream
import etu.vt.trpo_android.util.PermissionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@InjectViewState
class PicturePresenter : MvpPresenter<PictureView>() {

    lateinit var mainView: PictureView
    private lateinit var currentPhotoPath: String
    private val CAMERA_REQUEST_CODE = 1
    private val OPEN_PICTURE_REQUEST_CODE = 2
    private val MY_CAMERA_PERMISSION_REQUEST = 100
    private val WRITE_READ_FILE_PERMISSION_REQUEST = 102
    private val WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST = 103
    private val INTERNET_PERMISSION_REQUEST = 104
    private var photoFile: File? = null
    private var photoFilePath: String = ""
    private var outputFile: Uri? = null


    fun onShowPicture(){
        viewState.showPicture(PictureFragment.SingleBitmap.mbitmap)
    }

    override fun attachView(view: PictureView?) {
        super.attachView(view)
        this.mainView = view!!
        viewState.showPicture(PictureFragment.SingleBitmap.mbitmap)
    }

    private fun getExifAngle(path: InputStream, fragment: MvpAppCompatFragment): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionManager().requestPermissionsOpenFile(fragment)
        }
        var angle = 0
        try {
            val ei = ExifInterface(path)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
            }
        } catch (e: java.lang.Exception) {
            Log.d("getExifAngle", e.toString())
        }
        return angle.toFloat()
    }

    /***
     *
     */
    fun onActivityResultCamera(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        fragment: MvpAppCompatFragment,
        imView: ImageView
    ) {
        val imViewHeight = imView.height
        val imViewWidth = imView.width
        Log.d("imView width is ", imViewWidth.toString())
        Log.d("imView height is ", imViewHeight.toString())

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    //if (data != null && data.hasExtra("data")) {
                    try {
                        var stream = fragment.requireContext().contentResolver.openInputStream(outputFile!!)
                        if (stream == null )
                            Log.d("Stream null +++++", "imstream")
                        val buffer = BufferedInputStream(stream!!)

                        val out  = DecodeBitmapFromInputStream().run {
                            decodeSampledBitmapFromInputStream(buffer, imViewWidth, imViewHeight)
                        }
                        if (out == null )
                            Log.d("OUT null +++++", "OUT NULL")
                        stream.close()

                        val matrix = Matrix()
                        stream = fragment.requireContext().contentResolver.openInputStream(outputFile!!)
                        matrix.postRotate(getExifAngle(stream!!, fragment))


                        stream.close()
                        buffer.close()

                        Log.d("mbitmap width is ", PictureFragment.SingleBitmap.mbitmap?.height.toString())
                        Log.d("mbitmap height is ", PictureFragment.SingleBitmap.mbitmap?.width.toString())
                        PictureFragment.SingleBitmap.mbitmap =
                            Bitmap.createBitmap(out!!, 0, 0, out.width, out.height, matrix, true)

                        imView.setImageBitmap(PictureFragment.SingleBitmap.mbitmap)

                    } catch (e: IOException) {
                        Log.d("Can`t close streams", "Can`t close streams: $e")
                    }
                    catch (e: FileNotFoundException) {
                        Log.d("Not found file", "Image file not found: $e")
                    }
                    catch (e: Exception) {
                        Log.d("clickTakePicture", e.toString())
                    }

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("CAMERA_REQUEST_CODE", "Canceled")
                }

            }


            OPEN_PICTURE_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    try {
                        val imageUri = data.data
                        Log.d("imageUri", data.data.toString())

                        photoFilePath = imageUri?.path!!
                        var imageStream = fragment.requireContext().contentResolver.openInputStream(imageUri)
                        val buffer = BufferedInputStream(imageStream!!)

                        val out: Bitmap? = DecodeBitmapFromInputStream()
                            .decodeSampledBitmapFromInputStream(buffer, imViewWidth, imViewHeight)
                        if (out == null ) {
                            Log.d("out null +", "out")
                            viewState.pushToast("Не смог преобразовать фото")
                        }

                        try {
                            imageStream.close()
                            buffer.close()
                        }catch (e: IOException){
                            Log.d("Streams close", "Can`t close image streams")
                        }

                        imageStream = fragment.requireContext().contentResolver.openInputStream(imageUri)
                        if (imageStream == null )
                            Log.d("imageStream second null +", "imstream")

                        val matrix = Matrix()
                        matrix.postRotate(getExifAngle(imageStream!!, fragment))
                        imageStream.close()

                        PictureFragment.SingleBitmap.mbitmap =
                            Bitmap.createBitmap(out!!, 0, 0, out.width, out.height, matrix, true)

                        Log.d("mbitmap width is ", PictureFragment.SingleBitmap.mbitmap?.height.toString())
                        Log.d("mbitmap height is ", PictureFragment.SingleBitmap.mbitmap?.width.toString())
                        Log.d("selectedImage", PictureFragment.SingleBitmap.mbitmap.toString())

                        imView.setImageBitmap(PictureFragment.SingleBitmap.mbitmap)


                    } catch (e: IOException) {
                        Log.d("Can`t close streams", "Can`t close streams: $e")
                    }
                    catch (e: FileNotFoundException) {
                        Log.d("Not found file", "Image file not found: $e")
                    }
                    catch (e: Exception) {
                        Log.d("clickOpenPicture", e.toString())
                    }
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d("CAMERA_REQUEST_CODE", "Canceled")
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(fragment: MvpAppCompatFragment): File {
        // Create an image file name
        val file: File
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("TimeStamp is ", timeStamp)
        val storageDir = fragment.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("mkdir dir is ", storageDir!!.exists().toString())

        if (!storageDir.exists() && storageDir.isDirectory) {
            try {
                storageDir.mkdir()
            } catch (ex: SecurityException) {
                val errorMessage = "Не удалось создать папку и сохранить файл!"
                viewState.pushToast(errorMessage)
            }
        }
        file = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }


        Log.d("Storage dir is ", storageDir.toString())
        return file
    }

    fun requestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        fragment: MvpAppCompatFragment
    ) {
        if (grantResults.isNotEmpty() && grantResults.lastIndex+1 >= 2){
            viewState.pushToast(grantResults.toString())

            when(requestCode) {
                MY_CAMERA_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED
                    )
                        clickTakePicture(fragment)
                }

                WRITE_READ_FILE_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        clickSavePicture(fragment)
                }

                WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        clickOpenPicture(fragment)
                }

                INTERNET_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        prepareImageRequest(PictureFragment.SingleBitmap.mbitmap, fragment)
                }
            }
        }
    }

    fun clickOpenPicture(fragment: MvpAppCompatFragment){
        PermissionManager().requestPermissionsWriteFile(fragment)
        val intentOpenPicture = Intent(Intent.ACTION_PICK)
        intentOpenPicture.type = "image/*"
        fragment.startActivityForResult(
            intentOpenPicture,
            OPEN_PICTURE_REQUEST_CODE,
            ActivityOptions.makeSceneTransitionAnimation(fragment.requireActivity()).toBundle()
        )
    }

    fun clickTakePicture(fragment: MvpAppCompatFragment){

        PermissionManager().requestPermissionsCamera(fragment)
        val file = createImageFile(fragment)
        outputFile = FileProvider.getUriForFile(
            fragment.requireContext(),
            fragment.requireContext().applicationContext.packageName + ".provider", file
        )
        photoFilePath = file.path
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile)
        fragment.startActivityForResult(
            intent,
            CAMERA_REQUEST_CODE,
            ActivityOptions.makeSceneTransitionAnimation(fragment.requireActivity()).toBundle()
        )
    }

    fun clickSavePicture(fragment: MvpAppCompatFragment){
        //val photoFile: File?
        try {
            photoFile = createImageFile(fragment)
            val out = FileOutputStream(photoFile)
            PictureFragment.SingleBitmap.mbitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, out)
            out.flush()
            out.close()
            MediaStore.Images.Media.insertImage(
                fragment.activity?.contentResolver,
                photoFile!!.absolutePath,
                photoFile!!.name,
                photoFile!!.name
            )
            MediaScannerConnection.scanFile(fragment.activity, arrayOf(photoFile.toString()), null) {
                    path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
            viewState.pushToast("Сохранил " + photoFile!!.path.toString())
        }catch (e: Exception){
            viewState.pushToast("Ошибка при сохранении. Повторите попытку.")
            Log.d("MyIlnarLog2", e.toString())
        }
    }

    fun createRequestPicture(repository: PictureRepository, imageRequest: ImageRequest){
        //val gsonRequest = Gson().newBuilder().setPrettyPrinting().create().toJson(imageRequest)
        //imageRequest.arrPicture = "/9j/4AAQSkZJRgABAQAAAQABAAD/"
        Log.d("imageJSON", imageRequest.arrPicture)
        val call = repository.sendPictureForResult(imageRequest)
            call.enqueue(object : Callback<ImageResult>{

                override fun onResponse(call: Call<ImageResult>, response: Response<ImageResult>) {
                    if (response.isSuccessful){
                        Log.d("response succ", response.body()!!.content)
                        Log.d("response succ", response.body()!!.arrPicture)
                        viewState.pushToast("response succ : ${response.body()!!.content}")
                    }
                }

                override fun onFailure(call: Call<ImageResult>, t: Throwable) {
                    Log.d("response fail", t.localizedMessage!!)
                    viewState.pushToast("response fail : ${t.localizedMessage!!}")
                }
            })
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .doOnNext {
//                val content = Gson().fromJson(it.getOrNull()?.content, String::class.java)
//                Log.d("onnext response", "name is $content")
//            }
//            .doOnComplete {
//                Log.d("oncomplete", "qweqsdawd")
//            }
//            .subscribe({
//                result ->
//                val content = Gson().fromJson(result.getOrNull()?.content, String::class.java)
//                Log.d("result response", "name is $content")
//            }, {
//                error ->
//                Log.d("error response", "name is ${error.message}")
//            })
    }

    fun prepareImageRequest(bitmap : Bitmap?, fragment: MvpAppCompatFragment): ImageRequest{
        if (bitmap == null)
            throw Exception("Bitmap is null")
        val file = File.createTempFile(
            "compresbitmap",
            null, fragment.context?.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        )
        val buffStream = BufferedOutputStream(
            FileOutputStream(file)
        )
        val byteArrOutStr = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrOutStr)
        val arr = byteArrOutStr.toByteArray()
        buffStream.write(arr)
        //Test bitmap compress
        val bit = BitmapFactory.decodeByteArray(arr,0,arr.size)

        Log.d("new bitmap height", bit.height.toString())
        Log.d("new bitmap width", bit.width.toString())
        viewState.showPicture(bit)
        val byteArr = byteArrOutStr.toByteArray()
        byteArrOutStr.close()
        return ImageRequest(Base64.encodeToString(byteArr, Base64.NO_WRAP))
    }
}