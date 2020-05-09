package etu.vt.trpo_android.ui.Fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import etu.vt.trpo_android.R
import etu.vt.trpo_android.present.presenter.PicturePresenter
import etu.vt.trpo_android.present.view.PictureView
import etu.vt.trpo_android.repository.PictureRepository
import etu.vt.trpo_android.repository.PictureRepositoryProvider
import etu.vt.trpo_android.ui.Fragment.PictureFragment.SingleBitmap.mbitmap
import etu.vt.trpo_android.util.PermissionManager


class PictureFragment: MvpAppCompatFragment(), PictureView {

    private lateinit var imView: ImageView
    val pictureRepository = PictureRepositoryProvider.providePictureRepository()

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
        val view = inflater.inflate(R.layout.picture_fragment, container, false)
        imView = view.findViewById(R.id.im_view)

        val pictureButton = view.findViewById<Button>(R.id.take_picture)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val openButton = view.findViewById<Button>(R.id.open_button)
        val sendButton = view.findViewById<Button>(R.id.send_button)

        sendButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                val request = mPicturePresenter.prepareImageRequest(mbitmap,this)
                mPicturePresenter.createRequestPicture(pictureRepository, request)
            }
            else
                PermissionManager().requestPermissionsInternet(this)
        }



        openButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                mPicturePresenter.clickOpenPicture(this)
            }
            else
                PermissionManager().requestPermissionsWriteFile(this)
        }

        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                mPicturePresenter.clickSavePicture(this)
            }
            else
                PermissionManager().requestPermissionsWriteFile(this)
        }

        pictureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                mPicturePresenter.clickTakePicture(this)
            }
            else
                PermissionManager().requestPermissionsCamera(this)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPicturePresenter.onActivityResultCamera(requestCode, resultCode, data,this, imView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPicturePresenter.requestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun showPicture(bitmap: Bitmap?) {
        if (bitmap != null) {
            imView.setImageBitmap(bitmap)
        }
    }

    override fun pushToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}