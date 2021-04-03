package com.adoishe.photolier

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
import org.json.JSONArray


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

             val mainAct = (requireActivity() as MainActivity)

            //prg.visibility = ProgressBar.VISIBLE


             orders = ArrayList()

             mainAct.log.add("gwt orders thread started")

            var result : String
            val dl = DataLoader()

             mainAct.log.add("getOrders rquested")
             mainAct.log.add("uid = " + (context as MainActivity).auth.currentUser!!.uid)

            val sendResult = dl.getOrders((context as MainActivity).auth.currentUser!!.uid)

             mainAct.log.add(sendResult)

            try {

                val arrayCV  = JSONArray(sendResult)

                if (arrayCV.length() !=0){

                    for (i in 0 until arrayCV.length()) {

                        val order1c         = Order(requireActivity())
                        val orderItem       = arrayCV.getJSONObject(i)
                        order1c.name        = orderItem.getJSONObject("mValues").getString("orderName")
                        order1c.text        = orderItem.getJSONObject("mValues").getString("orderText")
                        //val uriJSONArray    = orderItem.getJSONObject("mValues").getJSONArray("imageUriList")
                        val orderStatus     = orderItem.getJSONObject("mValues").getString("orderStatus")
                        val imageUriList    : MutableList<Uri>  = ArrayList()
                        /*
                        for (j in 0 until uriJSONArray.length()){

                            val uri1c = Uri.parse( uriJSONArray.getString(j))

                            imageUriList.add(uri1c)

                        }

                         */

                        order1c.imageUriList = imageUriList
                        order1c.orderStatus = orderStatus

                        orders.add(order1c)
                    }
                }

                result = orders.toString()

                mainAct.log.add(result)

            }
            catch (e: Exception) {

                result = sendResult.toString()

                mainAct.log.add(result)

            }

             //prg.visibility = ProgressBar.INVISIBLE
             //progressBar.visibility  = ProgressBar.INVISIBLE
        }
    }

    private fun generateTextView(
        string: String
        //, orderIndex : Int
        , view: View
    ): TextView {

        val textView            = TextView(requireContext())
        val params              = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams   = params
        textView.text           = string
        //textView.id             = orderIndex


        return textView
    }

    private fun generateImageView(uri: Uri): ImageView {

        val imageView = ImageView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = params

        imageView.setImageURI(uri)//setImageResource(R.drawable.hungrycat)

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
/*
        Glide
            .with(requireContext())
            .load(uri)
            .apply(RequestOptions().override(30, 40))
            .into(imageView)

 */

        return imageView
    }

    private fun createCardView(view: View){

        // Add an ImageView to the CardView
        val historyLayout = view.findViewById<LinearLayout>(R.id.history_layout)

            // Initialize a new CardView instance
        orders.forEach { order ->

            val cardView    = CardView(requireContext())
            var orderIndex  = orders.indexOf(order)
            cardView.id     = orderIndex

            // Initialize a new LayoutParams instance, CardView width and height
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
            )
/*
            if (orderIndex == 0){

                layoutParams.topToTop = ConstraintSet.PARENT_ID

            }else{
                layoutParams.topToBottom = orderIndex
            }
 */

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
        /*
            cardView.setOnClickListener{

                var order = orders[it.id]

                val bundle = Bundle()
                bundle.putString("orderUuid", order.getUuid())
                bundle.putString("orderName", order.name)
                //bundle.putInt("arg2", 2)

                view.findNavController().navigate(R.id.action_ordersHistoryFragment_to_orderFragment , bundle)

            }

         */


            val textView = generateTextView(
                order.text
                /*order.name
                                            + "\n "
                                            + order.imageUriList.size
                                            + " "
                                            + resources.getString(R.string.photos)
                                            + " "
                                            + order.orderStatus
                                            // , orders.indexOf(order)

                                     */, cardView
            )

            cardView.addView(textView)


            cardView.setOnClickListener{

                val order = orders[it.id]
                val bundle = Bundle()

                bundle.putString("orderUuid", order.getUuid())
                bundle.putString("orderName", order.name)
                //bundle.putInt("arg2", 2)

                view?.findNavController().navigate(
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
            //cardView.addView(generateTextView(order.name))

                /*
                order.imageUriList.forEach {

                    cardView.addView(generateImageView(it))

                }

                 */


        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainAct = (requireActivity() as MainActivity)

         prg         = mainAct.findViewById<ProgressBar>(R.id.progressBar)
/*        prg!!.bringToFront()
        prg!!.visibility  = View.VISIBLE


 */
        val getOrdersThread = getOrders(view)




        getOrdersThread.start()
        getOrdersThread.join()
        prg!!.visibility  = View.GONE
        createCardView(view)




        try {
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

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
        fun newInstance(param1: String, param2: String) =
            OrdersHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}