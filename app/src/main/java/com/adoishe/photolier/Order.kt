package com.adoishe.photolier

//import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread


import android.app.Activity
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import org.kobjects.base64.Base64
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class Order(var context: Activity) {
    private     var uuid            : String                    = "" //UUID.randomUUID().toString()
                var session         : String                    = ""
                var name            : String                    = ""
                var text            : String                    = ""
                var imageFormat     : ImageFormat?              = null
               // var materialPhoto   : MaterialPhoto?            = null
                var materialPhoto   : Any?            = null
                var imageUriList    : MutableList<Uri>          = ArrayList()
                var imageBase64List : MutableList<String>       = ArrayList()
    //private     var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                var result                                      = String()
                var orderStatus     : String                    = ""
                var orderSendResult : String                    = ""
                var indexInPacket   : Int                       = 0
                var countOfPacket   : Int                       = 0
                var status          : Int                       = NEW
                var payed           : Boolean                   = false
    private     val mainAct                                     = context as MainActivity
                var userId          : String                    = mainAct.auth.currentUser?.uid.toString()


    init {
        this.name       = "blanc"//(context as MainActivity ).resources.getString(R.string.netTrouble)
        this.session    = mainAct.session
        this.uuid       =  UUID.randomUUID().toString()
    }


    override fun toString() = name + "\n" + orderStatus

    fun getUuid(): String {

        return uuid

    }

    fun setUuid(uuid: String) {

        this.uuid = uuid
    }
/*
    fun fillImagesThumbsByBase64List(imageBase64List : MutableList<String>){

        this.imageBase64List = imageBase64List

        imageBase64List.forEach{

            val imageOrder = ImageOrder()

            imageOrder.setThumb(it)

            imageOrderList.add(imageOrder)
        }

    }

    private fun getByteArrayList() : MutableList<ByteArray>{

        imageOrderList.forEach(){

            val byteArray = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            byteArrayList.add(byteArray)
        }

        return byteArrayList
    }

    fun getCvArrayList() : MutableList<ContentValues>{

        val cvArrayList    : MutableList<ContentValues>  = ArrayList()

        imageOrderList.forEach(){

            val cv          = ContentValues()
            val byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name", it.name)
            cv.put("byteArray", Base64.encode(byteArray))

            cvArrayList.add(cv)

        }

        return cvArrayList
    }

 */

    private fun getJSONArrayListSingle(it : ImageOrder) : JSONArray{

        val cvArrayList    =  JSONArray()

        //imageOrderList.forEach{

            val cv          = JSONObject()
            val byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()
            val bitMap      = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
            val bos         = ByteArrayOutputStream()

            bitMap.compress(CompressFormat.JPEG, 100, bos)

            val base64String = Base64.encode(bos.toByteArray())

            val maxSize = maxOf( it.imageFormat!!.heightPix , it.imageFormat!!.widthPix)

            when (maxSize >0 )
            {

                true -> mainAct.resizeBitmap(bitMap, maxSize )
            }


            cv.put("name"           , it.name)
            cv.put("materialPhoto"  , it.materialPhoto!!.uid)
            cv.put("imageFormat"    , it.imageFormat!!.uid)
            cv.put("price"          , it.imageFormat!!.price.toString())
            cv.put("qty"            , it.qty)
            //cv.put("byteArray"      , Base64.encode(byteArray))
            cv.put("base64String"    , base64String)
            cv.put("thumbB64String" , it.imageThumbBase64)
            cv.put("imageUri"       , it.imageUri)
            cv.put("lastOne"        , it.lastOne)
            cv.put("size"           , imageOrderList.size )

            val result = JSONObject()

            result.put("mValues", cv)

            cvArrayList.put(result)
        //}

        return cvArrayList
    }

    private fun getJSONArrayList() : JSONArray{

        val cvArrayList    =  JSONArray()

        imageOrderList.forEach{

            val cv          = JSONObject()
            val byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()
            val bitMap      = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
            val bos         = ByteArrayOutputStream()

            bitMap.compress(CompressFormat.JPEG, 100, bos)

            val base64String = Base64.encode(bos.toByteArray())

            val maxSize = maxOf( it.imageFormat!!.heightPix , it.imageFormat!!.widthPix)

            when (maxSize >0 )
                {

                true -> mainAct.resizeBitmap(bitMap, maxSize )
            }


            cv.put("name"           , it.name)
            cv.put("materialPhoto"  , it.materialPhoto!!.uid)
            cv.put("imageFormat"    , it.imageFormat!!.uid)
            cv.put("price"          , it.imageFormat!!.price.toString())
            cv.put("qty"            , it.qty)
            //cv.put("byteArray"      , Base64.encode(byteArray))
            cv.put("base64String"    , base64String)
            cv.put("thumbB64String" , it.imageThumbBase64)
            cv.put("imageUri"       , it.imageUri)

            val result = JSONObject()

            result.put("mValues", cv)

            cvArrayList.put(result)
        }

        return cvArrayList
    }

    /*
    private fun getCvForWs() : ContentValues{

        val cv = ContentValues()

        (context as MainActivity).log.add("cv + orderuuid ="    + this.uuid)
        (context as MainActivity).log.add("cv + displayName = " + (context as MainActivity).auth.currentUser?.displayName)
        (context as MainActivity).log.add("cv + email = "       + (context as MainActivity).auth.currentUser?.email)
        (context as MainActivity).log.add("cv + phoneNumber = " + (context as MainActivity).auth.currentUser?.phoneNumber)
        (context as MainActivity).log.add("cv + uid = "         + (context as MainActivity).auth.currentUser?.uid)

        cv.put("orderUid"       , this.uuid)
        cv.put("displayName"    , (context as MainActivity).auth.currentUser?.displayName.toString())
        cv.put("email"          , (context as MainActivity).auth.currentUser?.email.toString())
        cv.put("phoneNumber"    , (context as MainActivity).auth.currentUser?.phoneNumber.toString())
        cv.put("uid"            , (context as MainActivity).auth.currentUser?.uid.toString())

        return cv
    }

     */

    private fun getJSONForWs() : JSONObject{

        val json    = JSONObject()
        val result  = JSONObject()


        json.put("orderUid"         , this.uuid)
        //json.put("imageFormat"      , this.imageFormat?.uid)
        //json.put("materialPhoto"    , this.materialPhoto?.uid)
        json.put("session"          , this.session)
        //json.put("uuid"             , this.uuid)
        json.put("displayName"      , mainAct.auth.currentUser?.displayName.toString())
        json.put("email"            , mainAct.auth.currentUser?.email.toString())
        json.put("phoneNumber"      , mainAct.auth.currentUser?.phoneNumber.toString())
        json.put("uid"              , userId)//mainAct.auth.currentUser?.uid.toString())
        json.put("indexInPacket"    , this.indexInPacket)
        json.put("countOfPacket"    , this.countOfPacket)

        result.put("mValues", json)

        return result
    }




    suspend fun sendPhoto(index :Int, imageOrder :ImageOrder, dl : DataLoader , outputJson : String):String {

        imageOrder.isLastOne(imageOrderList.size-1 ==  index)

        val cvArrayList             = getJSONArrayListSingle(imageOrder)
        val jsoCvArrayList: String  = cvArrayList.toString()
        val sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

        return sendResult
    }



    private fun getSendThread(fragment: OrderFragment) : Thread {
        return Thread {

            //mainAct.log.add("send thread create")
            mainAct.saveLog("send thread create")


            //byteArrayList               = getByteArrayList()
            val jsonObject              = getJSONForWs()
            val dl                      = DataLoader()
            val outputJson              = jsonObject.toString()

            //mainAct.log.add("json to send = $outputJson")
/*
            mainAct.runOnUiThread(Runnable {
                mainAct.progressBar.max = imageOrderList.size-1
                mainAct.progressBar.min = 0
                mainAct.progressBar.isIndeterminate = false

                mainAct.progressBar.visibility =  ProgressBar.VISIBLE
            })

 */
/*
            val layout = findView
            val progressBar = ProgressBar(mainAct null, android.R.attr.progressBarStyleLarge)
            val params = RelativeLayout.LayoutParams(100, 100)
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            layout.addView(progressBar, params)

 */

           // val cvArrayList             = getJSONArrayList()
            //val jsoCvArrayList: String  = cvArrayList.toString()
            //val sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

            var sendResult = ""




            mainAct.runOnUiThread{
                fragment.progressBar.visibility         = View.VISIBLE
                fragment.progressBar.isIndeterminate    = false
                fragment.progressBar.max                = imageOrderList.size-1
                fragment.progressBar.min                = 0

            }


            val lastIndex = imageOrderList.size -1
            imageOrderList.forEachIndexed  { index, imageOrder ->

                imageOrder.isLastOne(lastIndex ==  index)


                val uiInfo  = Runnable               {

                        //
                       // val toast = Toast(mainAct)

                        // mainAct.progressBar.visibility = ProgressBar.VISIBLE
                fragment.progressBar.progress = index


                      //  toast.setText(index.toString())

                      //  toast.show()
/*
                        synchronized(this)
                        {
                            this.noti;
                        }

 */

                }

             //   synchronized( uiInfo ) {
                    mainAct.runOnUiThread(uiInfo) ;

                    //uiInfo.wait() ; // unlocks myRunable while waiting
              //  }





               // mainAct.runOnUiThread(uiInfo )





/*

                val photosFragment = mainAct.supportFragmentManager.fragments[0].childFragmentManager.fragments[0] as PhotosFragment


                photosFragment.imageUriList.removeAt(index)
                photosFragment.updateList()

 */

                val cvArrayList             = getJSONArrayListSingle(imageOrder)
                val jsoCvArrayList: String  = cvArrayList.toString()
                    sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)




/*
                GlobalScope.launch(Dispatchers.Main) {
                    //val deferred              = async(Dispatchers.Default) { sendPhoto(index, imageOrder, dl , outputJson ) }
                    sendResult             = withContext(Dispatchers.Default) { sendPhoto(index, imageOrder, dl , outputJson ) }



                    val toast = Toast(mainAct)

                    mainAct.progressBar.visibility = ProgressBar.VISIBLE
                    mainAct.progressBar.progress = index
                    mainAct.progressBar.isIndeterminate = true

                    toast.setText(index.toString())

                    toast.show()

                }
                \
 */


            }
                /*
            mainAct.runOnUiThread(Runnable {
                mainAct.progressBar.isIndeterminate = true
                mainAct.progressBar.visibility = View.GONE
            })

                 */


            //mainAct.log.add("answer json = $sendResult")

            try {

                    val resultJSSONObj  = JSONObject(sendResult)
                    val mValues         = resultJSSONObj.getJSONObject("mValues")
                    result              = resultJSSONObj.toString()

                    //mainAct.log.add("result = $result")
                    mainAct.saveLog("result = $result")

                    name            = mValues.getString("orderName")
                    orderStatus     = mValues.getString("orderStatus")
                    uuid            = mValues.getString("orderUuid")
                    orderSendResult = name
                    status          = SENT

                    mainAct.runOnUiThread {
                        fragment.arguments?.putString("orderName", name)
                        fragment.arguments?.putString("orderStatus", orderStatus)
                        fragment.arguments?.putString("orderUuid", uuid)

                        fragment.fillBySend(fragment.requireView().rootView)
                        //  progressBar.progress
                    }
                }
            catch (e: Exception) {

                // тууут ошибка загрузки заказа
                result = sendResult
                status = SEND_ERROR

                //mainAct.log.add("result = $result")
                mainAct.saveLog("result = $result")
                }

           // fragment.progressBar.visibility  = ProgressBar.GONE

            /*
            when (this.indexInPacket == this.countOfPacket ){
                true -> {

                    val bundle = Bundle()

                    bundle.putBoolean("sendorder"   , true)
                    bundle.putString("orderName"    , name)
                    bundle.putString("orderStatus"  , orderStatus)
                    bundle.putString("orderUuid"    , uuid)



                    //mainAct.findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

                }
            }

             */
        }
    }

    /*
    fun save() {

        val ref = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("orders")

        ref.child(uuid).setValue(this).addOnCompleteListener {

            Log.d("FirebaseActivity", it.toString())
        }

    }

     */




    fun send (fragment: OrderFragment){

        when (imageOrderList.size){
            0 -> {
                // nothing
                //mainAct.log.add("nothing to send ")
                mainAct.saveLog("nothing to send ")
            }
            else ->{

               // val progressBar             = mainAct.progressBar
               //     progressBar.visibility  = ProgressBar.VISIBLE

                val sendThread              = getSendThread(fragment)

                sendThread.start()
                //sendThread.join()

                //progressBar.visibility  = ProgressBar.INVISIBLE


            }//else ->{
        }//when (imageOrderList.size)
    }//fun send (

    companion object {

        @JvmStatic
        fun sendAll(fragment: OrderFragment){

            updateIndices()

            ordersArray.forEach {

                it.send(fragment)

              //  when (this.indexInPacket == this.countOfPacket ){
                //    true -> {
                  //  }
            }
        }
        @JvmStatic
        fun updateIndices(){

            ordersArray.forEach { currentOrder ->

                currentOrder.indexInPacket = ordersArray.indexOf(currentOrder) + 1
                currentOrder.countOfPacket = ordersArray.size
            }
        }

        @JvmStatic
                var ordersArray : MutableList<Order>    = ArrayList()
        const   val NEW         : Int                   = 0 // 0 - new
        const   val SENT        : Int                   = 1 // 1 - sent
        const   val SEND_ERROR  : Int                   = 3 // 3 - send error
    }
}