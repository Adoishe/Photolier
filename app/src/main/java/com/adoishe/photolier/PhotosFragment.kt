package com.adoishe.photolier

//import com.theartofdev.edmodo.cropper.CropImage
import android.app.Activity.RESULT_OK
import android.content.Context
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImage
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal


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
                        var imageUriList    : MutableList<Uri>  = ArrayList()
    private             var imageUri        : Uri?              = null
    private lateinit    var listView        : ListView
    //private lateinit    var recyclerView     : RecyclerView

    private lateinit    var adapter                 : PhotoListAdapter
                        var croppingPosition        : Int = -1
    lateinit            var mainAct                 : MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun blankOrderData(){

        imageUriList    = ArrayList()
        mainAct.order   = Order(mainAct)

        //mainAct.order.imageOrderList    = ArrayList()
    }

    private fun setQtyText(){

        val photosInPack = requireView().findViewById<TextView>(R.id.textViewPhotosInPack)
        val packsInOrder = requireView().findViewById<TextView>(R.id.textViewPacksInOrder)

        val numPiP = mainAct.order.imageOrderList.size.toString()
        val numPiO = Order.ordersArray.size.toString()


        var orderContent : String = ""

        Order.ordersArray.forEach { order ->

            orderContent = orderContent + order.indexInPacket + "/" + order.countOfPacket + " " + order.imageFormat?.name + "::" + order.materialPhoto?.name + "\n"

        }

        val photosInPackText = resources.getString(R.string.photosInPack)
        val packsInOrderText = resources.getString(R.string.packsInOrder)

        photosInPack.text = numPiP +  photosInPackText
        packsInOrder.text = orderContent

    }
/*
    private fun getSpinnerListener() : AdapterView.OnItemSelectedListener{
        return object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?,
                selectedItemPosition: Int,
                selectedId: Long
            ) {

                fillSpinner(selectedItemPosition)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    fun fillSpinner(selectedItemPosition : Int) {

        mainAct.order.imageFormat = availableImageFormats[selectedItemPosition]

        val toast = Toast.makeText( context
            ,"Ваш выбор: " + mainAct.order.imageFormat!!.name
            , Toast.LENGTH_SHORT)
        toast.show()
    }

 */

    fun afterTabSelected(tab: TabLayout.Tab){

        val currentMaterialPhoto =  MaterialPhoto.materialsPhoto.find {
            it.uid == tab.tag.toString()
        }

        mainAct.order.materialPhoto     = currentMaterialPhoto

        when (availableImageFormats.size ) {
            0 -> {

                //  mainAct.log.add("ImageFormat.imageFormats = " + ImageFormat.imageFormats.size )

                return
            }
        }

        val spinnerFormat           = requireView().findViewById<Spinner>(R.id.spinnerFormat)
        val spinnerAdapter          = getSpinnerFormatAdapter(requireContext())
        spinnerFormat.visibility    = View.VISIBLE
        spinnerFormat.adapter       = spinnerAdapter

        spinnerFormat.post {
            spinnerFormat.onItemSelectedListener =  getSpinnerListener(mainAct, -1)
        }

        fillSpinner(0, mainAct, -1)
    }

    private fun addPackToOrder(){

        Order.ordersArray.add(mainAct.order)

        Order.updateIndices()

        //blankOrderData()

        setQtyText()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val root    = inflater.inflate(R.layout.fragment_photos, container, false)
        mainAct     = context as MainActivity

        if(mainAct.auth.currentUser != null){
            //If user is signed in
            this.requireActivity().title  = resources.getString(R.string.app_name)  + ' '  + resources.getString(
                    R.string.ffor
            )  + ' ' + mainAct.auth.currentUser!!.displayName as CharSequence
        }

        val loadButton      = root.findViewById<Button>(R.id.buttonLoadPicture)

        val cropButton      = root.findViewById<Button>(R.id.buttonCropPicture)
        val sendButton      = root.findViewById<Button>(R.id.buttonSendPictures)
        val addOrderButton  = root.findViewById<Button>(R.id.buttonAddOrder)
            listView        = root.findViewById(R.id.list)
        val tabLayout       = root.findViewById<TabLayout>(R.id.tabLayout)

        val selectButton    = root.findViewById<ExtendedFloatingActionButton>(R.id.floatSelect)
        val sendFButton     = root.findViewById<ExtendedFloatingActionButton>(R.id.floatSend)
        //  val ordersButton = root.findViewById<Button>(R.id.buttonGetOrders)
        //val resultTextView      = root.findViewById<TextView>(R.id.textViewResult)

        tabLayout.tabGravity    = TabLayout.GRAVITY_FILL

        MaterialPhoto.materialsPhoto.forEach {

            val matTab  = tabLayout.newTab()
            matTab.id   = it.hash
            matTab.tag  = it.uid
            matTab.text = it.name

            tabLayout.addTab(matTab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                afterTabSelected(tab)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                //afterTabselected(tab)
            }
        }
        )

        loadButton.setOnClickListener{

           //val intent  = Intent(Intent.ACTION_GET_CONTENT)

            val spinnerFormat       = requireView().findViewById<Spinner>(R.id.spinnerFormat)

            spinnerFormat.visibility = View.GONE

            val intent  = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*" //allows any image file type. Change * to specific extension to limit it

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

           startActivityForResult(
                   Intent.createChooser(intent, resources.getString(R.string.selectPic)),
                   SELECT_PICTURES
           )
        }

        selectButton.setOnClickListener {

            val spinnerFormat       = requireView().findViewById<Spinner>(R.id.spinnerFormat)

            spinnerFormat.visibility = View.GONE

            val intent  = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" //allows any image file type. Change * to specific extension to limit it

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            intent.setFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )

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
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, id ->

                    when(Order.ordersArray.size){
                        0 -> addPackToOrder()
                    }

                  //  Order.sendAll()

                   // blankOrderData()

                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }

            val alert = builder.create()

            alert.show()
        }

        sendFButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

           // val progressBar             = mainAct.progressBar
           // progressBar.visibility  = ProgressBar.VISIBLE
            //progressBar.isIndeterminate = false

            builder.setMessage(resources.getString(R.string.send_photos) + "?")
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, id ->

                    when(Order.ordersArray.size){
                        0 -> addPackToOrder()
                    }

                    dialog.dismiss()

                    //Order.sendAll()

                   // when (this.indexInPacket == this.countOfPacket ) {
                   //     true -> {

                            val bundle = Bundle()

                            bundle.putBoolean("sendorder"   , true)
                           // bundle.putString("orderName"    , name)
                            //bundle.putString("orderStatus"  , orderStatus)
                            //bundle.putString("orderUuid"    , uuid)

                            mainAct.findNavController(R.id.fragment).navigate(R.id.orderFragment, bundle)

                        //}
                  //  }


                //    progressBar.visibility  = ProgressBar.GONE
                //    progressBar.isIndeterminate = true

                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }

            val alert = builder.create()

            alert.show()
        }

        addOrderButton.setOnClickListener {

            addPackToOrder()

        }

        listView.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, i: Int, l: Long ->

            val uri = listView.getItemAtPosition(i) as Uri

            CropImage.activity(uri)
                .setAllowRotation(true)
                .setAspectRatio(3, 4)
                .setCropMenuCropButtonTitle(resources.getString(R.string.crop))
                .setActivityTitle(resources.getString(R.string.crop))
                .start(requireContext(), this)

            croppingPosition = i

        }

        return root
    }

    private fun getFormatsByMaterialThread(materialUid: String): Thread{

        return Thread{
            //viewPager.currentItem = tab.position
            val dl                  = DataLoader()
            var res                 = dl.getFormatsByMaterial(materialUid)
            var resJarray           = JSONArray()

            try {

                resJarray           = JSONArray(res)

            } catch (e: Exception) {


                e.printStackTrace()

                res = e.toString()

                mainAct.log.add("getFormatsByMaterialThread = $res")

                //return
            }

            availableImageFormats   = ArrayList()

            //mainAct.log.add("getFormatsByMaterialThread = $res")

            val resArray : ArrayList<String> = ArrayList(resJarray.length())

            for (j in 0 until resJarray.length())
                //resArray.add(resJarray.getString(j))
                resArray.add( (JSONArray(res)[j] as JSONObject).getString("uuid"))

            // заполенние доступных форматов и цен
            ImageFormat.imageFormats.forEach{ imageFormat ->

                when (val uuidIndex = resArray.indexOf(imageFormat.uid)){
                    -1 -> {
                    }
                    else -> {
                        imageFormat.price =  BigDecimal((JSONArray(res)[uuidIndex] as JSONObject).getString("price"))

                        ///imageFormat.name = imageFormat.name + "(" + imageFormat.price + "₽)"

                        availableImageFormats.add(imageFormat)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        when (MaterialPhoto.materialsPhoto.size){
            0 -> {

                val messsage = resources.getString(R.string.netTrouble)

                mainAct.log.add(messsage + DataLoader.res)

            }
            else -> {

                mainAct.order.materialPhoto     = MaterialPhoto.materialsPhoto[0]
                mainAct.progressBar.visibility  = ProgressBar.VISIBLE

                val getFormatsByMaterialThread  = getFormatsByMaterialThread(MaterialPhoto.materialsPhoto[0].uid)

                    getFormatsByMaterialThread.start()
                    getFormatsByMaterialThread.join()

                mainAct.progressBar.visibility = ProgressBar.GONE

                val tabLayout   = requireView().findViewById<TabLayout>(R.id.tabLayout)
                val tab0        = tabLayout.getTabAt(0)

                tab0?.select()

                tab0?.let {
                    afterTabSelected(it)
                }

                setQtyText()

                when (availableImageFormats.size){
                    0 -> {
                        var warning = view.findViewById<TextView>(R.id.textViewResult)

                        warning.text = mainAct.log[mainAct.log.size - 1]
                    }
                }
            }
        }

        mainAct.order.imageOrderList.forEach {

            imageUriList.add(it.imageUri!!)

        }

        updateList()
    }

    private fun fillPhotosList(): List<String> {

        val data = mutableListOf<String>()

        imageUriList.forEach { uri ->

            data.add("$uri element")
        }
        return data
    }

    fun updateList(){

        //listView.adapter                    = PhotoListAdapter(requireActivity(), imageUriList)

        val photosRecyclerView                  = requireView().findViewById<RecyclerView>(R.id.photosRecyclerView)
            photosRecyclerView.layoutManager    = LinearLayoutManager(requireContext())
        val adapter                             = PhotosRecyclerViewAdapter(imageUriList, this)
            photosRecyclerView.adapter          = adapter

        val callback: ItemTouchHelper.Callback  = SwipeHelperCallback(adapter)
        val touchHelper                         = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(photosRecyclerView)

    }

    private fun insertUriToListView(resultUri: Uri) {

      //  requireActivity().contentResolver.takePersistableUriPermission(resultUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

// если добавление нового фото
        if (croppingPosition == -1) {

            val imageOrder                  = ImageOrder(resultUri, resultUri.toString())
                imageOrder.imageFormat      = mainAct.order.imageFormat
                imageOrder.materialPhoto    = mainAct.order.materialPhoto

            mainAct.order.imageOrderList.add(imageOrder)
            imageUriList.add(resultUri)
        }
// если редактирование ранее добавленного
        else {
            imageUriList[croppingPosition]                          = resultUri
            mainAct.order.imageOrderList[croppingPosition].imageUri = resultUri
            croppingPosition                                        = -1
        }

        updateList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
// если фото получено из редактора
            val result = CropImage.getActivityResult(data)

            if (resultCode == AppCompatActivity.RESULT_OK) {

     //           requireActivity().contentResolver.takePersistableUriPermission(result!!.uri,
       //             Intent.FLAG_GRANT_READ_URI_PERMISSION);

                insertUriToListView(result!!.uri)

                setQtyText()

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
            }
        } else
// если фото получено из галереи
            if(requestCode == SELECT_PICTURES) {
                if(resultCode == RESULT_OK) {
                    if(data!!.clipData == null) {
                        if (data.data != null) {
//----------------------------------------------------------------------------
 //                           requireActivity().contentResolver.takePersistableUriPermission(data.data!!,
   //                             Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            insertUriToListView(data.data!!)
                            setQtyText()
//----------------------------------------------------------------------------
                        }
                    }
                    else  {
// если было выбрано много фото
                        val count = data.clipData!!.itemCount

                        for( ind in 0 until count) {
//----------------------------------------------------------------------------
                            var uri = data.clipData!!.getItemAt(ind).uri

     //                       requireActivity().contentResolver.takePersistableUriPermission(
       //                         uri,
         //                       Intent.FLAG_GRANT_READ_URI_PERMISSION
                            //)

                            insertUriToListView(uri)
//----------------------------------------------------------------------------
                        }
                        setQtyText()

                        //String imagePath = data.getData().getPath();
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    }//if(data!!.clipData == null)
                }//if(resultCode == RESULT_OK)
            } //if(requestCode == SELECT_PICTURES)
    }//override fun onActivityResult

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
        @JvmStatic
        var availableImageFormats   : MutableList<ImageFormat?>  = ArrayList()

        @JvmStatic
        fun getSpinnerFormatAdapter(context: Context):ArrayAdapter<String>{

            val arrNames : Array<String> = Array(availableImageFormats.size) { index ->
                availableImageFormats[index]!!.name
            }

            val adapter : ArrayAdapter<String> = ArrayAdapter<String>(
                    //   context, R.layout.support_simple_spinner_dropdown_item, arrNames
                    context, R.layout.spinner_formats, R.id.nameFormat, arrNames
            )

           // adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            adapter.setDropDownViewResource(R.layout.spinner_formats)

            return adapter
        }

        @JvmStatic
         fun getSpinnerListener(mainAct: MainActivity, imageListPosition: Int) : AdapterView.OnItemSelectedListener{
            return object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        itemSelected: View?,
                        selectedItemPosition: Int,
                        selectedId: Long
                ) {

                    fillSpinner(selectedItemPosition, mainAct, imageListPosition)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

        @JvmStatic
        fun fillSpinner(selectedItemPosition: Int, mainAct: MainActivity, imageListPosition: Int) {

            when (imageListPosition) {
                // переключение табов
                -1 -> {

                    mainAct.order.imageFormat = availableImageFormats[selectedItemPosition]
                    mainAct.order.imageFormat!!.index = selectedItemPosition
                }
                //  без фото
                0 -> {
                    when (mainAct.order.imageOrderList.size) {
                        0 -> {
                            mainAct.order.imageFormat = availableImageFormats[selectedItemPosition]
                            mainAct.order.imageFormat!!.index = selectedItemPosition
                        }
                        else -> {
                            //mainAct.order.imageOrderList[mainAct.order.imageOrderList.size-1].imageFormat         = availableImageFormats[selectedItemPosition]
                            //mainAct.order.imageOrderList[imageListPosition].imageFormat!!.index = selectedItemPosition
                            //mainAct.order.imageOrderList[mainAct.order.imageOrderList.size-1].imageFormat!!.index = selectedItemPosition
                            mainAct.order.imageOrderList[imageListPosition].imageFormat =
                                    availableImageFormats[selectedItemPosition]
                            mainAct.order.imageOrderList[imageListPosition].imageFormat!!.index =
                                    selectedItemPosition
                            //--- MATERIAL
                            mainAct.order.imageOrderList[imageListPosition].materialPhoto =
                                    mainAct.order.materialPhoto

                        }
                    }
                }
                // с фото
                else ->{
                    mainAct.order.imageOrderList[imageListPosition].imageFormat         = availableImageFormats[selectedItemPosition]
                    mainAct.order.imageOrderList[imageListPosition].imageFormat!!.index = selectedItemPosition
                    //mainAct.order.imageOrderList[imageListPosition].imageFormat!!.index = imageListPosition
                }
            }
/*
            val toast = Toast.makeText( mainAct
                ,"Ваш выбор: " + mainAct.order.imageFormat!!.name + " для фото " + imageListPosition.toString()
                , Toast.LENGTH_SHORT)
            toast.show()

 */
        }

    }
}