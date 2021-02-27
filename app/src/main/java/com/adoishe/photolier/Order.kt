package com.adoishe.photolier

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.kobjects.base64.Base64
import java.util.*
import kotlin.collections.ArrayList

class Order {

    private             val OrderUuid       : String                    = UUID.randomUUID().toString()
                        var imageUriList    : MutableList<Uri>          = ArrayList()
    private             var byteArrayList   : MutableList<ByteArray>    = ArrayList()
                        var imageOrderList  : MutableList<ImageOrder>   = ArrayList()
                        val auth                                        = FirebaseAuth.getInstance()!!
                        var context         : Activity
                        var result                                      = String()

    constructor(context: Activity) {

        this.context = context

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

            cv.put("name"       , it.name)
            cv.put("byteArray"  , Base64.encode( byteArray!!))

            cvArrayList.add(cv)

        }

        return cvArrayList
    }

    fun getCvForWs() : ContentValues{

        var cv = ContentValues()

        cv.put("DisplayName"    , this.auth.currentUser?.displayName)
        cv.put("Email"          , this.auth.currentUser?.email)
        cv.put("PhoneNumber"    , this.auth.currentUser?.phoneNumber)
        cv.put("Uid"            , this.auth.currentUser?.uid)
        cv.put("OrderUid"       , this.OrderUuid)

        return cv
    }

    fun send (){

        Thread {

            byteArrayList               = getByteArrayList()
            var cv                      = getCvForWs()
            val dl                      = DataLoader()
            val outputJson: String      = Gson().toJson(cv)
            var cvArrayList             = getCvArrayList()
            val jsoCvArrayList: String  = Gson().toJson(cvArrayList)
            var sendResult              = dl.doing(outputJson, jsoCvArrayList)
            val builder                 = GsonBuilder()
            val gson                    = builder.create()

            try {
                cv          = gson.fromJson(sendResult, ContentValues::class.java)
                result      = cv.toString()
            }
            catch (e : Exception) {
                result     = sendResult.toString()
            }

        }.start()

    }
}