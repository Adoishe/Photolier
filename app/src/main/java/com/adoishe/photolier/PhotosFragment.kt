package com.adoishe.photolier

//import com.theartofdev.edmodo.cropper.CropImage
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.net.URI


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1        = "param1"
private const val ARG_PARAM2        = "param2"
private const val pickImage         = 100
private const val SELECT_PICTURES   = 1

/**
 * A simple [Fragment] subclass.
 * Use the [PhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private             var param1          : String?           = null
    private             var param2          : String?           = null
    private             var imageUriList    : MutableList<Uri>  = ArrayList()
    private             var imageUri        : Uri?              = null
    private lateinit    var listView        : ListView
    private lateinit    var adapter         : PhotoListAdapter
    private             var croppingPosition: Int = -1
     lateinit           var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_photos, container, false)

        order = Order(requireActivity())


        if(auth.currentUser != null){ //If user is signed in

            this.requireActivity().title  = resources.getString(R.string.app_name) + ' ' + resources.getString(
                R.string.ffor
            ) + ' ' + auth.currentUser!!.displayName as CharSequence
//                startActivity(Next Activity)
        }


        val loadButton = root.findViewById<Button>(R.id.buttonLoadPicture)
        val cropButton = root.findViewById<Button>(R.id.buttonCropPicture)
        val sendButton = root.findViewById<Button>(R.id.buttonSendPictures)
        val ordersButton = root.findViewById<Button>(R.id.buttonGetOrders)
        val resultTextView = root.findViewById<TextView>(R.id.textViewResult)
       // linearLayout   = root.findViewById(R.id.linearLayout)
        listView        = root.findViewById(R.id.list) //ListView(context)

       // linearLayout?.addView(listView)

        loadButton.setOnClickListener{

        //    val intent = Intent()
        //    intent.type = "image/*"
         //   intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
         //   intent.action = Intent.ACTION_GET_CONTENT
          //  startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)

            val intent  = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //allows any image file type. Change * to specific extension to limit it

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            startActivityForResult(
                Intent.createChooser(intent, resources.getString(R.string.selectPic)),
                SELECT_PICTURES
            ) //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult


            //      val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

       //     startActivityForResult(gallery, pickImage)

          //  CropImage.activity(imageUri).start(this.requireActivity());
        // CropImage.activity().start(requireContext(), this);

        }

        cropButton.setOnClickListener{

            CropImage.activity()
                .setAllowRotation(true)
                .setAspectRatio(3, 4)
                .setCropMenuCropButtonTitle(resources.getString(R.string.crop))
                .setActivityTitle(resources.getString(R.string.selectCrop))
                .start(requireContext(), this)
        }


        sendButton.setOnClickListener{

                order.send()

        }

        ordersButton.setOnClickListener {


            Thread {
                var cv : ContentValues
                var result : String
                val dl                      = DataLoader()
                var sendResult              = dl.getOrders( auth.currentUser!!.uid)
                val builder                 = GsonBuilder()
                val gson                    = builder.create()

                try {
                    var arrayCV      = gson.fromJson(sendResult, Array<ContentValues>::class.java).toList()//cv.toString()

                    var orders : MutableList<Order> = ArrayList()

                    arrayCV.forEach(){

                        var order1c = Order(requireActivity())

                        var imageUriList    : MutableList<Uri>          = ArrayList()

                        (it.get("imageUriList") as ArrayList<String>).forEach(){

                            var uri1c = Uri.parse(it)

                            imageUriList.add(uri1c)

                        }

                        order1c.imageUriList = imageUriList

                        orders.add(order1c)
                    }

                    result           = orders.toString()

                    resultTextView.setText(result)

                }
                catch (e : Exception) {

                    result     = sendResult.toString()

                    resultTextView.setText(result)
                }

            }.start()

        }

        listView.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, i: Int, l: Long ->

            val uri = listView.getItemAtPosition(i) as Uri

            CropImage.activity(uri)
                .setAllowRotation(true)
                .setAspectRatio(3, 4)
                .setCropMenuCropButtonTitle(resources.getString(R.string.crop))
                .setActivityTitle(resources.getString(R.string.crop))
                .start(requireContext(), this)

            croppingPosition = i;

            //imageUriList.removeAt(i)

            adapter.notifyDataSetChanged()

        }

        return root
    }

    private fun insertUriToListView(resultUri: Uri) {

        if (croppingPosition == -1) {

            imageUriList.add(resultUri)

            var imageOrder = ImageOrder(resultUri , resultUri.toString())

            order.imageOrderList.add(imageOrder)
        }

        else {
            imageUriList[croppingPosition]  = resultUri

            order.imageOrderList[croppingPosition].imageUri = resultUri

            croppingPosition                = -1

        }

        adapter             = PhotoListAdapter(this.requireActivity(), imageUriList)
        listView.adapter    = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {

                insertUriToListView(result!!.uri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
            }
        } else

            if(requestCode == SELECT_PICTURES) {
                if(resultCode == RESULT_OK) {

                    if(data!!.clipData == null) {

                        if(data!!.data != null){
                            insertUriToListView(data.data!!)
                        }

                    }
                    else  {

                        val count = data.clipData!!.itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.

                        for( i in 0 until count)
                            insertUriToListView(data.clipData!!.getItemAt(i).uri)

                        //String imagePath = data.getData().getPath();
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                }//if(resultCode == RESULT_OK)
            } //if(requestCode == SELECT_PICTURES)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PhotosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}