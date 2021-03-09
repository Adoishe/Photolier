package com.adoishe.photolier

import android.content.Context
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



        val mDatabase       = FirebaseDatabase.getInstance("https://photolier-ru-default-rtdb.europe-west1.firebasedatabase.app/").reference
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

            var collection = FirebaseDatabase.getInstance().getReference("ImageFormats")
//            var query = collection.orderByChild("id").


        }
        @JvmStatic
        fun sync(context: Context) :JSONObject {

            var hashArrayList0 = ImageFormat.getHashes(context)

            val hashArrayList   : MutableList<Int>  = ArrayList()




            hashArrayList.add(1)
            hashArrayList.add(2)
            hashArrayList.add(3)



            val dl                                  = DataLoader()
            val sendResult                          = dl.syncFormats(hashArrayList, context)
            var resultJSSONObj                      = JSONObject()
            var succ: Boolean = (sendResult != "")

            when (succ) {

                true -> {
                    resultJSSONObj = JSONObject(sendResult)

                    when (resultJSSONObj.getBoolean("succ")) {
                        true -> {

                            var resArray = resultJSSONObj.getJSONArray("resArray")
/*
                            "Код": "000000018",
                            "Наименование": "09 х 13",
                            "ПометкаУдаления": false,
                            "Высота": 9,
                            "Ширина": 13,
                            "uid": "7a3e31ea-77b7-11eb-b993-60a44c65164b"
                                constructor (width : Int, height : Int , uid : String)

 */

                            for (i: Int in 0 until resArray.length()) {

                                val item = JSONObject(resArray[i].toString())//resArray.getJSONObject(i)
                                val height = item.getInt("height")
                                val width = item.getInt("width")
                                val hash = item.getInt("hash")
                                val uid = item.getString("uid")
                                val name = item.getString("name")
                                val imageFormat = ImageFormat(width, height, uid, name, hash)

                                imageFormat.save()
                            }

                            //renewImageFormats()
                        }
                    }
                }
            }

            return resultJSSONObj
        }

        @JvmStatic
        var syncSucc = false
        var res = ""
    }
}