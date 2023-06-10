package com.example.newview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragmentB : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myActivity = (activity as MainActivity?)!!
        lateinit var userData: UserData
        try {
            userData = myActivity.userData
        } catch (e: UninitializedPropertyAccessException) {
            return
        }

        view.findViewById<Button>(R.id.CallVolunteerButton).setOnClickListener {



        }

    }
}