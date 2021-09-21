package com.adoishe.photolier

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile () {

    var uid = ""
    var email = ""
    var displayName = ""
    var phoneNumber = 0
   // var postalAddresses : ContentValues = ContentValues()


    init{

        val auth            = FirebaseAuth.getInstance()
        this.uid            = auth.currentUser!!.uid
        this.displayName    = auth.currentUser!!.displayName.toString()
        this.email          = auth.currentUser?.email.toString()

    }

    fun save (){

        val ref     = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles")

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

    fun load(){

        val ref     = FirebaseDatabase.getInstance(MainActivity.FIREINSTANCE).getReference("profiles").orderByChild("uid").equalTo(uid)

        //val rootRef = FirebaseDatabase.getInstance().reference
       // val rootRef = rootRef.child("orders").orderByChild("phonenumber").equalTo(givenString)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val username = ds.child("displayName").getValue(String::class.java)
                    Log.d("FirebaseActivity", username as String)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("FirebaseActivity", databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ref.addListenerForSingleValueEvent(valueEventListener)

    }
}