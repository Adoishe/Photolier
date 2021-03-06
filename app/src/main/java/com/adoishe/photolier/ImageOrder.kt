package com.adoishe.photolier

import android.content.ContentValues
import android.net.Uri
import org.kobjects.base64.Base64

class ImageOrder{

                var imageUri    : Uri?              = null
                var name        : String?           = null
                var binary      : ByteArray?        = null
                var paper       : ContentValues     = ContentValues()
    lateinit    var imageFormat : ImageFormat

    constructor(imageUri : Uri, name : String){

        this.imageUri   = imageUri
        this.name       = imageUri.lastPathSegment

        paper.put("uid", "")
        paper.put("name", "")
    }

    fun get64String() : String{

        return Base64.encode(binary)

    }
}