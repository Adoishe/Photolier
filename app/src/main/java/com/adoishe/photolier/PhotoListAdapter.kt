package com.adoishe.photolier

import android.app.Activity
import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class PhotoListAdapter(private val context: Activity, private val imageIdList: MutableList<Uri>) : BaseAdapter() {

    private fun getListener() : AdapterView.OnItemSelectedListener{
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
    }

    override fun getView(imageId: Int, view: View?, parent: ViewGroup?): View {

        val inflater                = context.layoutInflater
        val rowView                 = inflater.inflate(R.layout.photo_list_layout, parent , false)
        val imageView               = rowView.findViewById<ImageView>(R.id.imageItem)
        val spinnerFormat           = rowView.findViewById<Spinner>(R.id.spinnerFormat)
        val adapter                 = PhotosFragment.getSpinnerFormatAdapter(context)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFormat.adapter       = adapter

        spinnerFormat.post {
            spinnerFormat.onItemSelectedListener =  getListener()
        }

// каждый четный красим
      //  view?.setBackgroundColor(if ((imageId % 2)==0) Color.RED else Color.GREEN)

/*
        Glide
                .with(context)
                .load(imageIdList[imageId])
                .thumbnail((1/2).toFloat())
              //  .override(600     , 800)
                .fitCenter()
            .into( rowView as ImageView);



 */



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