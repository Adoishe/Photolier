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

class Order(var context: Activity) {
    private             var uuid            : String                    = UUID.randomUUID().toString()
                        var session         : String                    = ""
                        var name            : String                    = ""
                        var imageFormat     : ImageFormat?              = null
                        var materialPhoto   : MaterialPhoto?             = null
                        var imageUriList    : MutableList<Uri>          = ArrayList()
    private             var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                        var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                        var result                                      = String()
                        var orderStatus     : String                    = ""
                        var orderSendResult : String                    = ""

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

    private fun getJSONArrayList() : JSONArray{

      //  var cvArrayList    : MutableList<JSONObject>  = ArrayList()
        var cvArrayList    =  JSONArray()

        imageOrderList.forEach(){

            var cv          = JSONObject()
            var byteArray   = context.contentResolver.openInputStream(it.imageUri!!)!!.readBytes()

            cv.put("name"       , it.name)
            cv.put("byteArray"  , Base64.encode(byteArray!!))

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

        val json    = JSONObject()
        val result  = JSONObject()
        val mainAct = context as MainActivity

        json.put("orderUid"     , this.uuid)
        json.put("imageFormat"  , this.imageFormat?.uid)
        json.put("materialPhoto", this.materialPhoto?.uid)
        json.put("session"      , this.session)
        json.put("displayName"  , mainAct.auth.currentUser?.displayName.toString())
        json.put("email"        , mainAct.auth.currentUser?.email.toString())
        json.put("phoneNumber"  , mainAct.auth.currentUser?.phoneNumber.toString())
        json.put("uid"          , mainAct.auth.currentUser?.uid.toString())

        result.put("mValues", json)

        return result
    }

    fun send (){

        val progressBar             = (context as MainActivity)?.findViewById(R.id.progressBar) as ProgressBar
            progressBar.visibility  = ProgressBar.VISIBLE

        Thread {

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
                    result          = resultJSSONObj.toString()

                mainAct.log.add("result = $result")

                name            = mValues.getString("orderName")
                orderStatus     = mValues.getString("orderStatus")
                uuid            = mValues.getString("orderUuid")
                orderSendResult = name

            }
            catch (e: Exception) {

                // тууут ошибка загрузки заказа
                result = sendResult

                mainAct.log.add("result = $result")
            }

                progressBar.visibility  = ProgressBar.INVISIBLE

            val bundle = Bundle()

            bundle.putBoolean(  "sendorder"     , true)
            bundle.putString(   "orderName"     , name)
            bundle.putString(   "orderStatus"   , orderStatus)
            bundle.putString(   "orderUuid"     , uuid)

            mainAct.findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

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

    companion object {

        @JvmStatic
        fun sendAll(){
            ordersArray.forEach {
                it.send()
            }
        }

        @JvmStatic
        var ordersArray   : MutableList<Order>  = ArrayList()

    }
}