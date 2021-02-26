package com.adoishe.photolier

import android.net.Uri
import org.kobjects.base64.Base64

class ImageOrder{

                 var imageUri        : Uri?              = null
                 var name        : String?              = null
    var binary : ByteArray?= null


    constructor(imageUri : Uri, name : String){

        this.imageUri = imageUri
        this.name = imageUri.lastPathSegment
    }

    fun get64String() : String{

        return Base64.encode(binary)

    }
}