//package etu.vt.trpo_android.util
//
//package etu.vt.trpo_android.present.presenter
//
//import android.Manifest
//import android.app.Activity
//import android.app.ActivityOptions
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Matrix
//import android.media.ExifInterface
//import android.media.MediaScannerConnection
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.fragment.app.Fragment
//import com.arellomobile.mvp.InjectViewState
//import com.arellomobile.mvp.MvpAppCompatFragment
//import com.arellomobile.mvp.MvpPresenter
//import etu.vt.trpo_android.R
//import etu.vt.trpo_android.present.view.PictureView
//import etu.vt.trpo_android.ui.Fragment.PictureFragment
//import etu.vt.trpo_android.util.DecodeBitmapFromInputStream
//import kotlinx.android.synthetic.main.picture_fragment.*
//import java.io.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//@InjectViewState
//class PicturePresenter : MvpPresenter<PictureView>() {
//
//    lateinit var mainView: PictureView
//    private lateinit var currentPhotoPath: String
//    private val CAMERA_REQUEST_CODE = 1
//    private val OPEN_PICTURE_REQUEST_CODE = 2
//    private val MY_CAMERA_PERMISSION_REQUEST = 100
//    private val WRITE_READ_FILE_PERMISSION_REQUEST = 102
//    private val WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST = 103
//    private var photoFile: File? = null
//    private val bitmapFile: File? = null
//    private var photoFilePath: String = ""
//    private var outputFile: Uri? = null
//
//
//    fun onShowPicture(){
//        viewState.showPicture()
//    }
//
//    override fun attachView(view: PictureView?) {
//        super.attachView(view)
//        this.mainView = view!!
//        viewState.showPicture()
//    }
//
//    private fun getExifAngle(path: InputStream, fragment: MvpAppCompatFragment): Float {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            requestPermissionsOpenFile(fragment)
//        }
//        var angle = 0
//        try {
//            val ei = ExifInterface(path)
//            val orientation: Int = ei.getAttributeInt(
//                ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED
//            )
//            when (orientation) {
//                ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
//                ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
//                ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
//            }
//        } catch (e: java.lang.Exception) {
//            Log.d("getExifAngle", e.toString())
//        }
//        return angle.toFloat()
//    }
//
//    fun onActivityResultCamera(requestCode: Int, resultCode: Int, data: Intent?, fragment: MvpAppCompatFragment, imView: ImageView) {
//        val imViewHeight = imView.height
//        val imViewWidth = imView.width
//        Log.d("imView width is ", imViewWidth.toString())
//        Log.d("imView height is ", imViewHeight.toString())
//
//        when (requestCode) {
//            CAMERA_REQUEST_CODE -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    //if (data != null && data.hasExtra("data")) {
//                    try {
//                        val stream = fragment.context!!.contentResolver.openInputStream(outputFile!!)
//                        val matrix = Matrix()
//                        //stream?.close()
//                        //stream = fragment.context!!.contentResolver.openInputStream(outputFile!!)
//                        matrix.postRotate(getExifAngle(stream!!, fragment))
//
//                        val out: Bitmap? = DecodeBitmapFromInputStream().run {
//                            decodeSampledBitmapFromInputStream(stream, imViewWidth, imViewHeight)
//                        }
//                        stream.close()
//                        Log.d("mbitmap width is ", PictureFragment.SingleBitmap.mbitmap?.height.toString())
//                        Log.d("mbitmap height is ", PictureFragment.SingleBitmap.mbitmap?.width.toString())
//                        PictureFragment.SingleBitmap.mbitmap =
//                            Bitmap.createBitmap(out!!, 0, 0, out.width, out.height, matrix, true)
//
//                        imView.setImageBitmap(PictureFragment.SingleBitmap.mbitmap)
//
//                    } catch (e: Exception) {
//                        Log.d("clickTakePicture", e.toString())
//                    }
//                    // }
//                } else if (resultCode == Activity.RESULT_CANCELED) {
//                    Log.d("CAMERA_REQUEST_CODE", "Canceled")
//                }
//
//            }
//
//
//            OPEN_PICTURE_REQUEST_CODE -> {
//                if(resultCode == Activity.RESULT_OK && data != null) {
//                    try {
//                        val imageUri = data.data
//                        Log.d("imageUri", data.data.toString())
//
//                        photoFilePath = imageUri?.path!!
//                        var imageStream = fragment.context!!.contentResolver.openInputStream(imageUri!!)
//
//
////                        imageStream?.close()
////                        imageStream = fragment.context!!.contentResolver.openInputStream(imageUri!!)
//                        val matrix = Matrix()
//                        matrix.postRotate(getExifAngle(imageStream!!, fragment))
//
//                        val out: Bitmap? = DecodeBitmapFromInputStream().run {
//                            decodeSampledBitmapFromInputStream(imageStream, imViewWidth, imViewHeight)
//                        }
//                        Log.d("mbitmap width is ", PictureFragment.SingleBitmap.mbitmap?.height.toString())
//                        Log.d("mbitmap height is ", PictureFragment.SingleBitmap.mbitmap?.width.toString())
//                        imageStream.close()
//                        PictureFragment.SingleBitmap.mbitmap =
//                            Bitmap.createBitmap(out!!, 0, 0, out.width, out.height, matrix, true)
//                        //PictureFragment.SingleBitmap.mbitmap = Bitmap.createBitmap(out, 0, 0, out.width, out.height, matrix, true)
//                        Log.d("selectedImage", PictureFragment.SingleBitmap.mbitmap.toString())
//                        imView.setImageBitmap(PictureFragment.SingleBitmap.mbitmap)
//
//
//                    } catch (e: FileNotFoundException) {
//                        Log.d("clickOpenPicture", e.toString())
//                    }
//                }
//                else if (resultCode == Activity.RESULT_CANCELED) {
//                    Log.d("CAMERA_REQUEST_CODE", "Canceled")
//                }
//            }
//        }
//    }
//
//    fun requestPermissionsCamera(fragment: MvpAppCompatFragment) {
//        val permissionCamera = ContextCompat.checkSelfPermission(fragment.context!!,
//            android.Manifest.permission.CAMERA)
//
//        if (permissionCamera != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                fragment.requireActivity(),
//                arrayOf(
//                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ), MY_CAMERA_PERMISSION_REQUEST
//            )
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun requestPermissionsOpenFile(fragment: MvpAppCompatFragment) {
//        val permissionWrite = ContextCompat.checkSelfPermission(fragment.context!!,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val permissionMedia = ContextCompat.checkSelfPermission(fragment.context!!,
//            android.Manifest.permission.ACCESS_MEDIA_LOCATION)
//
//        if (permissionWrite != PackageManager.PERMISSION_GRANTED ||
//            permissionMedia != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                fragment.requireActivity(),
//                arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_MEDIA_LOCATION
//                ), WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST
//            )
//        }
//    }
//
//    fun requestPermissionsWriteFile(fragment: MvpAppCompatFragment) {
//        val permissionWrite = ContextCompat.checkSelfPermission(fragment.context!!,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        if (permissionWrite != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                fragment.requireActivity(),
//                arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ), WRITE_READ_FILE_PERMISSION_REQUEST
//            )
//        }
//    }
//
//
//    @Throws(IOException::class)
//    fun createImageFile(fragment: MvpAppCompatFragment): File {
//        // Create an image file name
//        val file: File
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        Log.d("TimeStamp is ", timeStamp)
//        val storageDir = fragment.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        Log.d("mkdir dir is ", storageDir!!.exists().toString())
//
//        if (!storageDir.exists() && storageDir.isDirectory) {
//            try {
//                storageDir.mkdir()
//            } catch (ex: SecurityException) {
//                val errorMessage = "Не удалось создать папку и сохранить файл!"
//                viewState.pushToast(errorMessage)
//            }
//        }
//        file = File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//
//
//        Log.d("Storage dir is ", storageDir.toString())
//        return file
//    }
//
//    fun requestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray,
//        fragment: MvpAppCompatFragment
//    ) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults.isNotEmpty() && grantResults.lastIndex+1 >= 2){
//            viewState.pushToast(grantResults.toString())
//
//            when(requestCode) {
//                MY_CAMERA_PERMISSION_REQUEST -> {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[2] == PackageManager.PERMISSION_GRANTED
//                    )
//                        clickTakePicture(fragment)
//                }
//
//                WRITE_READ_FILE_PERMISSION_REQUEST -> {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
//                        clickSavePicture(fragment)
//                }
//
//                WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST -> {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[2] == PackageManager.PERMISSION_GRANTED)
//                        clickOpenPicture(fragment)
//                }
//            }
//        }
//    }
//
//    fun clickOpenPicture(fragment: MvpAppCompatFragment){
//        requestPermissionsWriteFile(fragment)
//        val intentOpenPicture = Intent(Intent.ACTION_PICK)
//        intentOpenPicture.type = "image/*"
//        fragment.startActivityForResult(intentOpenPicture, OPEN_PICTURE_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(fragment.requireActivity()).toBundle())
//    }
//
//    fun clickTakePicture(fragment: MvpAppCompatFragment){
//
//        requestPermissionsCamera(fragment)
//        val file = createImageFile(fragment)
//        outputFile = FileProvider.getUriForFile(fragment.context!!, fragment.context!!.applicationContext.packageName + ".provider", file)
//        photoFilePath = file.path
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile)
//        fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(fragment.requireActivity()).toBundle())
//    }
//
//    fun clickSavePicture(fragment: MvpAppCompatFragment){
//        //val photoFile: File?
//        try {
//            photoFile = createImageFile(fragment)
//            val out = FileOutputStream(photoFile)
//            PictureFragment.SingleBitmap.mbitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, out)
//            out.flush()
//            out.close()
//            MediaStore.Images.Media.insertImage(fragment.activity?.contentResolver, photoFile!!.absolutePath, photoFile!!.name, photoFile!!.name)
//            MediaScannerConnection.scanFile(fragment.activity, arrayOf(photoFile.toString()), null) {
//                    path, uri ->
//                Log.i("ExternalStorage", "Scanned $path:")
//                Log.i("ExternalStorage", "-> uri=$uri")
//            }
//            viewState.pushToast("Сохранил " + photoFile!!.path.toString())
//        }catch (e: Exception){
//            viewState.pushToast("Ошибка при сохранении. Повторите попытку.")
//            Log.d("MyIlnarLog2", e.toString())
//        }
//    }
//
//    @Throws(IOException::class)
//    fun createBitmapFile(fragment: MvpAppCompatFragment): File {
//        // Create an image file name
//        val file: File
//
//        val storageDir = fragment.context?.getExternalFilesDir(Environment.DIRECTORY_DCIM)
//        Log.d("mkdir dir is ", storageDir!!.exists().toString())
//
//        if (!storageDir.exists() && storageDir.isDirectory) {
//            try {
//                storageDir.mkdir()
//            } catch (ex: SecurityException) {
//                val errorMessage = "Не удалось создать папку и сохранить файл!"
//                viewState.pushToast(errorMessage)
//            }
//        }
//        file = File.createTempFile(
//            "mbitmap", /* prefix */
//            null, /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//
//
//        Log.d("Storage dir is ", storageDir.toString())
//        return file
//    }
//}