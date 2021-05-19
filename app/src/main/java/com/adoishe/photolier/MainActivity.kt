package com.adoishe.photolier



//import com.theartofdev.edmodo.cropper.CropImage



import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


private val RC_SIGN_IN = 123 //the request code could be any Integer

/*
fun showSnackbar(id : Int){
    Snackbar.make(findViewById(R.id.sign_in_container), resources.getString(id), Snackbar.LENGTH_LONG).show()
}

 */

class MainActivity : AppCompatActivity() {

                var dbSq                : DatabaseHelper    = DatabaseHelper(this);
                val auth                                    = FirebaseAuth.getInstance()
                var session             : String            = UUID.randomUUID().toString()
    internal    var output              : File?             = null
    private     val pickImage                               = 100
    private     var imageUri            : Uri?              = null
    lateinit    var imageView           : ImageView
    private     val FixBitmap           : Bitmap?           = null
                var thePicBitmap        : Bitmap?           = null
                var ImagePath                               = "image_path"
                var ImagePath_1         : String?           = null
    private     val TEMP_PHOTO_FILE                         = "temporary_holder.jpg"
    private     val REQ_CODE_PICK_IMAGE                     = 0
    private     val SELECT_PHOTO                            = 100
    private     val CAMERA_REQUEST                          = 101
                var order               : Order             = Order(this)
                val providers                               = arrayListOf(
                    //   AuthUI.IdpConfig.EmailBuilder().build(),
                    //   AuthUI.IdpConfig.PhoneBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                    //    AuthUI.IdpConfig.FacebookBuilder().build(),
                    //    AuthUI.IdpConfig.TwitterBuilder().build()
                )

     lateinit   var progressBar         : ProgressBar
                var imageUriList        : MutableList<Uri>      = ArrayList()
                var log                 : MutableList<String>   = ArrayList()

                val REG                                         = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$"
    private     var PATTERN             : Pattern               = Pattern.compile(REG)


    fun CharSequence.isPhoneNumber() : Boolean  = PATTERN.matcher(this).find()

    fun generateImageView(base64: String): ImageView {

        val imageView = ImageView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = params


        //val inStream : InputStream? = requireActivity().contentResolver.openInputStream(uri)
        //val bitmap = BitmapFactory.decodeStream(inStream)

        //imageView.setImageBitmap(bitmap)
        //imageView.setImageURI(uri)
        //imageView.setImageResource(R.drawable.ic_launcher_foreground)

            imageView.scaleType             = ImageView.ScaleType.CENTER_CROP
        val imageByteArray                   = Base64.getDecoder().decode(base64)
        val bmp                              = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            imageView.layoutParams.height   = bmp.height
            imageView.layoutParams.width    = bmp.width
            imageView.requestLayout()

        Glide
            .with(this)
            .load(imageByteArray)
            //    .apply(RequestOptions().override(30, 40))
            .into(imageView)

        return imageView
    }

    fun generateTextView(string : String) :TextView {

        val textView = TextView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.gravity          = Gravity.CENTER
        textView.layoutParams   = params
        textView.text           = string

        textView.requestLayout()

        return textView

    }


    public fun encodeImage(bm: Bitmap): String? {

        val baos = ByteArrayOutputStream()

        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val b = baos.toByteArray()

        return Base64.getEncoder().encodeToString(b)
    }

    private fun authenticate(){

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(providers)
                //     .setTosUrl("link to app terms and service")
                //    .setPrivacyPolicyUrl("link to app privacy policy")
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onStart() {
        super.onStart()

    /*
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility  = ProgressBar.VISIBLE

        if(auth.currentUser == null){ //If user is signed in
            //  startActivity(Next Activity)
            authenticate()
            //log.add(auth.currentUser!!.email.toString())
        }
        else {

            // log.add("auth.currentUser == null")
            // authenticate()
        }

        ImageFormat.sync(this )
        //    log.add("ImageFormat = ")
        MaterialPhoto.sync(this )
        //  log.add("MaterialPhoto = ")



        progressBar.visibility  = ProgressBar.INVISIBLE


 */
    }



    override fun onWindowFocusChanged(hasFocus: Boolean) {

        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {

            when (auth.currentUser) {
                null -> authenticate()
                else -> setAppTitle()
            }

            //sync()

        }
    }


    override fun onResume() {

        super.onResume()

        //Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show()

    }


    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this , Manifest.permission.MANAGE_DOCUMENTS) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.MANAGE_DOCUMENTS)) {

                showPermissionDeniedDialog()

            } else {

                /*
                ActivityCompat.requestPermissions(this
                                                    , arrayOf(Manifest.permission.MANAGE_DOCUMENTS)
                                                    , OrdersHistoryFragment.REQUEST_CODE

                 */

                requestPermissions( arrayOf(Manifest.permission.MANAGE_DOCUMENTS)
                    , OrdersHistoryFragment.REQUEST_CODE

                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            OrdersHistoryFragment.REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings"
            ) { dialogInterface, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                val uri = Uri.fromParts("package", packageName, null)

                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = uri

                startActivity(intent)
            }
            .setNegativeButton("Cancel",null)
            .show()
    }

     fun showPhotos(){

         val bottomNavigationView   = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
         /*
         val photosBottomItem       = bottomNavigationView.findViewById<View>(R.id.action_dial) as MenuView.ItemView

         photosBottomItem.setChecked(true)
         photosBottomItem.setEnabled(true)

          */

         bottomNavigationView.menu.getItem(1).isChecked = true

        when (ImageFormat.status == ImageFormat.SYNC && MaterialPhoto.status == MaterialPhoto.SYNC) {
            true    -> findNavController(R.id.fragment).navigate(R.id.photosFragment)
            false   -> Toast.makeText(
                this,
                resources.getString(R.string.netTrouble),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_Photolier)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bottomNavigationView    = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        progressBar                 = findViewById(R.id.progressBar)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            item.isChecked = true

            when (item.itemId) {
                R.id.action_map -> {

                    findNavController(R.id.fragment).navigate(R.id.rootFragment)

                }
                R.id.action_dial -> {

                    showPhotos()

                }
                R.id.action_mail -> {

                    //if (askForPermissions()) {

                        progressBar.visibility = ProgressBar.VISIBLE

                        findNavController(R.id.fragment).navigate(R.id.ordersHistoryFragment)

                    //}
                }
            }

            false
        }

    }

    override fun onSupportNavigateUp(): Boolean {

        if(findNavController(R.id.fragment).currentDestination!!.id == R.id.rootFragment ) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Exit Alert")
                    .setMessage("Do You Want To Exit Photolier App?")
                    .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                        //findNavController(R.id.fragment).navigateUp()finishAffinity()
                        //finishAffinity()
                        moveTaskToBack(true);
                        exitProcess(-1)

                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, whichButton ->

                    }
                    .show()
            }
            else{
                findNavController(R.id.fragment).navigateUp()
            }
        return true
    }

   // }

    private fun setAppTitle(){

        title = resources.getString(R.string.app_name) + ' ' + resources.getString(R.string.ffor) + ' ' + auth.currentUser!!.displayName as CharSequence
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            /*
                this checks if the activity result we are getting is for the sign in
                as we can have more than activity to be started in our Activity.
             */

          //  println("8089845216".isPhoneNumber())

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK){
                /*
                    Checks if the User sign in was successful
                 */
    //                startActivity(Next Activity)
                //showSnackbar(R.string.signed_in)
                if(auth.currentUser != null){ //If user is signed in

                    setAppTitle()
    //                startActivity(Next Activity)
                }


                //finish()
                //return
            }
            else {
                if(response == null){
                    //If no response from the Server
                  //  showSnackbar(R.string.sign_in_cancelled)
                    return
                }
                /*
                if(response. == ErrorCodes.NO_NETWORK){
                    //If there was a network problem the user's phone
                    showSnackbar(R.string.no_internet_connection)
                    return
                }
                if(response.errorCode == ErrorCodes.UNKNOWN_ERROR){
                    //If the error cause was unknown
                    showSnackbar(R.string.unknown_error)
                    return
                }
                */
            }
    }
   // showSnackbar(R.string.unknown_sign_in_response) //if the sign in response was unknown
}

//        imageView = findViewById(R.id.imageView)

/*
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
        val listView = ListView(this)
        val adapter = PhotoListAdapter(this, imageUriList)
        listView.adapter = adapter
        linearLayout.addView(listView)

 */

    /* val cameraIntent    = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
     val dir             = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
     val date            = Date()
     output              = File(dir, "CheckImage $date.jpeg")

     startActivityForResult(cameraIntent, 101)




     var loadButton = findViewById<Button>(R.id.buttonLoadPicture)

     loadButton.setOnClickListener{

       //  val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
         //startActivityForResult(gallery, pickImage)

         CropImage.activity(imageUri).start(this);

     }

     */


/*        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {

                val resultUri = result.uri
              //  imageView.setImageURI(resultUri)
                imageUriList.add(resultUri)

                val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
                val listView = ListView(this)
                val adapter = PhotoListAdapter(this, imageUriList)

                listView.adapter = adapter
              //  linearLayout.addView(listView)


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }


    }*/



/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        if (resultCode === RESULT_OK) {

                    //pick image from gallery(sd card)
                    if (requestCode === SELECT_PHOTO) {
                        val selectedImage = imageReturnedIntent!!.data
                        var imageStream: InputStream? = null
                        try {
                            imageStream = contentResolver.openInputStream(selectedImage!!)
                        } catch (e: FileNotFoundException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                        val yourSelectedImage = BitmapFactory.decodeStream(imageStream)
                        imageView.setImageBitmap(yourSelectedImage)
                    } else if (requestCode === CAMERA_REQUEST) {
                        val photo = imageReturnedIntent!!.extras!!["data"] as Bitmap?
                        imageView.setImageBitmap(photo)
                    }
                }
            }


 */








/*
    public override fun onActivityResult(requestcode: Int, resultcode: Int, intent: Intent?) {
        super.onActivityResult(requestcode, resultcode, intent)
       /*
        if (resultcode == Activity.RESULT_OK) {
            if (requestcode == 101) {

                Log.d("check",dbSq.insertData(output!!.absolutePath).toString().plus(" "));

            }

        */


            if (resultcode == RESULT_OK && requestcode == pickImage) {
                imageUri = intent?.data
                imageView.setImageURI(imageUri)


                // Get image path from media store
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                val cursor: Cursor? = this.contentResolver.query(imageUri!!, filePathColumn, null, null, null)

                if (cursor == null || !cursor.moveToFirst()) {
                    // (code to show error message goes here)
                    return
                }

                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val imagePath: String = cursor.getString(columnIndex)

                cursor.close()

                if (imagePath != null) {
                    Log.d("check",dbSq.insertData(imagePath).toString().plus(" "));
                }


            }
        }


    }
*/

/*
class MainActivity : AppCompatActivity() {
    var dbSq: DatabaseHelper= DatabaseHelper(this);



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 101)


    }

    public override fun onActivityResult(requestcode: Int, resultcode: Int, intent: Intent) {
        super.onActivityResult(requestcode, resultcode, intent)
        if (resultcode == Activity.RESULT_OK) {
            if (requestcode == 101) {


                val photo = intent.extras!!.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

                Log.d("check",dbSq.insertData(byteArray).toString().plus(" "));

            }
        }
    }


 */

/*
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
 */
}

