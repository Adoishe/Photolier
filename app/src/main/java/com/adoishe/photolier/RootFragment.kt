package com.adoishe.photolier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class RootFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root                        = inflater.inflate(R.layout.fragment_root, container, false)
        val goToPhotoButton : Button    = root.findViewById(R.id.goToPhotobutton)
        val ordersButton    : Button    = root.findViewById(R.id.buttonGetOrders)
        val logView                     = root.findViewById<EditText>(R.id.log)
        val atuhButton                  = root.findViewById<Button>(R.id.auth)
        val syncButton                  = root.findViewById<Button>(R.id.sync)

        //ImageFormat.sync(requireContext())
        //MaterialPhoto.sync(requireContext())

        goToPhotoButton.setOnClickListener{

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_photosFragment)
        }

        ordersButton.setOnClickListener {

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_ordersHistoryFragment)
        }

        logView.setOnClickListener {

            logView.setText((requireActivity() as MainActivity).log.joinToString("\n"))
        }

        atuhButton.setOnClickListener{

            view?.findNavController()?.navigate(R.id.action_rootFragment_to_profileFragment)
        }
        syncButton.setOnClickListener{

            var jImageFormat = ImageFormat.sync(requireContext())
            var jMaterialPhoto = MaterialPhoto.sync(requireContext())
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = (requireContext() as MainActivity). findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = ProgressBar.VISIBLE

        ImageFormat.sync(requireContext() )
        //    log.add("ImageFormat = ")
        MaterialPhoto.sync(requireContext() )
        //  log.add("MaterialPhoto = ")

        progressBar.visibility = ProgressBar.INVISIBLE

    }

}