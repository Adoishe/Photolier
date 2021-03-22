package com.adoishe.photolier

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject


 class ImageFormat
{
    var width : Int = 0
    var height : Int = 0
    var hash : Int = 0
    var index : Int = 0
    var uid     : String = ""
    var name     : String = ""


    constructor (width: Int, height: Int, uid: String, name: String, hash: Int) {

        this.height = height
        this.width  = width
        this.uid  = uid
        this.name  = name
        this.hash  = hash

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
        var imageFormats    : MutableList<ImageFormat>  = ArrayList()
    }
}