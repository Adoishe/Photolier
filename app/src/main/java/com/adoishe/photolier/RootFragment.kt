package com.adoishe.photolier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        var root =  inflater.inflate(R.layout.fragment_root, container, false)
 /*       var navController: NavController = Navigation.findNavController(
                                                this.requireActivity(),
                                                R.id.rootFragment
                                            )

  */

        var goToPhotoButton : Button = root.findViewById(R.id.goToPhotobutton)

        goToPhotoButton.setOnClickListener{

           // navController.navigate(R.id.action_rootFragment_to_photosFragment)
            view?.findNavController()?.navigate(R.id.action_rootFragment_to_photosFragment)

        }

        return root
    }
}