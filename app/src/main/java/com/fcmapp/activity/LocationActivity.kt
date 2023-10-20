package com.fcmapp.activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fcmapp.MainViewModel
import com.fcmapp.databinding.ActivityLocationBinding
import com.fcmapp.interfaces.OperationalCallBack
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
class LocationActivity  : AppCompatActivity() {
    private lateinit var mainBinding: ActivityLocationBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var viewModel: MainViewModel
    var lat=""
    var long=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        mainBinding.btnLocation.setOnClickListener {
            getLocation()
        }
        mainBinding.uploadLocation.setOnClickListener {
            if (lat!=null&&long!=null&&lat.isNotEmpty()&&long.isNotEmpty())
            viewModel.uploadLocation(lat,long,object : OperationalCallBack {
                override fun onSuccess(message: String) {
                    Toast.makeText(this@LocationActivity,"Location uploaded successfully",Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@LocationActivity,"Failed to upload location",Toast.LENGTH_SHORT).show()

                }
            })
            else
                Toast.makeText(this,"please get the Location",Toast.LENGTH_SHORT).show()
        }
        mainBinding.backButton.setOnClickListener {
            finish()
        }

    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                       // geocoder.getAddrByWeb(getLocationInfo(locationAddress));
                        val list: MutableList<Address>? = geocoder?.getFromLocation(location.latitude, location.longitude, 1)
                        mainBinding.apply {
                            lat=list?.get(0)?.latitude.toString()
                            long=list?.get(0)?.longitude.toString()
                            tvLatitude.text = "Latitude\n${list?.get(0)?.latitude}"
                            tvLongitude.text = "Longitude\n${list?.get(0)?.longitude}"
                            tvCountryName.text = "Country Name\n${list?.get(0)?.countryName}"
                            tvLocality.text = "Locality\n${list?.get(0)?.locality}"
                            tvAddress.text = "Address\n${list?.get(0)?.getAddressLine(0)}"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}