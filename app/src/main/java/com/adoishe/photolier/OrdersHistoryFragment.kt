package com.adoishe.photolier

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val mainAct    = (requireActivity() as MainActivity)

    var orders : MutableList<Order> = ArrayList()
    private var prg: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun getOrders(view: View) : Thread{

         return Thread {

             // val mainAct    = (requireActivity() as MainActivity)
             orders         = ArrayList()

             mainAct.log.add("gwt orders thread started")

            var result : String
            val dl = DataLoader()

             mainAct.log.add("getOrders requested")
             mainAct.log.add("uid = " + mainAct.auth.currentUser!!.uid)

            val sendResult = dl.getOrders(mainAct.auth.currentUser!!.uid)

            mainAct.log.add(sendResult)

            try {

                val arrayCV  = JSONArray(sendResult)

                if (arrayCV.length() != 0){

                    for (i in 0 until arrayCV.length()) {

                        val order1c         = Order(requireActivity())
                        val orderItem       = arrayCV.getJSONObject(i)
                        order1c.name        = orderItem.getJSONObject("mValues").getString("orderName")
                        order1c.text        = orderItem.getJSONObject("mValues").getString("orderText")

                        order1c.setUuid(orderItem.getJSONObject("mValues").getString("orderUuid"))

                        val uriJSONArray    = JSONArray(
                                                            orderItem.getJSONObject("mValues").get("imageUriList").toString()
                                                        )
                        val orderStatus     = orderItem.getJSONObject("mValues").getString("orderStatus")
                        val imageUriList    : MutableList<Uri>      = ArrayList()
                        val imageBase64List : MutableList<String>   = ArrayList()

                        for (j in 0 until uriJSONArray.length()){

                            val jsonObject      = JSONObject(uriJSONArray.getString(j))
                            val uri1c           = Uri.parse( jsonObject.get("imageUri").toString())
                            val thumbB64String  = jsonObject.get("thumbB64String").toString()

                            imageUriList.add(uri1c)
                            imageBase64List.add(thumbB64String)

                        }

                        order1c.imageUriList        = imageUriList
                        order1c.imageBase64List     = imageBase64List
                        order1c.orderStatus         = orderStatus


                        orders.add(order1c)
                    }
                }

                result = orders.toString()

               //mainAct.log.add(result)

            }
            catch (e: Exception) {

                result = sendResult.toString()

                mainAct.log.add(result)

            }

             //prg.visibility = ProgressBar.INVISIBLE
             //progressBar.visibility  = ProgressBar.INVISIBLE
        }
    }



/*
    private fun generateImageView(base64: String): ImageView {

        val imageView = ImageView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = params

       //val inStream : InputStream? = requireActivity().contentResolver.openInputStream(uri)
        //val bitmap = BitmapFactory.decodeStream(inStream)

        //imageView.setImageBitmap(bitmap)
        //imageView.setImageURI(uri)
        //imageView.setImageResource(R.drawable.ic_launcher_foreground)

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        Glide
            .with(requireContext())
            .load(base64)
            .apply(RequestOptions().override(30, 40))
            .into(imageView)

        return imageView
    }

 */

    private fun createCardView(view: View){

        // Add an ImageView to the CardView
        val historyLayout = view.findViewById<LinearLayout>(R.id.history_layout)

            // Initialize a new CardView instance
        orders.forEach { order ->


            val cardView    = CardView(requireContext())
            val orderIndex  = orders.indexOf(order)
            cardView.id     = orderIndex

            // Initialize a new LayoutParams instance, CardView width and height
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
            )

            // Set margins for card view
            layoutParams.setMargins(20, 20, 20, 20)
            // Set the card view layout params
            cardView.layoutParams = layoutParams
            // Set the card view corner radius
            cardView.radius = 16F
            // Set the card view content padding
            cardView.setContentPadding(25, 25, 25, 25)
            // Set the card view background color
            cardView.setCardBackgroundColor(Color.LTGRAY)
            // Set card view elevation
            cardView.cardElevation = 8F
            // Set card view maximum elevation
            cardView.maxCardElevation = 12F
            // Set a click listener for card view

            val textView = mainAct.generateTextView(
                order.name + "\n" + order.orderStatus
                    //, cardView
            )

            cardView.addView(textView)

            order.imageBase64List.forEach {

                when(it){

                    ""      -> {

                    }
                    else    -> {
                        cardView.addView((requireContext() as MainActivity ).generateImageView(it))
                    }
                }

            }



//            cardView.addView(generateImageView(order.imageUriList[0]))


            cardView.setOnClickListener{

                val order1c = orders[it.id]
                val bundle = Bundle()

                bundle.putString    ("orderUuid"    , order1c.getUuid())
                bundle.putInt       ("orderId"      , it.id)
                bundle.putString    ("orderName"    , order1c.name)
                bundle.putString    ("orderText"    , order1c.text)
                bundle.putBoolean   ("ordersHistory", true)

                view.findNavController().navigate(
                    R.id.action_ordersHistoryFragment_to_orderFragment,
                    bundle
                )

                /*
                            Toast.makeText(
                                requireContext(),
                                "Card clicked.",
                                Toast.LENGTH_SHORT
                            ).show()

                 */
            }

            // Finally, add the CardView in root layout
            historyLayout?.addView(cardView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainAct         = (requireActivity() as MainActivity)
        val getOrdersThread = getOrders(view)

        getOrdersThread.start()
        getOrdersThread.join()

        mainAct.progressBar.visibility  = View.GONE

        createCardView(view)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root    =  inflater.inflate(R.layout.fragment_orders_history, container, false)


        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        val REQUEST_CODE = 333


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}