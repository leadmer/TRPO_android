package etu.vt.trpo_android.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatFragment

class PermissionManager{

    private val MY_CAMERA_PERMISSION_REQUEST = 100
    private val WRITE_READ_FILE_PERMISSION_REQUEST = 102
    private val WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST = 103
    private val INTERNET_PERMISSION_REQUEST = 104

    fun requestPermissionsCamera(fragment: MvpAppCompatFragment) {
        val permissionCamera = ContextCompat.checkSelfPermission(fragment.requireContext(),
            Manifest.permission.CAMERA)

        if (permissionCamera != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), MY_CAMERA_PERMISSION_REQUEST
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestPermissionsOpenFile(fragment: MvpAppCompatFragment) {
        val permissionWrite = ContextCompat.checkSelfPermission(fragment.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionMedia = ContextCompat.checkSelfPermission(fragment.requireContext(),
            Manifest.permission.ACCESS_MEDIA_LOCATION)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED ||
            permissionMedia != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                ), WRITE_READ_MEDIA_FILE_PERMISSION_REQUEST
            )
        }
    }

    fun requestPermissionsWriteFile(fragment: MvpAppCompatFragment) {
        val permissionWrite = ContextCompat.checkSelfPermission(fragment.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionWrite != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), WRITE_READ_FILE_PERMISSION_REQUEST
            )
        }
    }

    fun requestPermissionsInternet(fragment: MvpAppCompatFragment) {
        val permissionInternet = ContextCompat.checkSelfPermission(fragment.requireContext(),
            Manifest.permission.INTERNET)

        if (permissionInternet != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                arrayOf(
                    Manifest.permission.INTERNET
                ), INTERNET_PERMISSION_REQUEST
            )
        }
    }
}