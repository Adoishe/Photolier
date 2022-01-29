package com.adoishe.photolier

import android.Manifest
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.system.exitProcess
import android.app.NotificationManager

import android.app.NotificationChannel

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo

import android.os.Build
import android.util.Log
import androidx.annotation.UiThread
import com.google.android.gms.common.util.UidVerifier
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat


private val RC_SIGN_IN = 123 //the request code could be any Integer

/*
fun showSnackbar(id : Int){
    Snackbar.make(findViewById(R.id.sign_in_container), resources.getString(id), Snackbar.LENGTH_LONG).show()
}

 */

class MainActivity : AppCompatActivity() {


                var dbSq                : DatabaseHelper    = DatabaseHelper(this);
    lateinit    var auth                : FirebaseAuth                    //= FirebaseAuth.getInstance()
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
    lateinit    var order               : Order             //= Order(this)
                val providers                               = arrayListOf(
                    //   AuthUI.IdpConfig.EmailBuilder().build(),
                    //   AuthUI.IdpConfig.PhoneBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                    //    AuthUI.IdpConfig.FacebookBuilder().build(),
                    //    AuthUI.IdpConfig.TwitterBuilder().build()
                )

     lateinit   var progressBar         : ProgressBar
     lateinit   var progressBarPiece    : ProgressBar
                var imageUriList        : MutableList<Uri>      = ArrayList()
                var log                 : MutableList<String>   = ArrayList()

                val REG                                         = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}\$"
    private     var PATTERN                 : Pattern           = Pattern.compile(REG)
                var availableImageFormats   : MutableList<Any>  = ArrayList()

                var syncSuccessful           :Boolean            = false

    companion object {
                val FIREINSTANCE    = "https://photolier-ru-default-rtdb.europe-west1.firebasedatabase.app/"
        const   val CHANNEL_ID      = "photolier.app.CHANNEL_ID"
        const   val CHANNEL_NAME    = "photolier.app.Notification"
    }
    fun authIsInitialized():Boolean{

        return this::auth.isInitialized
    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {

            if (source.height >= source.width) {

                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result      = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)

                return result

            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                        return source
                    }

                val aspectRatio     = source.height.toDouble() / source.width.toDouble()
                val targetHeight    = (maxLength * aspectRatio).toInt()
                val result          = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)

                return result
            }
        } catch (e: Exception) {
            return source
        }
    }

    fun CharSequence.isPhoneNumber() : Boolean  = PATTERN.matcher(this).find()

    fun getFormatsByMaterialThread(materialUid: String): Thread{

        return Thread{
            //viewPager.currentItem = tab.position
            val dl                  = DataLoader()
            var res                 = dl.getFormatsByMaterial(materialUid)
            var resJarray           = JSONArray()

            try {

                resJarray           = JSONArray(res)

            } catch (e: Exception) {

                e.printStackTrace()

                res = e.toString()

                saveLog("getFormatsByMaterialThread = $res")
                //log.add("getFormatsByMaterialThread = $res")

                //return
            }

            availableImageFormats = ArrayList()

            //mainAct.log.add("getFormatsByMaterialThread = $res")

            val resArray : ArrayList<String> = ArrayList(resJarray.length())

            for (j in 0 until resJarray.length())
            //resArray.add(resJarray.getString(j))
                resArray.add( (JSONArray(res)[j] as JSONObject).getString("uuid"))

            // заполенние доступных форматов и цен
            ImageFormat.imageFormats.forEach{ it ->

                when (val uuidIndex = resArray.indexOf((it as  ImageFormat).uid)){
                    -1 -> {
                    }
                    else -> {
                        it.price =  BigDecimal((JSONArray(res)[uuidIndex] as JSONObject).getString("price"))

                        ///imageFormat.name = imageFormat.name + "(" + imageFormat.price + "₽)"

                        availableImageFormats.add(it)
                    }
                }
            }
        }
    }

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
        val imageByteArray                  = Base64.getDecoder().decode(base64)
        val bmp                             = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
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


    fun cvToString (cv: ContentValues) : String {

        val s                       = cv.valueSet()
        val itr     : Iterator<*>   = s.iterator()
        var result  : String        = ""

        while (itr.hasNext()) {
            val me = itr.next() as Map.Entry<*, *>
            val key = me.key.toString()
           // val value = me.value

            when ( key ) {
                "id" -> {}
                "uid" -> {}
                else -> result += me.value
            }

        }

        return result
    }

    fun createCardView(view: View, listCV : MutableList<ContentValues> , action : Int) {

        // Add an ImageView to the CardView
        val cardView_targetLayout = view.findViewById<LinearLayout>(R.id.cardView_target_layout)

        // Initialize a new CardView instance
        listCV.forEach {

            val cardView    = CardView(this)

            cardView.id     = listCV.indexOf(it)
            cardView.tag    = it.getAsString("uid")

            // Initialize a new LayoutParams instance, CardView width and height
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
            )

            // Set margins for card view
            layoutParams.setMargins(20, 20, 20, 20)
            // Set the card view layout params
            cardView.layoutParams = layoutParams
            // Set the card view corner radius
            cardView.radius = 16F
            // Set the card view content padding
            cardView.setContentPadding(25, 25, 25, 25)
            // Set the card view background color
            cardView.setCardBackgroundColor(Color.LTGRAY)
            // Set card view elevation
            cardView.cardElevation = 8F
            // Set card view maximum elevation
            cardView.maxCardElevation = 12F
            // Set a click listener for card view
            //val mainAct    = requireActivity() as MainActivity



            val textView = generateTextView( cvToString(it) )

            //Toast.makeText(context, imageFormat.name + "\n" + imageFormat.price, Toast.LENGTH_SHORT).show()

            cardView.addView(textView)


//            cardView.addView(generateImageView(order.imageUriList[0]))
            cardView.setOnClickListener {

               // val imageFormat = ImageFormat.imageFormats[it.id]
                val bundle = Bundle()

                bundle.putInt(      "id"   , it.id)
                bundle.putString(   "uid"  , it.tag as String?)


                //bundle.putString("imageFormatName", imageFormat.name)
                //bundle.putB("imageFormatPrice", imageFormat.price)
                //bundle.putBoolean("ordersHistory", true)


                view.findNavController().navigate(
                    action
                    ,bundle
                )
            }
            // Finally, add the CardView in root layout
            cardView_targetLayout?.addView(cardView)
        }
    }

    public fun encodeImage(bm: Bitmap): String? {

        val baos = ByteArrayOutputStream()

        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val b = baos.toByteArray()

        return Base64.getEncoder().encodeToString(b)
    }

    fun authenticate(){

        auth = FirebaseAuth.getInstance()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(providers)
                //     .setTosUrl("link to app terms and service")
                //    .setPrivacyPolicyUrl("link to app privacy policy")
                .build()
            , RC_SIGN_IN
        )
    }

    override fun onStart() {
        super.onStart()



//        Toast.makeText(this, "STARTED!!!!!", Toast.LENGTH_LONG).show()

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

        super.onWindowFocusChanged(hasFocus)

//        Toast.makeText(this, "onWindowFocusChanged!!!!!", Toast.LENGTH_LONG).show()

        if (hasFocus) {

            try {

//                auth = FirebaseAuth.getInstance()

                when (this::auth.isInitialized) {
                    false -> authenticate()
                    true -> setAppTitle()
                }

            }catch(e:Exception){

                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()

            }




        }
    }

    override fun onResume() {

        super.onResume()

//        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show()

    }

     fun sync() : Boolean{

        //        val mainAct =  MainActivity

        progressBar.visibility = ProgressBar.VISIBLE

        //        Toast.makeText(context, resources.getString(R.string.sync), Toast.LENGTH_LONG).show()
        saveLog(resources.getString(R.string.sync))
        ImageFormat.sync(this)
        MaterialPhoto.sync(this)

        progressBar.visibility  = ProgressBar.INVISIBLE
        syncSuccessful          = ImageFormat.status == ImageFormat.SYNC && MaterialPhoto.status == MaterialPhoto.SYNC

        when (syncSuccessful){
            true ->{
                //Toast.makeText(context, resources.getString(R.string.sync), Toast.LENGTH_LONG).show()
                //(context as MainActivity).setTheme(R.style.Theme_Photolier)
            }

            false ->{

                saveLog("SYNC failed")
            }
        }

        return syncSuccessful
    }



    fun saveLog(msg:String){

        val logs                = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("logs")
        val date                = java.util.Calendar.getInstance().time
        val formatter           = SimpleDateFormat("dd-MM-yyyy")//SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formattedDate       = formatter.format(date)
        val formatterMsg        = SimpleDateFormat("HH:mm:ss")
        val formattedDateMsg    = formatterMsg.format(date)

        log.add("$formattedDateMsg->$msg")

        var userId = ""

        try {

            userId = auth.currentUser!!.uid
        }
        catch(e : Exception) {

            userId = "auto_" + UUID.randomUUID().toString()

        }

        logs
            .child(formattedDate)
            .child(userId)
            .child(session)
            .setValue(log)
            .addOnCompleteListener {

           Log.d("FirebaseActivity", msg)
        }

//        launch(UiThread)
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

                requestPermissions(     arrayOf(Manifest.permission.MANAGE_DOCUMENTS)
                                    ,   OrdersHistoryFragment.REQUEST_CODE

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

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

//        Toast.makeText(this, "CREATED!!!!!", Toast.LENGTH_LONG).show()
// Приложение запущено впервые или восстановлено из памяти?
//        if ( savedInstanceState == null )   // приложение запущено впервые
//        {
//            saveLog("savedInstanceState == null")
//            // другой код
//        }
//        else // приложение восстановлено из памяти
//        {
//            saveLog("savedInstanceState != null")
//            // инициализация суммы счета сохранённой в памяти суммой
////            currentBillTotal = savedInstanceState.getDouble(BILL_TOTAL);
//        }

//        saveLog("setTheme(R.style.Theme_Photolier)")

//        setTheme(R.style.Theme_Photolier)

//        saveLog("super.onCreate(savedInstanceState)")
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Photolier)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        saveLog("setContentView(R.layout.activity_main)")
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val bottomNavigationView    = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        progressBar                 = findViewById(R.id.progressBar)
//        progressBarPiece            = findViewById(R.id.progressBarSendPiece)

        progressBar.visibility      = ProgressBar.INVISIBLE
//        progressBarPiece.visibility = ProgressBar.INVISIBLE

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

                    progressBar.visibility      = ProgressBar.VISIBLE
                    progressBarPiece.visibility = ProgressBar.VISIBLE

                    findNavController(R.id.fragment).navigate(R.id.ordersHistoryFragment)

                    //}
                }
            }

            false
        }


        //val intent = intent

        authenticate()
        sync()


    }

    override fun onSupportNavigateUp(): Boolean {

        var rootReached = false

        val hostFragment    = Navigation.findNavController(this,R.id.fragment)
        val navHost         = this.supportFragmentManager.findFragmentById(R.id.fragment)
        var currentFragment = navHost?.childFragmentManager?.fragments?.get(0)

        if (currentFragment is OrderFragment) {

            hostFragment.navigate(R.id.rootFragment)

            order  = Order(this)

            Order.ordersArray = ArrayList()

            return true
        }

        try {
           rootReached = (hostFragment.currentDestination!!.id == R.id.rootFragment)
        }
        catch (e: Exception) {
            hostFragment.navigateUp()
            return true
        }

        if( rootReached) {
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


            try {
                hostFragment.navigateUp()
            }
            catch (e: Exception) {
                hostFragment.navigate(R.id.rootFragment)
                return true
            }



            }
        return true
    }

   // }

    private fun setAppTitle(){

        title = resources.getString(R.string.app_name) + ' ' + resources.getString(R.string.ffor) + ' ' + auth.currentUser!!.displayName as CharSequence



        //Profile.load(auth.currentUser!!.uid)
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

