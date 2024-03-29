package com.adoishe.photolier

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import kotlin.math.abs


class PhotosRecyclerViewAdapter(private val values: List<Uri>, private val fragment: PhotosFragment)
    : RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>()
        ,  ItemTouchHelperAdapter{

    override fun getItemCount() = values.size

    class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var largeTextView   : TextView? = null
        var smallTextView   : TextView? = null
        var imageView       : ImageView = ImageView(itemView.context)
        var spinnerFormat   : Spinner?  = null
        var qty             : EditText? = null
        var plus            : Button?   = null
        var minus           : Button?   = null
        var crop            : Button?   = null
        var delete          : Button?   = null
        var material        : TextView?  = null
        var progressBar     : ProgressBar? = null

        init {
            largeTextView   = itemView.findViewById(R.id.textViewLarge)
            smallTextView   = itemView.findViewById(R.id.textViewSmall)
            imageView       = itemView.findViewById(R.id.imageView)
            spinnerFormat   = itemView.findViewById(R.id.spinnerFormat)
            qty             = itemView.findViewById(R.id.qty)
            plus            = itemView.findViewById(R.id.qtyPlus)
            minus           = itemView.findViewById(R.id.qtyMinus)
            crop            = itemView.findViewById(R.id.editPhoto)
            delete          = itemView.findViewById(R.id.deletePhoto)
            material        = itemView.findViewById(R.id.material)
            progressBar     = itemView.findViewById(R.id.progressBar)
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    private fun setThumbnail(context : MainActivity, position : Int) : Thread {
        return Thread{

            val rate        = 15
            val source      = ImageDecoder.createSource(context.contentResolver,  values[position])
            val bmOriginal  = ImageDecoder.decodeBitmap(source)
            val width: Int  = bmOriginal.width
            val height: Int = bmOriginal.height
            val halfWidth   = width     / rate
            val halfHeight  = height    / rate
            val bmHalf      = Bitmap.createScaledBitmap(
                                                            bmOriginal, halfWidth,
                                                            halfHeight, false
                                                        )
/*
            val bitmap = Glide
                .with(context)
                .asBitmap()
                .load(values[position])
               // .apply(RequestOptions().override(50, 50))
                //.thumbnail(0.05f) // 0.1f 10%
                 //.override(50     , 50)
                .apply(RequestOptions().override(50).downsample(DownsampleStrategy.CENTER_INSIDE).skipMemoryCache(true).diskCacheStrategy(
                    DiskCacheStrategy.NONE))
                // .fitCenter()
                .submit()
                .get()


            var bitmap = Glide.with(application)
                .asBitmap()
                .load(file)
                // .apply(RequestOptions().override(size))
                // .apply(RequestOptions().override(size).downsample(DownsampleStrategy.AT_MOST))
                .apply(RequestOptions().override(size).downsample(DownsampleStrategy.CENTER_INSIDE).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .submit().get()

            val out = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()
 */
            val base64String = (context.encodeImage(bmHalf))

            context.order.imageOrderList[position].imageThumbBase64 = base64String
        }
    }

    private fun getQtyTextWatcher  (holder: PhotosViewHolder, position: Int) : TextWatcher {

        return object : TextWatcher
        {
            override fun afterTextChanged(s: Editable) {

/*
                val toast = Toast.makeText(holder.itemView.context
                    ,s.toString()
                    , Toast.LENGTH_SHORT)

                toast.show()

 */

                val qtyString = s.toString()

                var qtyInt = 1

                if (qtyString.isEmpty()){

                }

                else{

                    qtyInt = abs(qtyString.toInt())

                    (holder.itemView.context as MainActivity).order.imageOrderList[position].qty = qtyInt

                    if (qtyInt < 1)
                        qtyInt = 1

                }

                if(qtyString != qtyInt.toString())
                    holder.qty!!.setText(qtyInt.toString())

            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }

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

        //-------------thumb
// TODO включить thumbnails
//        val setThumbnailThread = setThumbnail((holder.itemView.context as MainActivity) , position )
//
//        setThumbnailThread.start()

  //-----------------------------spinner
        val spinnerAdapter              = PhotosFragment.getSpinnerFormatAdapter(holder.itemView.context)
        holder.spinnerFormat?.adapter   = spinnerAdapter

        when((holder.itemView.context as MainActivity).order.imageOrderList[position].imageFormat){
            null->{
                // если список фоток пустой
                holder.spinnerFormat!!.setSelection(0)
            }
            else -> {
                // список фоток не пустой
                // выделяем тот формат, который выбран в элементе массив
                holder.spinnerFormat!!.setSelection((holder.itemView.context as MainActivity).order.imageOrderList[position].imageFormat!!.index)
            }
        }

        holder.spinnerFormat?.post {
            holder.spinnerFormat?.onItemSelectedListener = PhotosFragment.getSpinnerListener(holder.itemView.context as MainActivity, holder.adapterPosition)
        }

        // заполняем  элементы нахера?
        //PhotosFragment.fillSpinner(0, holder.itemView.context as MainActivity , position )
//------------------------------------- qty
        holder.qty!!.setText((holder.itemView.context as MainActivity).order.imageOrderList[position].qty.toString())
        holder.qty!!.addTextChangedListener(getQtyTextWatcher(holder, position) )
//------------------------qty change
        val qtyChangeListener = View.OnClickListener {

            var qty = holder.qty!!.text.toString().toInt()

            when ((it as Button).text){
                "+"-> qty++
                "-"-> qty--
            }
            holder.qty!!.setText( qty.toString())
        }

        holder.plus?.setOnClickListener(qtyChangeListener)
        holder.minus?.setOnClickListener(qtyChangeListener)
//-------------crop

        holder.crop!!.setOnClickListener {

            val uri = (holder.itemView.context as MainActivity).order.imageOrderList[position].imageUri

            fragment.croppingPosition = position

            CropImage.activity(uri)
                .setAllowRotation(true)
                .setAspectRatio(3, 4)
                .setCropMenuCropButtonTitle((holder.itemView.context as MainActivity).resources.getString(R.string.crop))
                .setActivityTitle((holder.itemView.context as MainActivity).resources.getString(R.string.crop))
                .start(holder.itemView.context as MainActivity, fragment)
        }
// imageview
        holder.imageView!!.setOnClickListener {

            val uri = (holder.itemView.context as MainActivity).order.imageOrderList[position].imageUri

            fragment.croppingPosition = position

            CropImage.activity(uri)
                .setAllowRotation(true)
                .setAspectRatio(3, 4)
                .setCropMenuCropButtonTitle((holder.itemView.context as MainActivity).resources.getString(R.string.crop))
                .setActivityTitle((holder.itemView.context as MainActivity).resources.getString(R.string.crop))
                .start(holder.itemView.context as MainActivity, fragment)
        }
//------------ delete
      holder.delete!!.setOnClickListener {

          (holder.itemView.context as MainActivity).order.imageOrderList.removeAt(position)
          fragment.imageUriList.removeAt(position)
          fragment.updateList()

      }
//----------------material

    holder.material!!.text = (holder.itemView.context as MainActivity).order.imageOrderList[position].materialPhoto!!.name

    }//onBindViewHolder


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {

        (fragment.context as MainActivity).order.imageOrderList.removeAt(position)
        fragment.imageUriList.removeAt(position)
        fragment.updateList()

    }
}