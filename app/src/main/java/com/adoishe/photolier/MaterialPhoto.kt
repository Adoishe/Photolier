package com.adoishe.photolier

import android.content.ContentValues
import android.content.Context
import org.json.JSONObject

class MaterialPhoto {

    var uid : String = ""
        get() { return field }
    var name : String = ""
    var hash : Int = 0
    var indexInArray : Int = 0

    constructor (uid: String, name: String, hash: Int) {

        this.uid  = uid
        this.name  = name
        this.hash  = hash

    }

    override fun toString() = name

    fun toCv(): ContentValues{

        var cv = ContentValues()

        cv.put("uid" , uid)
        cv.put("name" , name)

        return cv

    }

    companion object{
        @JvmStatic
        fun sync(context: Context) : JSONObject {

            return DataLoader.sync(context, "Справочник.Материалы")

        }

        @JvmStatic
        var materialsPhoto : MutableList<Any>  = ArrayList()

        val NONSYNC = 0
        val SYNC = 1
        val SYNCERR = 2
        var status = NONSYNC
        var syncerr = ""

        @JvmStatic
        fun toCvArrayList () : MutableList<ContentValues> {

            val cvArrayList    : MutableList<ContentValues>  = ArrayList()

            materialsPhoto.forEach { (it as MaterialPhoto)

                cvArrayList.add( it.toCv() )

            }

            return cvArrayList
        }

    }
}
