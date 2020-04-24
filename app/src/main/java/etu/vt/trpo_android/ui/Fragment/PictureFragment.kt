package etu.vt.trpo_android.ui.Fragment

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Path
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import etu.vt.trpo_android.R
import etu.vt.trpo_android.present.presenter.PicturePresenter
import etu.vt.trpo_android.present.view.PictureView
import etu.vt.trpo_android.ui.Fragment.PictureFragment.SingleBitmap.mbitmap
import kotlinx.android.synthetic.main.picture_fragment.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class PictureFragment: MvpAppCompatFragment(), PictureView {

    private lateinit var imView: ImageView
    private lateinit var currentPhotoPath: String
    private val CAMERA_REQUEST_CODE = 1
    private val OPEN_PICTURE_REQUEST_CODE = 2
    private val MY_CAMERA_PERMISSION_REQUEST = 100
    private val WRITE_READ_FILE_PERMISSION_REQUEST = 102
    private val WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST = 103
    private var photoFile: File? = null
    private var photoFilePath: String = ""
    private var outputFile: Uri? = null

    object SingleBitmap{
        var mbitmap: Bitmap? = null
    }

    @InjectPresenter
    lateinit var mPicturePresenter: PicturePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null)
            mbitmap = savedInstanceState.getParcelable("mbitmap")
        super.onCreate(savedInstanceState)
        retainInstance = true //save state fragment
        mPicturePresenter.onShowPicture()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        var view = inflater.inflate(R.layout.picture_fragment, container, false)
        imView = view.findViewById(R.id.im_view)

        val pictureButton = view.findViewById<Button>(R.id.take_picture)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val openButton = view.findViewById<Button>(R.id.open_button)

        openButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                clickOpenPicture()
            }
            else
                requestPermissionsWriteFile()
        }

        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                clickSavePicture()
            }
            else
                requestPermissionsWriteFile()
        }

        pictureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                clickTakePicture()
            }
            else
                requestPermissionsCamera()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getExifAngle(path: InputStream): Float {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionsOpenFile()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    //if (data != null && data.hasExtra("data")) {
                          try {
                              //val uri = data!!.data
                              //photoFilePath = uri?.path!!
                              //val imgFile = File(photoFilePath)
                              var stream = context!!.contentResolver.openInputStream(outputFile!!)
                              val out =
                                  BitmapFactory.decodeStream(stream) //data.extras?.get("data") as Bitmap
                              val matrix = Matrix()
                              stream?.close()
                              stream = context!!.contentResolver.openInputStream(outputFile!!)
                              matrix.postRotate(getExifAngle(stream!!))
                              mbitmap =
                                  Bitmap.createBitmap(out, 0, 0, out.width, out.height, matrix, true)
                              Log.d("mbitmap width is ", (mbitmap!!.width).toString()) //test
                              Log.d("mbitmap height is ", (mbitmap!!.height).toString()) //test
                              imView.setImageBitmap(mbitmap)
                          } catch (e: Exception) {
                              Log.d("clickTakePicture", e.toString())
                          }
                   // }
                } else if (resultCode == RESULT_CANCELED) {
                        Log.d("CAMERA_REQUEST_CODE", "Canceled")
                }
            }


            OPEN_PICTURE_REQUEST_CODE -> {
                if(resultCode == RESULT_OK && data != null) {
                        try {
                            val imageUri = data.data
                            Log.d("imageUri", data.data.toString())

                            photoFilePath = imageUri?.path!!
                            //костыль
//                            if (photoFilePath?.startsWith("/raw")!!) {
//                                photoFilePath = photoFilePath?.replaceFirst("/raw", "")
//                            }
                            var imageStream = context!!.contentResolver.openInputStream(imageUri!!)

                            val out: Bitmap = BitmapFactory.decodeStream(imageStream)
                            imageStream?.close()
                            imageStream = context!!.contentResolver.openInputStream(imageUri!!)
                            val matrix = Matrix()
                            matrix.postRotate(getExifAngle(imageStream!!))
                            mbitmap = Bitmap.createBitmap(out, 0, 0, out.width, out.height, matrix, true)
                            Log.d("selectedImage", out.toString())
                            imView.setImageBitmap(out)

                        } catch (e: FileNotFoundException) {
                            Log.d("clickOpenPicture", e.toString())
                        }
                }
                else if (resultCode == RESULT_CANCELED) {
                    Log.d("CAMERA_REQUEST_CODE", "Canceled")
                }
            }
        }
    }

    private fun requestPermissionsCamera() {
        val permissionCamera = ContextCompat.checkSelfPermission(context!!,
            android.Manifest.permission.CAMERA)

        if (permissionCamera != PackageManager.PERMISSION_GRANTED){
            requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE), MY_CAMERA_PERMISSION_REQUEST)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissionsOpenFile() {
        val permissionWrite = ContextCompat.checkSelfPermission(context!!,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionMedia = ContextCompat.checkSelfPermission(context!!,
            android.Manifest.permission.ACCESS_MEDIA_LOCATION)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED ||
                permissionMedia != PackageManager.PERMISSION_GRANTED){
            requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_MEDIA_LOCATION), WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST)
        }
    }

    private fun requestPermissionsWriteFile() {
        val permissionWrite = ContextCompat.checkSelfPermission(context!!,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED){
            requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE), WRITE_READ_FILE_PERMISSION_REQUEST)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val file: File
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("TimeStamp is ", timeStamp)
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("mkdir dir is ", storageDir!!.exists().toString())

        if (!storageDir.exists() && storageDir.isDirectory) {
            try {
                storageDir.mkdir()
            } catch (ex: SecurityException) {
                val errorMessage =
                    "Не удалось создать папку и сохранить файл!"
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults.lastIndex+1 >= 2){
            Toast.makeText(activity, grantResults.toString(),
                Toast.LENGTH_SHORT).show()
            when(requestCode) {
                MY_CAMERA_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED
                    )
                        clickTakePicture()
                }

                WRITE_READ_FILE_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        clickSavePicture()
                }

                WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        clickOpenPicture()
                }
            }
        }
    }

    private fun clickOpenPicture(){
        requestPermissionsWriteFile()
        val intentOpenPicture = Intent(Intent.ACTION_PICK)
        intentOpenPicture.type = "image/*"
        startActivityForResult(intentOpenPicture, OPEN_PICTURE_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(this.requireActivity()).toBundle())
    }

    private fun clickTakePicture(){

        requestPermissionsCamera()
        val file = createImageFile()
        outputFile = FileProvider.getUriForFile(context!!, context!!.applicationContext.packageName + ".provider", file)
        photoFilePath = file.path
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile)
        startActivityForResult(intent, CAMERA_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(this.requireActivity()).toBundle())
    }

    private fun clickSavePicture(){
        //val photoFile: File?
        try {
            photoFile = createImageFile()
            val out = FileOutputStream(photoFile)
            mbitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, out)
            out.flush()
            out.close()
            MediaStore.Images.Media.insertImage(activity?.contentResolver, photoFile!!.absolutePath, photoFile!!.name, photoFile!!.name)
            MediaScannerConnection.scanFile(activity, arrayOf(photoFile.toString()), null) {
                path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
            Toast.makeText(activity, "Сохранил " + photoFile!!.path.toString(),
                Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(activity, "Ошибка при сохранении. Повторите попытку.",
                Toast.LENGTH_SHORT).show()
            Log.d("MyIlnarLog2", e.toString())
        }
    }

    override fun showPicture() {
        if (mbitmap != null) {
            imView.setImageBitmap(mbitmap)
        }
    }
}