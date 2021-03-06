package com.adoishe.photolier

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.kobjects.base64.Base64
import java.util.*
import kotlin.collections.ArrayList

class Order {
                        var context         : Activity
    private             var uuid            : String                    = UUID.randomUUID().toString()
                        var name            : String                    = ""
                        var imageUriList    : MutableList<Uri>          = ArrayList()
    private             var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                        var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                        val auth                                        = FirebaseAuth.getInstance()

                        var result                                      = String()
                        var orderStatus     : String                    = ""
                        var orderSendResult : String                    = ""

    constructor(context: Activity) {

        this.context = context

        this.name = "Пусто" //this.    .getString(R.string.order)

    }

    fun getUuid(): String {

        return uuid

    }

    fun setUuid(uuid: String) {

        this.uuid = uuid
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

        //this = Order(context)
        val progressBar             = (context as MainActivity)?.findViewById(R.id.progressBar) as ProgressBar

        progressBar.visibility  = ProgressBar.VISIBLE


        Thread {

            (context as MainActivity).log.add("send thread create")

            byteArrayList               = getByteArrayList()
            var cv                      = getCvForWs()
            val jsonObject              = getJSONForWs()
            val dl                      = DataLoader()
            val outputJson              = jsonObject.toString()

            (context as MainActivity).log.add("json to send = $outputJson")

            val cvArrayList             = getJSONArrayList()
            val jsoCvArrayList: String  = cvArrayList.toString()
            val sendResult              = dl.sendOrder(outputJson, jsoCvArrayList)

            (context as MainActivity).log.add("answer json = $sendResult")

            try {

                val resultJSSONObj  = JSONObject(sendResult)
                    result          = resultJSSONObj.toString()

                (context as MainActivity).log.add("result = $result")

                name            = resultJSSONObj.getJSONObject("mValues").getString("orderName")
                orderStatus     = resultJSSONObj.getJSONObject("mValues").getString("orderStatus")
                uuid            = resultJSSONObj.getJSONObject("mValues").getString("orderUuid")
                orderSendResult = name

            }
            catch (e: Exception) {

                // тууут ошибка загрузки заказа
                result = sendResult

                (context as MainActivity).log.add("result = $result")
            }

                progressBar.visibility  = ProgressBar.INVISIBLE

            val bundle = Bundle()

            bundle.putBoolean("sendorder"   , true)
            bundle.putString("orderName"    , name)
            bundle.putString("orderStatus"  , orderStatus)
            bundle.putString("orderUuid"    , uuid)

            (context as MainActivity).findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

/*            val main                    = (context as MainActivity)
            val navFragment             = main.supportFragmentManager.findFragmentById(R.id.fragment)
            val orderFragment            = main.supportFragmentManager.findFragmentById(R.id.orderFragment)

            (orderFragment as OrderFragment).fill()

            navFragment?.let { navFragment ->
                navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                    (fragment as OrderFragment).fill()
                }
            }

 */
        }.start()
    }
}