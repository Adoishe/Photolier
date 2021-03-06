package com.adoishe.photolier

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.kobjects.base64.Base64
import java.util.*
import kotlin.collections.ArrayList

class Order {

    private             val uuid            : String                    = UUID.randomUUID().toString()
                        var name            : String                    = UUID.randomUUID().toString()
                        var imageUriList    : MutableList<Uri>          = ArrayList()
    private             var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                        var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                        val auth                                        = FirebaseAuth.getInstance()
                        var context         : Activity
                        var result                                      = String()
                        var orderStatus     : String                    = ""
                        var orderSendResult : String                    = ""

    constructor(context: Activity) {

        this.context = context

    }

    fun getUuid(): String {

        return uuid
    }

    fun getByteArrayList() : MutableList<ByteArray>{

        imageOrderList.forEach(){

            var byteArray = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            byteArrayList.add(byteArray!!)
        }

        return byteArrayList
    }

    fun getCvArrayList() : MutableList<ContentValues>{

        var cvArrayList    : MutableList<ContentValues>  = ArrayList()

        imageOrderList.forEach(){

            var cv          = ContentValues()
            var byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name", it.name)
            cv.put("byteArray", Base64.encode(byteArray!!))

            cvArrayList.add(cv)

        }

        return cvArrayList
    }

    fun getJSONArrayList() : JSONArray{

      //  var cvArrayList    : MutableList<JSONObject>  = ArrayList()
        var cvArrayList    =  JSONArray()

        imageOrderList.forEach(){

            var cv          = JSONObject()
            var byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name", it.name)
            cv.put("byteArray", Base64.encode(byteArray!!))

            var result = JSONObject()

            result.put("mValues", cv)

            cvArrayList.put(result)

        }

        return cvArrayList
    }

    private fun getCvForWs() : ContentValues{

        var cv = ContentValues()

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

    private fun getJSONForWs() : JSONObject{

        var json = JSONObject()
        var result = JSONObject()

        json.put("orderUid"     , this.uuid)
        json.put("displayName"  , (context as MainActivity).auth.currentUser?.displayName.toString())
        json.put("email"        , (context as MainActivity).auth.currentUser?.email.toString())
        json.put("phoneNumber"  , (context as MainActivity).auth.currentUser?.phoneNumber.toString())
        json.put("uid"          , (context as MainActivity).auth.currentUser?.uid.toString())

        result.put("mValues", json)

        return result
    }

    fun send (){

        Thread {

            (context as MainActivity).log.add("send thread create")

            byteArrayList               = getByteArrayList()
            var cv                      = getCvForWs()
            var jsonObject              = getJSONForWs()
            val dl                      = DataLoader()
            val outputJson              = jsonObject.toString()

            (context as MainActivity).log.add("json to send = $outputJson")

            var cvArrayList             = getJSONArrayList()
            val jsoCvArrayList: String  = cvArrayList.toString()
            var sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

            (context as MainActivity).log.add("answer json = $sendResult")

            try {

                var resultJSSONObj = JSONObject(sendResult)

                result = resultJSSONObj.toString()

                (context as MainActivity).log.add("result = $result")

                orderSendResult = resultJSSONObj.getJSONObject("mValues").getString("orderName")

            }
            catch (e: Exception) {

                // тууут ошибка загрузки заказа
                result     = sendResult

                (context as MainActivity).log.add("result = $result")
            }
        }.start()

    }
}