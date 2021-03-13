package com.adoishe.photolier

import android.app.Activity
import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.*

class PhotoListAdapter(private val context: Activity, private val imageIdList: MutableList<Uri>)
    : BaseAdapter() {





    override fun getView(imageId: Int, view: View?, p2: ViewGroup?): View {


          fun getListener() : AdapterView.OnItemSelectedListener{
            return object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        itemSelected: View?,
                        selectedItemPosition: Int,
                        selectedId: Long
                ) {


                    val toast = Toast.makeText( context
                            ,"Ваш выбор: $selectedItemPosition"
                            , Toast.LENGTH_SHORT)
                    toast.show()


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
            fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        val inflater                = context.layoutInflater
        val rowView                 = inflater.inflate(R.layout.photo_list_layout, null)
        val imageView : ImageView   = rowView.findViewById(R.id.image_item)
        val spinnerFormat : Spinner = rowView.findViewById(R.id.spinnerFormat)
        var formatsArrCV            = ArrayList<ContentValues>()

        var cv = ContentValues()

        cv.put("formatName" , "10x15")

        var cv1 = ContentValues()
        cv1.put("formatName" , "09x10")

        formatsArrCV.add(cv)
        formatsArrCV.add(cv1)


        val arrNames : Array<String> = Array(formatsArrCV.size) { index ->
            formatsArrCV[index].getAsString("formatName")

        }


        val adapter : ArrayAdapter<String> = ArrayAdapter<String>(context
                , R.layout.support_simple_spinner_dropdown_item
                , arrNames)

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)


        spinnerFormat.adapter = adapter
        spinnerFormat.post {
            spinnerFormat.onItemSelectedListener =  getListener()
        }


// каждый четный красим
        view?.setBackgroundColor(if ((imageId % 2)==0) Color.RED else Color.GREEN)

        imageView.setImageURI(imageIdList[imageId])

        return rowView
    }
    override fun getItem(p0: Int): Any {
        return imageIdList[p0]
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return imageIdList.size
    }
}