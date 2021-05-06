package com.adoishe.photolier

import android.content.ContentValues
import android.net.Uri
import org.kobjects.base64.Base64

class ImageOrder(imageUri: Uri, name: String) {

    var imageUri            : Uri?              = imageUri
    var imageThumbBase64    : String?           = ""
    var name                : String?           = null
    var binary              : ByteArray?        = null
    var paper               : ContentValues     = ContentValues()
    var imageFormat         : ImageFormat?      = null
    var qty                 : Int               = 1
    var materialPhoto       : MaterialPhoto?    = null

    init {

        this.name       = imageUri.lastPathSegment

        paper.put("uid"     , "")
        paper.put("name"    , "")
    }

    fun get64String() : String{

        return Base64.encode(binary)

    }
    fun setThumb (stringBase64: String){
        imageThumbBase64 = stringBase64
    }
}