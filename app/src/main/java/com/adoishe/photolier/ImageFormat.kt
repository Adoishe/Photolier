package com.adoishe.photolier

class ImageFormat
{
    var width : Int = 0
    var height : Int = 0
    var uid     : String = ""


    constructor (width : Int, height : Int , uid : String) {

        this.height = height
        this.width  = width
        this.uid  = uid

   }
}