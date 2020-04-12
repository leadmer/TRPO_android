package etu.vt.trpo_android.ui.Fragment

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import etu.vt.trpo_android.R
import etu.vt.trpo_android.present.presenter.PicturePresenter
import etu.vt.trpo_android.present.view.PictureView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PictureFragment: MvpAppCompatFragment(), PictureView {

    private val CAMERA_REQUEST_CODE = 1
    private val REQUEST_CAPTURE_IMAGE = 2
    private lateinit var imView: ImageView
    private lateinit var currentPhotoPath: String
    private var MY_CAMERA_PERMISSION_REQUEST = 100
    private val WRITE_FILE_PERMISSION_REQUEST = 101
    private val READ_FILE_PERMISSION_REQUEST = 102
    private val WRITE_READ_FILE_PERMISSION_REQUEST = 102
    private var bitmap : Bitmap? = null

    @InjectPresenter
    lateinit var mPicturePresenter: PicturePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPicturePresenter.onShowPicture()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.picture_fragment, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imView = view.findViewById(R.id.im_view)
        val pictureButton = view.findViewById<Button>(R.id.take_picture)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                createImageFile()
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
//        button?.setOnClickListener {
//            findNavController().navigate(R.id.greetingsFragment2, null)
//        }
    }

    override fun showPicture() {
        if (bitmap != null){
            imView.setImageBitmap(bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> if (resultCode == RESULT_OK){
                if (data != null && data.hasExtra("data")){
                    Log.d("bitmap", data.getStringExtra("data").toString())
                    bitmap = data.extras?.get("data") as Bitmap
                    val bitmap = data.extras?.get("data") as Bitmap
                    imView.setImageBitmap(bitmap)
                }
            }

//            REQUEST_CAPTURE_IMAGE -> if (resultCode == RESULT_OK){
//                if (data != null && data.hasExtra("data")){
//                    bitmap = data.extras?.get("data") as Bitmap
//                    val bitmap = data.extras?.get("data") as Bitmap
//                    imView.setImageBitmap(bitmap)
//                }
//            }
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

    private fun requestPermissionsWriteFile() {
        val permissionWrite = ContextCompat.checkSelfPermission(context!!,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val permissionRead = ContextCompat.checkSelfPermission(context!!,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED){
            requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE), WRITE_READ_FILE_PERMISSION_REQUEST)
        }

//        if (permissionRead != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(requireActivity(),
//                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_FILE_PERMISSION_REQUEST)
//        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("TimeStamp is ", timeStamp)
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("mkdir dir is ", storageDir!!.exists().toString())

        if (!storageDir.exists()){
            try {
                storageDir.mkdir()
            }catch (ex : SecurityException){
                val errorMessage =
                    "Не удалось создать папку и сохранить файл!"
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("Storage dir is ", storageDir.toString())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults.lastIndex+1 >= 2)
        when(requestCode) {
            MY_CAMERA_PERMISSION_REQUEST-> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED)
                    clickTakePicture()
                }
//            WRITE_READ_FILE_PERMISSION_REQUEST-> {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
//                    createImageFile()
//            }
        }
    }

    private fun clickTakePicture(){

        requestPermissionsCamera()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(context!!, "etu.vt.trpo_android.provider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

//        if (photoFile != null) {
//            val  photoURI = FileProvider.getUriForFile(context!!, "etu.vt.trpo_android", photoFile)
//            val pictureIntent = Intent(MediaStore.EXTRA_OUTPUT, photoURI)
//            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
//        }
    }

}