package com.adoishe.photolier

import android.content.ContentValues
import android.net.Uri
import org.kobjects.base64.Base64
import java.math.BigDecimal
import java.util.*

class ImageOrder(name: String) {

    var imageUri            : Uri?              = null
    var imageThumbBase64    : String?           = ""
    var name                : String?           = null
    var binary              : ByteArray?        = null
    var paper               : ContentValues     = ContentValues()
    var imageFormat         : ImageFormat?      = null
    var qty                 : Int               = 1
    var materialPhoto       : MaterialPhoto?    = null
    var price                                   = BigDecimal("0")
    var lastOne                                 = false
    var progressBarId       :Int                = 0

     var uuid                :String             = ""

    init {

        this.name = name
        this.uuid = UUID.randomUUID().toString()

        paper.put("uid"     , "")
        paper.put("name"    , "")
    }

    init {
        this.name = name
    }
    constructor(imageUri: Uri, name: String) : this(name) {

        this.name       = imageUri.lastPathSegment
        this.imageUri   = imageUri

    }

    public fun isLastOne(lastOne : Boolean) : Unit {

        this.lastOne = lastOne
    }

    fun get64String() : String{

        return Base64.encode(binary)

    }
    fun setThumb (stringBase64: String){
        imageThumbBase64 = stringBase64
    }
}