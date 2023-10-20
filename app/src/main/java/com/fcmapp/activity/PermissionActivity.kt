package com.fcmapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fcmapp.R
import com.fcmapp.databinding.ActivityPermissionBinding
import com.google.android.material.snackbar.Snackbar


class PermissionActivity: AppCompatActivity() {
    private lateinit var layout: View
    private lateinit var binding: ActivityPermissionBinding
     var multiplePermissionsContract: ActivityResultContracts.RequestMultiplePermissions? = null

    val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        multiplePermissionsContract = ActivityResultContracts.RequestMultiplePermissions()

        binding.button.setOnClickListener {
           // onClickRequestPermission(view)
          /*  val   multiplePermissionLauncher = registerForActivityResult<Any, Any>(multiplePermissionsContract) { isGranted: Any ->
                Log.d("PERMISSIONS", "Launcher result: $isGranted")
                if (isGranted.containsValue(false)) {
                    Log.d(
                        "PERMISSIONS",
                        "At least one of the permissions was not granted, launching again..."
                    )
                    multiplePermissionLauncher.launch(PERMISSIONS)
                }
            }
*/
           // askPermissions(multiplePermissionLauncher)
            if (!hasPermissions(PERMISSIONS)) {
                Log.d(
                    "PERMISSIONS",
                    "Launching multiple contract permission launcher for ALL required permissions"
                )
                launchMultiPermission()

            } else {
                Log.d("PERMISSIONS", "All permissions are already granted")
            }


        }

    }

    private fun launchMultiPermission() {
        requestMultiplePermissions.launch(PERMISSIONS )
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.e("Shivangi DEBUG", "${it.key} = ${it.value}")
        }
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("Permission: ", "Granted")
        } else {
            Log.i("Permission: ", "Denied")
        }
    }
    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_INDEFINITE,
                    null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


/*
     fun askPermissions(multiplePermissionLauncher: ActivityResultLauncher<Any>) {
        if (!hasPermissions(PERMISSIONS)) {
            Log.d(
                "PERMISSIONS",
                "Launching multiple contract permission launcher for ALL required permissions"
            )
            multiplePermissionLauncher.launch(PERMISSIONS)
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted")
        }
    }
*/

    private fun hasPermissions(permissions: Array<String>?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("PERMISSIONS", "Permission is not granted: $permission")
                    return false
                }
                Log.d("PERMISSIONS", "Permission already granted: $permission")
            }
            return true
        }
        return false
    }

    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }
}