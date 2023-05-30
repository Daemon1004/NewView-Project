package com.example.newview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentV : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_v, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myActivity = (activity as MainActivity?)!!
        val userData = myActivity.userData
        val auth = myActivity.auth

        val firstName = userData.firstname ?: ""
        val lastName = userData.lastname ?: ""

        view.findViewById<TextView>(R.id.UserFullname).text = if (firstName != "" || lastName != "") {
            firstName + (if (firstName == "" || lastName == "") { "" } else { " " }) + lastName
        } else {
            auth.currentUser?.displayName
        }

        view.findViewById<TextView>(R.id.UserRegDate).text =
            resources.getString(R.string.ParticipantSince) + " " +
                    if (userData.since != null) { getDate(userData.since!!) } else { "?" }

    }

    fun getDate(timestamp: Long) :String {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }
}