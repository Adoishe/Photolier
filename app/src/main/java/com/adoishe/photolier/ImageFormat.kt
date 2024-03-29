package com.adoishe.photolier

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import java.math.BigDecimal


class ImageFormat
{
    var width   : Int = 0
    var height  : Int = 0
    var widthPix   : Int = 0
    var heightPix  : Int = 0
    var hash    : Int = 0
    var index   : Int = 0
    var price   = BigDecimal("0")

    var uid     : String    = ""
        get() { return field }

    var name    : String    = ""
        get() { return field + "(" + price + "₽)"}
        set(value) {
             field = value
        }

    constructor (width: Int, height: Int, uid: String, name: String, hash: Int ,  widthPix : Int , heightPix : Int) {

        this.height = height
        this.width  = width
        this.heightPix = heightPix
        this.widthPix  = widthPix
        this.uid    = uid
        this.name   = name
        this.hash   = hash

   }

    override fun toString() = name + "\n" + price

    fun toCv(): ContentValues {

        val cv = ContentValues()

        cv.put("uid" , uid)
        cv.put("name" , name)

        return cv

    }

    fun save(){

        //userRef = FirebaseDatabase.getInstance().getReference();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//        FirebaseDatabase.getInstance().getReference("disconnectmessage").onDisconnect().setValue("I disconnected!")
      //  FirebaseFirestore.setLoggingEnabled(true);



        val mDatabase           = FirebaseDatabase.getInstance("https://photolier-ru-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val imageFormatsFire    = mDatabase.child("imageFormats")
        val imageFormatFire     = imageFormatsFire.child(uid)

        imageFormatFire.setValue(uid)

        imageFormatFire.child("name").setValue(name)
        imageFormatFire.child("width").setValue(width)
        imageFormatFire.child("height").setValue(height)
        imageFormatFire.child("hash").setValue(hash)

        imageFormatsFire.push()
    }



    companion object{

        @JvmStatic
        fun getHashes(context: Context) {

           // var collection = FirebaseDatabase.getInstance().getReference("ImageFormats")
//            var query = collection.orderByChild("id").

            val ref = FirebaseDatabase.getInstance().reference.child("ImageFormats")

            ref.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        Log.d("FirebaseActivity", it.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        @JvmStatic
        fun sync(context: Context) :JSONObject {
           return DataLoader.sync(context, "Справочник.Форматы")
        }

        @JvmStatic
        var syncSucc = false
        var res = ""
        var imageFormats    : MutableList<Any>  = ArrayList()
        val NONSYNC = 0
        val SYNC = 1
        val SYNCERR = 2
        var status = NONSYNC
        var syncerr = ""

        @JvmStatic
        fun toCvArrayList () : MutableList<ContentValues> {

            val cvArrayList    : MutableList<ContentValues>  = ArrayList()

            imageFormats.forEach { (it as ImageFormat)

                cvArrayList.add( it.toCv() )

            }

            return cvArrayList

        }


    }
}