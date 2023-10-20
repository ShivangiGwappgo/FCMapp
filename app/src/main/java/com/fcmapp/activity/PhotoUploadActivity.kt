package com.fcmapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mvpwithvolley.presentationlayer.registration.RegistrationMVP
import com.example.mvpwithvolley.presentationlayer.registration.RegistrationPresenter
import com.fcmapp.MainViewModel
import com.fcmapp.MyRequest
import com.fcmapp.R
import com.fcmapp.databinding.ActivityMainBinding
import com.fcmapp.interfaces.OperationalCallBack
import com.fcmapp.volley.VolleyHandler
import com.fcmapp.utility.Constant
import com.fcmapp.utility.FilePath
import com.fcmapp.utility.FileUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


open class PhotoUploadActivity : AppCompatActivity(), RegistrationMVP.RegistrationView  {

    private var mCurrentPhotoPath: String? = ""
    private lateinit var presenter: RegistrationPresenter
    val RESULT_LOAD = 156
    var userId:String?=""
    var isCamraOrGallery=0
    var  base64img=""
    lateinit var photoUri:Uri
    private lateinit var viewModel: MainViewModel
    private lateinit var volleyHandler: VolleyHandler
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
         volleyHandler = VolleyHandler(this)
        presenter = RegistrationPresenter(volleyHandler, this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainBinding.btnUploadpdf.setOnClickListener {
            userId=mainBinding.userId?.text?.toString()
            selectPdf()
        }
        mainBinding.selectPhoto.setOnClickListener {
            userId=mainBinding.userId?.text?.toString()
            selectImage(this)
        }
        mainBinding.backButton.setOnClickListener {
          finish()
        }
        mainBinding.btnRetrofit.setOnClickListener {
            userId=mainBinding.userId?.text?.toString()
           if (userId!=null&&userId!!.isNotEmpty()){
               if (isCamraOrGallery==1&&base64img.isNotEmpty()) {
                   mainBinding.progressTxt.text="Uploading Base64 image..."
                   progressVisibility(true,"")
                   viewModel.uploadBase64File(base64img, userId,object : OperationalCallBack {
                       override fun onSuccess(message: String) {
                           progressVisibility(false,"Image Uploaded successfully")
                       }

                       override fun onFailure(message: String)  {
                           progressVisibility(false,"Failed to upload Image")
                       }
                   })
               }
               else if (isCamraOrGallery==2&&photoUri!=null)
               {
                   mainBinding.progressTxt.text="Uploading in Multipart..."
                   progressVisibility(true,"")
                   uploadImage(photoUri)
               }
               else{
                   Toast.makeText(this,"Please select Image",Toast.LENGTH_SHORT).show()
               }
           }
            else{
               Toast.makeText(this,"Please enter User Id",Toast.LENGTH_SHORT).show()
           }

        }
        mainBinding.btnVolley.setOnClickListener {
            userId=mainBinding.userId?.text?.toString()
            if (userId!=null&&userId!!.isNotEmpty()) {
                if (isCamraOrGallery == 1 && base64img.isNotEmpty()) {
                    mainBinding.progressTxt.text = "Uploading Base64 image..."
                    progressVisibility(true, "Failed to upload Image")
                    presenter.base64Upload(
                        MyRequest(
                            "" + userId,
                            "shivangi.jpg",
                            base64img
                        )
                    )

                } else if (isCamraOrGallery == 2 && photoUri != null) {
                    userId?.let {
                        mainBinding.progressTxt.text = "Uploading in Multipart..."
                        progressVisibility(true, "Failed to upload Image")
                        multipartVolley(it, photoUri)
                    }
                } else {
                    Toast.makeText(this, "Please select Image", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Please enter User Id",Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun callIntent(caseId: Int) {
        when (caseId) {
            Constant.INTENT_CAMERA -> try {
                dispatchTakePictureIntent()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Constant.RESULT_LOAD -> {
                val photoPickerIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(photoPickerIntent, Constant.RESULT_LOAD)
            }

            Constant.REQUEST_CAMERA -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    Constant.MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
            Constant.MY_PERMISSIONS_REQUEST_EXTERNAL -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES),
                    Constant.MY_PERMISSIONS_REQUEST_EXTERNAL
                )
            }
        }
    }
/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                 }
            else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "You need to allow access permissions", Toast.LENGTH_SHORT).show()

*/
/*
                      showMessageOKCancel("You need to allow access permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission()
                                }
                            })
*//*

                    }
                }
            }
        }
    }
*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode ==  Constant.MY_PERMISSIONS_REQUEST_EXTERNAL||requestCode ==   Constant.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices) {

                    if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "READ_EXTERNAL_STORAGE granted")
                        }
                    }
                    else if (permissions[i] == Manifest.permission.READ_MEDIA_IMAGES) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "READ_MEDIA_IMAGES granted")
                            //callIntent(RESULT_LOAD)
                        }
                    }
                    else if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "WRITE_EXTERNAL_STORAGE granted")
                        }
                    }
                    else if (permissions[i] == Manifest.permission.CAMERA) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "CAMERA granted")
                            callIntent(Constant.INTENT_CAMERA)
                        }

                    }
                }
            }
        }
    }

    private fun selectImage(context:Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context, R.style.MyDialogTheme)
        builder.setTitle("Choose your picture")
        builder.setItems(options, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, item: Int) {
                if (options[item] == "Take Photo") {
                    isCamraOrGallery=1
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constant.REQUEST_CAMERA)
                    } else {
                        callIntent(Constant.INTENT_CAMERA)
                    }
                }
                else if (options[item] == "Choose from Gallery") {
                    isCamraOrGallery=2

                    if (Build.VERSION.SDK_INT >= 23) {

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                        )
                        {
                            callIntent(Constant.MY_PERMISSIONS_REQUEST_EXTERNAL)
                        }
                        else {
                            callIntent(RESULT_LOAD)
                        }
                    } else {
                        callIntent(RESULT_LOAD)
                    }
                } else if (options[item] == "Cancel") {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createBase64file(uri:Uri):String {
        var sImage:String=""
        try {
            mainBinding.imgViewer.setImageURI(uri)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 2000, 2000, false);
            val stream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
            val bytes = stream.toByteArray()
             sImage=Base64.encodeToString(bytes, Base64.DEFAULT)
            Log.d("base64 image.. ","..$sImage")

        }
        catch (e: IOException) {
          Log.d("base64 exception ","..${e.message}")
        }
        return sImage
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile1()
            } catch (ex: IOException) { }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, "com.fcmapp.provider", photoFile)
        //      val photoURI= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, Constant.REQUEST_CAMERA)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != 0) {
            when (requestCode) {
                Constant.PDF_LOAD->{
                    val uri = data?.getData()
                      Toast.makeText(this, "photo uri ${uri.toString()}", Toast.LENGTH_SHORT).show()
                    if (uri != null) {
                        uploadPDfMultipart(uri)
                    }
                }
                Constant.RESULT_LOAD -> {
                    val temPhoto = data?.data
                    if (temPhoto != null) {
                       mainBinding.imgViewer.setImageURI(temPhoto)
                        photoUri=temPhoto
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                Constant.REQUEST_CAMERA -> {
                    val photoURI = Uri.fromFile(File(mCurrentPhotoPath))
                    if (photoURI != null) {
                        runBlocking {
                            val accessTokenDeferred = async {
                                createBase64file(photoURI)
                             //   asyncTask(photoURI)
                            }
                            delay(500)
                            base64img= accessTokenDeferred.await()
                         Log.d("base64 image asyn..","base64 image..$base64img")
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun multipartVolley(userId:String,uri:Uri){
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bytes = stream.toByteArray()

         volleyHandler.saveMultipartImage(userId,"shivangi12.jpg",bytes,object : OperationalCallBack {
             override fun onSuccess(message: String) {
                 progressVisibility(false,"Image uploaded successfully")
             }

             override fun onFailure(message: String) {
                 progressVisibility(false,"Failed to upload Image")

             }
         })

    }
    fun selectPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), Constant.PDF_LOAD)
    }
    @Throws(IOException::class)
    private fun createImageFile1(): File {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        return image
    }
    private fun uploadImage(selectedImageUri:Uri) {
        val file1 = FileUtils.getFile(this, selectedImageUri)
        val requestFile1: RequestBody = RequestBody.create(
            MediaType.parse(contentResolver.getType(selectedImageUri)), file1)
        val body1 = MultipartBody.Part.createFormData("file", file1.getName(), requestFile1)
        file1.exists()
        file1.canRead()
       viewModel.uploadMultipartFile(body1,userId,"shivangi.jpg",
           object : OperationalCallBack {
           override fun onSuccess(message: String) {
               progressVisibility(false," Image uploaded successfully")
           }

           override fun onFailure(message: String)  {
               progressVisibility(false," Failed to upload image")

           }
       })
    }

    fun progressVisibility(isVisible:Boolean,message:String){
        if (isVisible)
        {
            mainBinding.progressBar.visibility=View.VISIBLE
            mainBinding.progressTxt.visibility=View.VISIBLE

        }
        else{
            mainBinding.progressBar.visibility=View.GONE
            mainBinding.progressTxt.visibility=View.GONE
            Toast.makeText(this@PhotoUploadActivity,message,Toast.LENGTH_SHORT).show()

        }

    }

    open fun uploadPDfMultipart(filePath:Uri) {
       val path = FilePath.getPath(this, filePath)
       // val file1 = FileUtils.getFile(this, filePath)
      // val path=  file1.path
        if (path == null) {
            Toast.makeText(
                this,
                "Please move your .pdf file to internal storage and retry",
                Toast.LENGTH_LONG
            ).show()
        } else {
            //Uploading code
            try {
            //   val uploadId = UUID.randomUUID().toString()
                if (path != null && FileUtils.isLocal(path)) {
                    var file1= File(path)
                    val requestFile1: RequestBody = RequestBody.create(
                        MediaType.parse(contentResolver.getType(filePath)),
                        file1
                    )
                    val body1 = MultipartBody.Part.createFormData("file", file1.getName(), requestFile1)
                    mainBinding.progressTxt.text="Uploading PDF file...."
                    progressVisibility(true,"")

                    viewModel.uploadMultipartFile(body1,userId,"Shivangi1.pdf",object : OperationalCallBack {
                        override fun onSuccess(message: String) {
                            progressVisibility(false,"Image uploaded successfully")
                        }

                        override fun onFailure(message: String) {
                            progressVisibility(false,"Failed to upload Image")
                        }
                    })
                }
            } catch (exc: java.lang.Exception) {
                Toast.makeText(this, exc.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLoad(isLoading: Boolean) {
        mainBinding.apply {
            if (isLoading) {
                mainBinding.progressTxt.text="Uploading Base64 image..."
                progressVisibility(true,"")
            } else {
                mainBinding.progressBar.visibility = View.GONE
                mainBinding.progressTxt.visibility=View.GONE
               // progressVisibility(false,"Failed to upload Image")
            }
        }
    }

    override fun setResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        mainBinding.apply {
            mainBinding.userId.text!!.clear()
        }
    }
    ///
    /*fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            false
        } else true
    }*/
   /* fun requestPermission() {
        requestPermissions(
            this, arrayOf<String>(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }*/
    /* private fun createImageFile(): File? {
         val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
         val imageFileName = "JPEG_" + timeStamp + "_"
         val storageDir = externalCacheDir
         val image = File.createTempFile(
             imageFileName, *//* prefix *//*
            ".jpg", *//* suffix *//*
            storageDir      *//* directory *//*
            )
        return image
    }*/
    /*  @RequiresApi(Build.VERSION_CODES.O)
   suspend  fun asyncTask(uri:Uri)
    {
        val valueDeferred1 = GlobalScope.async (Dispatchers.IO){ createBase64file(uri) }
       // val valueDeferred2 = GlobalScope.async (Dispatchers.IO){ getRandom() }
        //val resultText = resultOne.await() + resultTwo.await()
        Log.i("Async", valueDeferred1.await().toString())
    }*/
    /*fun capturePhoto(){
        lateinit var tmpUri: Uri
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.message?.let { Log.d("Log", it) }
            }
            if (photoFile != null) {
                tmpUri = FileProvider.getUriForFile(this, "com.fcmapp.provider", getTemporalFile(this))
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri)
                startActivityForResult(takePictureIntent, Constant.REQUEST_CAMERA)
            }
        }
    }*/
    /*fun getTemporalFile(baseActivity: Context): File {
        val tempFile = File(baseActivity.getExternalCacheDir(), "tmp.jpg")
        return tempFile
    }*/
    /*
        private fun uploadMultipartImage(selectedImageUri:Uri) {

            if (selectedImageUri == null) {
                Toast.makeText(this,"Select an Image First",Toast.LENGTH_SHORT).show()
                return
            }

            val parcelFileDescriptor = contentResolver.openFileDescriptor(
                selectedImageUri!!, "r", null
            ) ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir,contentResolver.getFileName(selectedImageUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            progressBar.progress = 0
            val body = UploadRequestBody(file,"image")
            val body1 = MultipartBody.Part.createFormData("image", file.name, body)
              viewModel.uploadMultipartFile(body1)


        }
    */
    /*private fun ContentResolver.getFileName(selectedImageUri: Uri): String {
        var name = ""
        val returnCursor = this.query(selectedImageUri,null,null,null,null)
        if (returnCursor!=null){
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }

        return name
    }*/

}

