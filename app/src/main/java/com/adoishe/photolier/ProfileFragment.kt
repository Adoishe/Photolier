package com.adoishe.photolier

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

            val auth    = FirebaseAuth.getInstance()
//    private val mainAct = context as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root                        = inflater.inflate(R.layout.fragment_profile, container, false)
        val saveProfileButton : Button   = root.findViewById(R.id.buttonSaveProfile)
        val profileView                 = root.findViewById<RecyclerView>(R.id.profileRecyclerView)
            profileView.layoutManager   = LinearLayoutManager(requireContext())
            profileView.adapter         = CustomRecyclerAdapter(fillList())

        saveProfileButton.setOnClickListener {

            //view?.findNavController()?.navigate(R.id.ordersHistoryFragment)
            val profile = Profile()

            profile.phoneNumber = 343434

            profile.load()

        }


        return root
    }


    private fun fillList(): List<ContentValues> {

        val data = mutableListOf<ContentValues>()

        val displayName = ContentValues()
        val email = ContentValues()
        val uidCV = ContentValues()

        displayName.put("Key", resources.getString(R.string.display_name))
        displayName.put("Value", auth.currentUser?.displayName.toString())

        email.put("Key", resources.getString(R.string.email))
        email.put("Value", auth.currentUser?.email.toString())

        uidCV.put("Key", resources.getString(R.string.uid))
        uidCV.put("Value", auth.currentUser?.uid.toString())


        data.add(displayName)
        data.add(email)
        data.add(uidCV)

        return data
    }

    class CustomRecyclerAdapter(private val values: List<ContentValues>) : RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

        override fun getItemCount() = values.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_item, parent, false)

            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.largeTextView?.text = (values[position] as ContentValues).getAsString("Value")
            holder.smallTextView?.text =  (values[position] as ContentValues).getAsString("Key")
        }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var largeTextView: TextView? = null
            var smallTextView: TextView? = null

            init {
                largeTextView = itemView.findViewById(R.id.textViewLarge)
                smallTextView = itemView.findViewById(R.id.textViewSmall)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}