package com.adoishe.photolier

import android.content.Context
import org.json.JSONObject

class MaterialPhoto {

    var uid : String = ""
    var name : String = ""
    var hash : Int = 0

    constructor (uid: String, name: String, hash: Int) {

        this.uid  = uid
        this.name  = name
        this.hash  = hash

    }

    companion object{
        @JvmStatic
        fun sync(context: Context) : JSONObject {

            return DataLoader.sync(context, "Справочник.Материалы")

        }

        @JvmStatic
        var materialsPhoto : MutableList<MaterialPhoto>  = ArrayList()

        val NONSYNC = 0
        val SYNC = 1
        val SYNCERR = 2
        var status = NONSYNC
        var syncerr = ""



    }
}
