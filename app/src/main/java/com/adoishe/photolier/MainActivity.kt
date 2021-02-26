package com.adoishe.photolier



import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
//import com.theartofdev.edmodo.cropper.CropImage
import com.canhub.cropper.CropImage
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.viewmodel.RequestCodes.GOOGLE_PROVIDER
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

private val RC_SIGN_IN = 123 //the request code could be any Integer
val auth = FirebaseAuth.getInstance()!!
/*
fun showSnackbar(id : Int){
    Snackbar.make(findViewById(R.id.sign_in_container), resources.getString(id), Snackbar.LENGTH_LONG).show()
}

 */

class MainActivity : AppCompatActivity() {

    var dbSq: DatabaseHelper= DatabaseHelper(this);

    internal var output: File? = null

    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var imageView: ImageView

    private val FixBitmap: Bitmap? = null
    var thePicBitmap: Bitmap? = null
    var ImagePath = "image_path"
    var ImagePath_1: String? = null
    private val TEMP_PHOTO_FILE = "temporary_holder.jpg"
    private val REQ_CODE_PICK_IMAGE = 0
    private val SELECT_PHOTO = 100
    private val CAMERA_REQUEST = 101

    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
        //    AuthUI.IdpConfig.FacebookBuilder().build(),
        //    AuthUI.IdpConfig.TwitterBuilder().build()
    )


    var imageUriList: MutableList<Uri> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(auth.currentUser != null){ //If user is signed in
//                startActivity(Next Activity)
        }
        else {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .setAvailableProviders(providers)
               //     .setTosUrl("link to app terms and service")
                //    .setPrivacyPolicyUrl("link to app privacy policy")
                    .build(),
                    RC_SIGN_IN)
        }
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


     val REG = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$"
    private var PATTERN: Pattern = Pattern.compile(REG)
    fun CharSequence.isPhoneNumber() : Boolean = PATTERN.matcher(this).find()

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
                title = resources.getString(R.string.app_name) + ' ' + auth.currentUser!!.displayName as CharSequence
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

