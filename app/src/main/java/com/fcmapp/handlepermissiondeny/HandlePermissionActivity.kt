package com.fcmapp.handlepermissiondeny

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.fcmapp.R
import com.fcmapp.databinding.ActivityPermisionBinding

class HandlePermissionActivity : AppCompatActivity() {

    val PERMISSION_ID = 42
lateinit var  binding:ActivityPermisionBinding
lateinit var layout:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.activity_permision)
        layout = binding.constraintLayout

        setContentView(R.layout.activity_permision)

        binding.requestBtn.setOnClickListener({
            if (!checkPermissionsGranted())
                requestPermissions();
        })

        binding.visitAppSettingsBtn.setOnClickListener({
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        })

    }

    override fun onResume() {
        super.onResume()
        if (checkPermissionsGranted()) {
            binding.visitAppSettingsBtn.visibility = View.GONE
            binding.requestBtn.visibility = View.GONE
            binding.label.setText("Permission Granted")
        } else {
            binding.label.setText("Permission Denied")
            checkUserRequestedDontAskAgain()
        }

    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun checkPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //when granted
                binding.label.setText("Permission Granted:")
                showToast("Permission Granted:")
            } else {
                //Permission Denied
                binding.label.setText("Permission Denied:")
                showToast("Permission Denied:")

                checkUserRequestedDontAskAgain()
            }
        }
    }

    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFalgCOARSE =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            val rationalFalgFINE =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            if (!rationalFalgCOARSE && !rationalFalgFINE) {
                binding.label.setText("permission_denied_forcefully")
                binding.visitAppSettingsBtn.visibility = View.VISIBLE
                binding.requestBtn.visibility = View.GONE
                showToast("permission_denied_forcefully")
            }
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this@HandlePermissionActivity, msg, Toast.LENGTH_LONG).show()
    }

}