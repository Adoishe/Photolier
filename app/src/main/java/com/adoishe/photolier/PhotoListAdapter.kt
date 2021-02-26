package com.adoishe.photolier

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class PhotoListAdapter(private val context: Activity, private val imageIdList: MutableList<Uri>)
    : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val inflater                = context.layoutInflater
        val rowView                 = inflater.inflate(R.layout.photo_list_layout, null)
        val imageView : ImageView   = rowView.findViewById(R.id.image_item)

        p1?.setBackgroundColor(if ((p0 % 2)==0) Color.RED else Color.GREEN)


        imageView.setImageURI(imageIdList[p0])

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