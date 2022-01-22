package com.adoishe.photolier

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.iid.FirebaseInstanceId

class RootFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainAct = (context as MainActivity)
//        mainAct.saveLog("onCreateView")

        val root                        = inflater.inflate(R.layout.fragment_root, container, false)
        val goToPhotoButton : Button    = root.findViewById(R.id.goToPhotobutton)
        val ordersButton    : Button    = root.findViewById(R.id.buttonGetOrders)
        val logView                     = root.findViewById<EditText>(R.id.log)
        val atuhButton                  = root.findViewById<Button>(R.id.auth)
        val syncButton                  = root.findViewById<Button>(R.id.sync)
        val photosPrint                 = root.findViewById<TextView>(R.id.printPhotos)
        val widePrint                   = root.findViewById<TextView>(R.id.printWide)
        val profileButton               = root.findViewById<TextView>(R.id.profileButton)

        profileButton.setOnClickListener {

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_profileFragment)

        }

        photosPrint.setOnClickListener {

            mainAct.order = Order(mainAct)

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_getMaterialFragment)
        }

        goToPhotoButton.setOnClickListener{

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_photosFragment)
        }

        ordersButton.setOnClickListener {

            //view?.findNavController()?.navigate(R.id.action_rootFragment_to_ordersHistoryFragment)
            //view?.findNavController()?.navigate(R.id.action_rootFragment_to_getMaterialFragment)
            view?.findNavController()?.navigate(R.id.action_rootFragment_to_profileFragment)

        }

        logView.setOnClickListener {

            logView.setText((requireActivity() as MainActivity).log.joinToString("\n"))
        }

        atuhButton.setOnClickListener{

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_profileFragment)
        }
        syncButton.setOnClickListener{

            sync()
        }
        return root
    }

    private fun sync() : Boolean{

        val mainAct = (context as MainActivity)

        mainAct.progressBar.visibility = ProgressBar.VISIBLE

//        Toast.makeText(context, resources.getString(R.string.sync), Toast.LENGTH_LONG).show()
        mainAct.saveLog(resources.getString(R.string.sync))
        ImageFormat.sync(mainAct)
        MaterialPhoto.sync(mainAct)

        mainAct.progressBar.visibility  = ProgressBar.INVISIBLE

        val successfully = ImageFormat.status == ImageFormat.SYNC && MaterialPhoto.status == MaterialPhoto.SYNC

        when (successfully){
            true ->{
                //Toast.makeText(context, resources.getString(R.string.sync), Toast.LENGTH_LONG).show()
                //(context as MainActivity).setTheme(R.style.Theme_Photolier)
            }

            false ->{

                mainAct.saveLog("SYNC failed")
            }
        }

        return successfully
    }

    private fun doWithIntentData(view: View){

        val mainAct = (context as MainActivity)
        val orderId = mainAct.intent.getStringExtra("orderId")

        when (orderId){
            ""   -> {}
            else -> {

                val orderText   = mainAct.intent.getStringExtra("orderText")
                val bundle      = Bundle()

                bundle.putString    ("orderUuid"        , orderId)
                bundle.putString    ("orderName"        , orderText)
                bundle.putString    ("orderText"        , orderText)
                bundle.putBoolean   ("ordersHistory"    , true)

                when(mainAct.intent.getStringExtra("messageId")){
                    "GOT_LAST_IMAGE_OF_ORDER" -> {}
                    "GOT_A_NEW_ORDER_PAYMENT" -> {

                            view.findNavController().navigate(
                                  R.id.orderFragment
                                , bundle
                                )
                    }
                }//when
            }//whenelse
        }//when
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val mainAct         = (context as MainActivity)
        //this::name.isInitialized
        when(mainAct.authIsInitialized()){
            true -> {

                val successfully = sync()

                if (successfully) {

                    Profile.load(mainAct.auth.currentUser!!.uid)

                }// if successfully
                else {

                    val printPhotos     :TextView   = requireView().findViewById(R.id.printPhotos)
                    val profileButton   :TextView   = requireView().findViewById(R.id.profileButton)

                    printPhotos.text = "Полный швах"
                    printPhotos.isEnabled = false
                    profileButton.isEnabled = false

                }//if not successfully

                if  (mainAct.intent.extras != null)
                    doWithIntentData(view)
            }

            else -> {
            }
        }


        }//onViewCreated
}