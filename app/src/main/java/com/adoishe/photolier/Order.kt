package com.adoishe.photolier

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.navigation.findNavController
import org.json.JSONArray
import org.json.JSONObject
import org.kobjects.base64.Base64
import java.util.*
import kotlin.collections.ArrayList

class Order(var context: Activity) {
    private     var uuid            : String                    = UUID.randomUUID().toString()
                var session         : String                    = ""
                var name            : String                    = ""
                var text            : String                    = ""
                var imageFormat     : ImageFormat?              = null
                var materialPhoto   : MaterialPhoto?            = null
                var imageUriList    : MutableList<Uri>          = ArrayList()
    private     var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                var result                                      = String()
                var orderStatus     : String                    = ""
                var orderSendResult : String                    = ""
                var indexInPacket   : Int                       = 0
                var countOfPacket   : Int                       = 0
                var status          : Int                       = Order.NEW


    init {
        this.name       = "blank"
        this.session    = (context as MainActivity ).session
    }

    fun getUuid(): String {

        return uuid

    }

    fun setUuid(uuid: String) {

        this.uuid = uuid
    }

    private fun getByteArrayList() : MutableList<ByteArray>{

        imageOrderList.forEach(){

            val byteArray = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            byteArrayList.add(byteArray)
        }

        return byteArrayList
    }
/*
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

    private fun getJSONArrayList() : JSONArray{

      //  var cvArrayList    : MutableList<JSONObject>  = ArrayList()
        val cvArrayList    =  JSONArray()

        imageOrderList.forEach(){

            val cv          = JSONObject()
            val byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name"           , it.name)
            cv.put("materialPhoto"  , it.materialPhoto!!.uid)
            cv.put("imageFormat"    , it.imageFormat!!.uid)
            cv.put("qty"            , it.qty)
            cv.put("byteArray"      , Base64.encode(byteArray))
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
        val mainAct = context as MainActivity

        json.put("orderUid"         , this.uuid)
        //json.put("imageFormat"      , this.imageFormat?.uid)
        //json.put("materialPhoto"    , this.materialPhoto?.uid)
        json.put("session"          , this.session)
        json.put("displayName"      , mainAct.auth.currentUser?.displayName.toString())
        json.put("email"            , mainAct.auth.currentUser?.email.toString())
        json.put("phoneNumber"      , mainAct.auth.currentUser?.phoneNumber.toString())
        json.put("uid"              , mainAct.auth.currentUser?.uid.toString())
        json.put("indexInPacket"    , this.indexInPacket)
        json.put("countOfPacket"    , this.countOfPacket)

        result.put("mValues", json)

        return result
    }

    private fun getSendThread() : Thread {
        return Thread {

            val mainAct = context as MainActivity

            mainAct.log.add("send thread create")

            byteArrayList               = getByteArrayList()
            val jsonObject              = getJSONForWs()
            val dl                      = DataLoader()
            val outputJson              = jsonObject.toString()

            mainAct.log.add("json to send = $outputJson")

            val cvArrayList             = getJSONArrayList()
            val jsoCvArrayList: String  = cvArrayList.toString()
            val sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

            mainAct.log.add("answer json = $sendResult")

            try {

                val resultJSSONObj  = JSONObject(sendResult)
                val mValues         = resultJSSONObj.getJSONObject("mValues")
                result              = resultJSSONObj.toString()

                mainAct.log.add("result = $result")

                name            = mValues.getString("orderName")
                orderStatus     = mValues.getString("orderStatus")
                uuid            = mValues.getString("orderUuid")
                orderSendResult = name

                status = SENT

                        //  progressBar.progress
                }
            catch (e: Exception) {

                // тууут ошибка загрузки заказа
                result = sendResult

                status = SENDERROR

                mainAct.log.add("result = $result")
                }

            //progressBar.visibility  = ProgressBar.INVISIBLE

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

    fun send (){

        when (imageOrderList.size){
            0 -> {
                // nothing
                (context as MainActivity).log.add("nothing to send ")
            }
            else ->{

                val progressBar             = (context as MainActivity).progressBar
                    progressBar.visibility  = ProgressBar.VISIBLE

                val sendThread              = getSendThread()

                sendThread.start()
                sendThread.join()

                progressBar.visibility  = ProgressBar.INVISIBLE

                when (this.indexInPacket == this.countOfPacket ) {
                    true -> {

                        val bundle = Bundle()

                        bundle.putBoolean("sendorder"   , true)
                        bundle.putString("orderName"    , name)
                        bundle.putString("orderStatus"  , orderStatus)
                        bundle.putString("orderUuid"    , uuid)

                        (context as MainActivity).findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

                    }
                }
            }//else ->{
        }//when (imageOrderList.size)
    }//fun send (

    companion object {

        @JvmStatic
        fun sendAll(){

            updateIndices()

            ordersArray.forEach {

                it.send()


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
        var ordersArray   : MutableList<Order>  = ArrayList()
        val NEW : Int = 0 // 0 - new
        val SENT : Int = 0 // 1 - sent
        val SENDERROR : Int = 0 // 3 - senderror
    }
}