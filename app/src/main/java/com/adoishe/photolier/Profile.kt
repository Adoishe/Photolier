package com.adoishe.photolier

//import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class Profile () {

    var uid = ""
    var guid = ""
    var email = ""
    var displayName = ""
    var firstName = ""
    var lastName = ""
    var phoneNumber = ""
    var postalAddresses: ArrayList<String> = ArrayList()
    var pushToken = ""


    init {

        val auth            = FirebaseAuth.getInstance()
        this.uid            = auth.currentUser!!.uid
        this.displayName    = auth.currentUser!!.displayName.toString()
        this.email          = auth.currentUser?.email.toString()

       //this.load_(this.uid)
    }

    private fun setGuid (){

        this.guid = UUID.randomUUID().toString()

        save()
    }

    fun save() {

        val ref = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles")

        ref.child(uid).setValue(this).addOnCompleteListener {

            Log.d("FirebaseActivity", it.toString())
        }

        /*
           //  fireId  = ref.push().key.toString()
        val mDatabase           = FirebaseDatabase.getInstance("https://photolier-ru-default-rtdb.europe-west1.firebasedatabase.app/").reference
        val profilesFire    = mDatabase.child("profiles")
        val profileFire     = profilesFire.child(uid)

        profileFire.setValue(uid).setv

        //profileFire.child("displayName").setValue(displayName)
        //profileFire.child("email").setValue(email)
        //////profileFire.child("height").setValue(height)
        /////profileFire.child("hash").setValue(hash)

        profileFire.push()
            */


    }

    fun load_(uid: String) {


//        val ref =
//            FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles")
//
//
//        ref.child(uid).setValue(this).addOnCompleteListener {
//
//            Log.d("FirebaseActivity", it.toString())
//        }
//

        val ref =
            FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val profile = snapshot.getValue(Profile::class.java)!!


                Log.d("FirebaseActivity", profile.phoneNumber.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        ref.child(uid).addListenerForSingleValueEvent(valueEventListener)
    }

    companion object {
        @JvmStatic
        var profile : Profile = Profile()

        @JvmStatic

        fun hasEnoughData(): Boolean{

            var result = true

            result = result  and (profile.email != "") and (profile.phoneNumber != "")

            return result
        }

    fun updateToken(){

        // 1
//        FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isComplete){

                val firebaseToken   = task.result.toString()
                val oldToken        = profile.pushToken

                if (oldToken != firebaseToken)
                {
                    profile.pushToken = firebaseToken

                    profile.save()
                }

                Log.d("Token", firebaseToken)

                //Util.printLog(firebaseToken)
            }
        }


        /*
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                // 2
                if (!task.isSuccessful) {
                    Log.w("Token failed", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // 3
                val token = task.result?.token

                // 4
                //val msg = getString(R.string.token_prefix, token)
                if (token != null) {

                    val oldToken = profile.pushToken

                    if (oldToken != token)
                    {
                        profile.pushToken = token

                        profile.save()
                    }

                    Log.d("Token", token)
                }
                // Toast.makeText(requireContext(), token, Toast.LENGTH_LONG).show()
            })

         */

    }

        fun load(uid: String) {

            Log.d("FirebaseActivity", uid)

            val ref = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles")

            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val gottenValue = snapshot.getValue(Profile::class.java)

                    if (gottenValue != null) {

                        profile = gottenValue

                        if (profile.guid == "") profile.setGuid()

                        updateToken()
                    }

                    Log.d("FirebaseActivity", profile.phoneNumber.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            ref.child(uid).addListenerForSingleValueEvent(valueEventListener)

            // Log.d("FirebaseActivity", r) //Don't ignore errors!

            /*
             val ref     = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles").orderByChild("uid").equalTo(uid)

             //val rootRef = FirebaseDatabase.getInstance().reference
            // val rootRef = rootRef.child("orders").orderByChild("phonenumber").equalTo(givenString)
             val valueEventListener = object : ValueEventListener {

                 override fun onDataChange(dataSnapshot: DataSnapshot) {

                     val profile = dataSnapshot.getValue(Profile::class.java)
     /*
                     for (ds in dataSnapshot.children) {

                         //val username = ds.child("displayName").getValue(String::class.java)
                         val username = ds.child(uid).ref.getValue(String::class.java)//children.getValue(Profile::class.java)

                         Log.d("FirebaseActivity", uid)
                         Log.d("FirebaseActivity", username.toString())
                     }

      */
                     Log.d("FirebaseActivity", profile.toString())
                 }

                 override fun onCancelled(databaseError: DatabaseError) {

                     Log.d("FirebaseActivity", databaseError.getMessage()) //Don't ignore errors!
                 }
             }

             ref.addListenerForSingleValueEvent(valueEventListener)
         }

              */
        }
    }
}