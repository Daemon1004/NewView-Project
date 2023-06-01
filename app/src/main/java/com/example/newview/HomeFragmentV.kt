package com.example.newview

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        val database = myActivity.database

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

        if (userData.status != null) {
            view.findViewById<TextView>(R.id.Status).text =
                if (userData.status == true) {
                    resources.getString(R.string.Online)
                } else {
                    resources.getString(R.string.DontDisturb)
                }


            fun updateStatusName() {
                view.findViewById<TextView>(R.id.Status).text =
                    if (userData.status == true) {
                        resources.getString(R.string.Online)
                    } else {
                        resources.getString(R.string.DontDisturb)
                    }
            }

            updateStatusName()

            view.findViewById<Button>(R.id.ChangeStatusButton).setOnClickListener {
                userData.status = !userData.status!!
                updateStatusName()
                database.child("users").child(auth.uid!!).child("status").setValue(userData.status)
                Log.i("firebase", "Write new status: " + userData.status)
                Toast.makeText(
                    myActivity.applicationContext,
                    resources.getString(R.string.StatusChanged),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun getDate(timestamp: Long) :String {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }
}