package com.adoishe.photolier

import android.content.ContentValues
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GetSizeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GetSizeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: Int? = null
    private var uid: String? = null
    //val mainAct    = requireActivity() as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id")
            uid = it.getString("uid")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_size, container, false)
    }

    /*
    private fun createCardView(view: View) {

        // Add an ImageView to the CardView
        val frameLayout = view.findViewById<LinearLayout>(R.id.imageFormat_layout)

        // Initialize a new CardView instance
        ImageFormat.imageFormats.forEach { imageFormat ->



            val cardView = CardView(requireContext())
            val imageFormatIndex = ImageFormat.imageFormats.indexOf(imageFormat)
            cardView.id = imageFormatIndex

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
            val mainAct    = requireActivity() as MainActivity
            val textView = mainAct.generateTextView(
                imageFormat.name + "\n" + imageFormat.price
                //, cardView
            )



            //Toast.makeText(context, imageFormat.name + "\n" + imageFormat.price, Toast.LENGTH_SHORT).show()

            cardView.addView(textView)


//            cardView.addView(generateImageView(order.imageUriList[0]))
            cardView.setOnClickListener {

                val imageFormat = ImageFormat.imageFormats[it.id]
                val bundle = Bundle()


                bundle.putInt("imageFormatId", it.id)
                bundle.putString("imageFormatName", imageFormat.name)
                //bundle.putB("imageFormatPrice", imageFormat.price)
                //bundle.putBoolean("ordersHistory", true)

                /*
                view.findNavController().navigate(
                    R.id.action_ordersHistoryFragment_to_orderFragment,
                    bundle
                )
                */


            }
            // Finally, add the CardView in root layout
            frameLayout?.addView(cardView)
        }
    }

    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val mainAct                                     = (requireActivity() as MainActivity)
            mainAct.order.materialPhoto                 = MaterialPhoto.materialsPhoto[this.id as Int]

        (mainAct.order.materialPhoto  as MaterialPhoto).indexInArray    = this.id as Int

        mainAct.progressBar.visibility  = ProgressBar.VISIBLE

        val getFormatsByMaterialThread      = mainAct.getFormatsByMaterialThread(this.uid as String)

        getFormatsByMaterialThread.start()
        getFormatsByMaterialThread.join()

        mainAct.progressBar.visibility                  = ProgressBar.GONE
        mainAct.progressBar.visibility                  = View.GONE
        val cvArrayList : MutableList<ContentValues>    = ArrayList()

        mainAct.availableImageFormats.forEach { (it as ImageFormat)

                cvArrayList.add( it.toCv() )

        }

        mainAct.createCardView(view, cvArrayList , R.id.action_getSizeFragment_to_photosFragment)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment getSizeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GetSizeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}