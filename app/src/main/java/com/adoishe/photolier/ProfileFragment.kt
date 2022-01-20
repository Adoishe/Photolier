package com.adoishe.photolier

import android.content.ContentValues
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

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

    private fun fillList(view: View){

        val profileView             = view.findViewById<RecyclerView>(R.id.profileRecyclerView)

        profileView.layoutManager   = LinearLayoutManager(requireContext())
        profileView.adapter         = CustomRecyclerAdapter(getProfileData())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root                            = inflater.inflate(R.layout.fragment_profile, container, false)
        val saveProfileButton : Button      = root.findViewById(R.id.buttonSaveProfile)
        val reloadProfileButton : Button    = root.findViewById(R.id.buttonReloadProfile)
        /*
        val profileView                 = root.findViewById<RecyclerView>(R.id.profileRecyclerView)
            profileView.layoutManager   = LinearLayoutManager(requireContext())
            profileView.adapter         = CustomRecyclerAdapter(getProfileData())

         */

        //Profile.load(auth.currentUser!!.uid)

        fillList(root)

        saveProfileButton.setOnClickListener {

            Profile.profile.save()

        }


        reloadProfileButton.setOnClickListener {

            fillList(requireView())

        }


        return root
    }


    private fun getProfileData(): List<ContentValues> {


       // Profile.load(auth.currentUser!!.uid)



        val data = mutableListOf<ContentValues>()

        val displayName = ContentValues()
        val email       = ContentValues()
        val uidCV       = ContentValues()
        val addrrCV     = ContentValues()
        val phoneCV     = ContentValues()

        displayName.put("Key"   , resources.getString(R.string.display_name))
        displayName.put("Value" , Profile.profile.displayName)

        email.put("Key"     , resources.getString(R.string.email))
        email.put("Value"   , Profile.profile.email)


        addrrCV.put("Key"   , "Addresses")
        addrrCV.put("Value" , Profile.profile.postalAddresses.toString())

        phoneCV.put("Key"   , "Phone")
        phoneCV.put("Value" , Profile.profile.phoneNumber)

        uidCV.put("Key"     , resources.getString(R.string.uid))
        uidCV.put("Value"   , Profile.profile.uid)


        data.add(displayName)
        data.add(email)
        data.add(phoneCV)
        data.add(addrrCV)
        data.add(uidCV)

        return data
    }

    class CustomRecyclerAdapter(private val values: List<ContentValues>) : RecyclerView.Adapter<CustomRecyclerAdapter.ProfileViewViewHolder>() {

        override fun getItemCount() = values.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_item, parent, false)

            return ProfileViewViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ProfileViewViewHolder, position: Int) {

            val key     = (values[position] as ContentValues).getAsString("Key")
            val value   = (values[position] as ContentValues).getAsString("Value")

            holder.largeTextView?.setText(value)

            holder.largeTextView?.setOnFocusChangeListener { view, hasFocus ->

                if (!hasFocus) {

                    val keyFromTag = holder.largeTextView!!.tag

                    when (keyFromTag){
                        "E-mail"    -> Profile.profile.email  =  holder.largeTextView!!.text.toString()
                        "Phone"     -> {
                                        val phoneAsString       = holder.largeTextView!!.text.toString()
                                        val phoneAsFormatted    = PhoneNumberUtils.formatNumber(phoneAsString,  Locale.getDefault().country)

                                        holder.largeTextView!!.setText(phoneAsFormatted)

                                        Profile.profile.phoneNumber  = phoneAsFormatted
                                        Profile.profile.save()
                        }
                        //"Addresses" -> Profile.profile.postalAddresses =  holder.largeTextView!!.text.toString()

                    }
                }
            }





            holder.largeTextView?.setOnKeyListener { v, keyCode, event ->
                // if the event is a key down event on the enter button
                if (event.action == KeyEvent.ACTION_DOWN
                    //&&
                    //keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // perform action on key press

                    val phoneAsString       = holder.largeTextView!!.text.toString()



                    return@setOnKeyListener phoneAsString.length > 15
                }
                return@setOnKeyListener false

            }




            when (key){
                "E-mail"    -> holder.largeTextView?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                "Phone"     -> {
                                holder.largeTextView?.inputType = InputType.TYPE_CLASS_PHONE

                                holder.largeTextView?.addTextChangedListener(
                                    PhoneNumberFormattingTextWatcher(Locale.getDefault().country)
                                )
                }
                "ID"        -> holder.largeTextView?.keyListener = null
                "Addresses" -> holder.largeTextView?.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE

            }



            holder.largeTextView?.tag   = key
            holder.smallTextView?.text  = key

        }

        class ProfileViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var largeTextView: EditText? = null
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