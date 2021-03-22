package com.adoishe.photolier

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotosRecyclerViewAdapter(private val values: List<Uri>) : RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>() {

    override fun getItemCount() = values.size

    class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var largeTextView   : TextView? = null
        var smallTextView   : TextView? = null
        var imageView       : ImageView = ImageView(itemView.context)
        var spinnerFormat   : Spinner?  = null
        var qty             : EditText? = null
        var plus            : Button?   = null
        var minus           : Button?   = null

        init {
            largeTextView   = itemView.findViewById(R.id.textViewLarge)
            smallTextView   = itemView.findViewById(R.id.textViewSmall)
            imageView       = itemView.findViewById(R.id.imageView)
            spinnerFormat   = itemView.findViewById(R.id.spinnerFormat)
            qty             = itemView.findViewById(R.id.qty)
            plus            = itemView.findViewById(R.id.qtyPlus)
            minus           = itemView.findViewById(R.id.qtyMinus)

        }
    }

    private fun getListener() : AdapterView.OnItemSelectedListener{
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?,
                selectedItemPosition: Int,
                selectedId: Long
            ) {

                val toast = Toast.makeText( parent?.context
                    ,"Ваш выбор: $selectedItemPosition"
                    , Toast.LENGTH_SHORT)

                toast.show()

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photos_recyclerview_layout, parent, false)

        return PhotosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
//------------image
        holder.largeTextView?.text = values[position].toString()
        holder.smallTextView?.text = "кот"

        Glide
            .with(holder.itemView.context)
            .load(values[position])
            .thumbnail(0.1f) // 0.1f 10%
            //  .override(600     , 800)
            // .fitCenter()
            .into(holder.imageView)
  //-----------------------------spinner
        val spinnerAdapter              = PhotosFragment.getSpinnerFormatAdapter(holder.itemView.context)
        holder.spinnerFormat?.adapter   = spinnerAdapter
        
        holder.spinnerFormat?.post {
            holder.spinnerFormat?.onItemSelectedListener = PhotosFragment.getSpinnerListener(holder.itemView.context as MainActivity, holder.adapterPosition)
        }

        PhotosFragment.fillSpinner(0, holder.itemView.context as MainActivity , position )
//------------------------------------- qty
        holder.qty!!.addTextChangedListener(object : TextWatcher
            {
                override fun afterTextChanged(s: Editable) {


                    val toast = Toast.makeText(holder.itemView.context
                        ,s.toString()
                        , Toast.LENGTH_SHORT)

                    toast.show()

                    (holder.itemView.context as MainActivity).order.imageOrderList[position].qty = s.toString().toInt()

                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            }
        )
        //------------------------qty change

        val qtyChangeListener = View.OnClickListener {

            var qty = holder.qty!!.text.toString().toInt()

            when (it.id){
                R.id.qtyPlus->{

                    qty = qty++

                }
                R.id.qtyMinus->{

                    qty = qty--

                }
            }

            holder.qty!!.setText( qty.toString())

        }

        holder.plus?.setOnClickListener(qtyChangeListener)
        holder.minus?.setOnClickListener(qtyChangeListener)

    }
}