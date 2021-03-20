package com.adoishe.photolier


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotosRecyclerViewAdapter(private val values: List<String>) :

        RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {

        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.photos_recyclerview_layout, parent, false)

        return PhotosViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: PhotosViewHolder,
        position: Int
    ) {
        holder.largeTextView?.text = values[position]
        holder.smallTextView?.text = "кот"
    }

    class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null

        init {
            largeTextView = itemView?.findViewById(R.id.textViewLarge)
            smallTextView = itemView?.findViewById(R.id.textViewSmall)
        }
    }




}