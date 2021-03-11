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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImage
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray


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
    //private lateinit    var recyclerView     : RecyclerView

    private lateinit    var adapter                 : PhotoListAdapter
    private             var croppingPosition        : Int = -1
                        var availableImageFormats   : MutableList<ImageFormat?>  = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun getFormatsByMaterialThread(materialUid :String): Thread{

        return Thread{
            //viewPager.currentItem = tab.position
            var dl = DataLoader()
            var res = dl.getFormatsByMaterial(materialUid)

            var resJarray = JSONArray(res)

            availableImageFormats  = ArrayList()

            for (j in 0 until resJarray.length()){

                availableImageFormats
                    .add(ImageFormat.imageFormats.find { imageFormat -> imageFormat.uid == resJarray.getString(j) })
            }
            val progressBar = (context as MainActivity).findViewById(R.id.progressBar) as ProgressBar
            progressBar.visibility = ProgressBar.INVISIBLE
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root    = inflater.inflate(R.layout.fragment_photos, container, false)
        val mainAct = context as MainActivity

        if(mainAct.auth.currentUser != null){
            //If user is signed in
            this.requireActivity().title  = resources.getString(R.string.app_name)  + ' '  + resources.getString(
                    R.string.ffor
            )  + ' ' + (context as MainActivity).auth.currentUser!!.displayName as CharSequence
        }

        val loadButton = root.findViewById<Button>(R.id.buttonLoadPicture)
        val cropButton = root.findViewById<Button>(R.id.buttonCropPicture)
        val sendButton = root.findViewById<Button>(R.id.buttonSendPictures)
            //  val ordersButton = root.findViewById<Button>(R.id.buttonGetOrders)
        val resultTextView = root.findViewById<TextView>(R.id.textViewResult)
        var tabLayout = root.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL


        MaterialPhoto.materialsPhoto.forEach {

            var matTab = tabLayout.newTab()

            matTab.id = it.hash
            matTab.tag = it.uid
            matTab.text = it.name

            tabLayout.addTab(matTab);
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

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

                var getFormatsByMaterialThread = getFormatsByMaterialThread(tab.tag.toString())

                val progressBar = (context as MainActivity).findViewById(R.id.progressBar) as ProgressBar
                progressBar.visibility = ProgressBar.VISIBLE
                getFormatsByMaterialThread.start()
                getFormatsByMaterialThread.join()

                    // resultTextView.text = availableImageFormats.toString()


                val spinnerFormat : Spinner = requireActivity().findViewById(R.id.spinnerFormat)



                val arrNames : Array<String> = Array(availableImageFormats.size) { index ->
                    availableImageFormats[index]!!.name

                }


                val adapter : ArrayAdapter<String> = ArrayAdapter<String>(requireContext()
                    , R.layout.support_simple_spinner_dropdown_item
                    , arrNames)

                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)


                spinnerFormat.adapter = adapter
                spinnerFormat.post {
                    spinnerFormat.onItemSelectedListener =  getListener()
                }



            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })



       // linearLayout   = root.findViewById(R.id.linearLayout)
        listView        = root.findViewById(R.id.list) //ListView(context)
    //    recyclerView     = root.findViewById(R.id.recycler) //ListView(context)

       // linearLayout?.addView(listView)

        loadButton.setOnClickListener{

            val intent  = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //allows any image file type. Change * to specific extension to limit it

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            startActivityForResult(
                    Intent.createChooser(intent, resources.getString(R.string.selectPic)),
                    SELECT_PICTURES
            )

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

                    val builder = AlertDialog.Builder(requireContext())

                        builder.setMessage(resources.getString(R.string.send_photos) + "?")
                            .setCancelable(false)
                            .setPositiveButton(resources.getString(R.string.yes))
                            { dialog, id ->

                                val main = (context as MainActivity)

                                main.order.send()

                            }
                            .setNegativeButton(resources.getString(R.string.no))
                            { dialog, id ->
                                // Dismiss the dialog
                                dialog.dismiss()
                            }

            val alert = builder.create()

            alert.show()
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




/*
        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(requireContext(), recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                        val uri = recyclerView.getChildAt(position) as Uri


                        CropImage.activity(uri)
                                .setAllowRotation(true)
                                .setAspectRatio(3, 4)
                                .setCropMenuCropButtonTitle(resources.getString(R.string.crop))
                                .setActivityTitle(resources.getString(R.string.crop))
                                .start(requireActivity(), requireParentFragment())

                        croppingPosition = position;

                        //imageUriList.removeAt(i)

                        adapter.notifyDataSetChanged()



                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        TODO("Not yet implemented")
                    }

                    /*  fun onLongItemClick(view: View?, position: Int) {
                          // do whatever
                      }

                     */
                })
        )

 */


        return root
    }

    private fun insertUriToListView(resultUri: Uri) {

        val mainAct = context as MainActivity
// если добавление нового фото
        if (croppingPosition == -1) {

            imageUriList.add(resultUri)

            var imageOrder = ImageOrder(resultUri, resultUri.toString())

            mainAct.order.imageOrderList.add(imageOrder)
        }
// если редактирование ранее добавленного
        else {

            imageUriList[croppingPosition]                          = resultUri
            mainAct.order.imageOrderList[croppingPosition].imageUri = resultUri
            croppingPosition                                        = -1
        }

        adapter             = PhotoListAdapter(this.requireActivity(), imageUriList)
        listView.adapter    = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
// если фото получено из редактора
            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {

                insertUriToListView(result!!.uri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
            }
        } else
// если фото получено из галереи
            if(requestCode == SELECT_PICTURES) {
                if(resultCode == RESULT_OK) {

                    if(data!!.clipData == null) {

                        if(data!!.data != null){
                            insertUriToListView(data.data!!)
                        }

                    }
                    else  {
// если было выбрано много фото
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