package com.example.newview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentV : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_v, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myActivity = (activity as MainActivity?)!!
        lateinit var userData: UserData
        try {
            userData = myActivity.userData
        } catch (e: UninitializedPropertyAccessException) {
            return
        }
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
                database.child("users").child(auth.uid!!).child("status").setValue(userData.status).addOnSuccessListener {
                    Log.i("firebase", "Write new status: " + userData.status)
                    Toast.makeText(
                        myActivity.applicationContext,
                        resources.getString(R.string.StatusChanged),
                        Toast.LENGTH_SHORT
                    ).show()
                    updateStatusName()
                    myActivity.reloadService()
                }
            }
        }

        view.findViewById<Button>(R.id.TutorialButton).setOnClickListener {
            val intent = Intent(context, TutorialActivity::class.java)
            startActivity(intent)
        }

        //users count
        database.child("users").orderByChild("isblind").equalTo(false)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    view.findViewById<TextView>(R.id.VCount).text = dataSnapshot.childrenCount.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error (volunteers count)")
                }
            })
        database.child("users").orderByChild("isblind").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    view.findViewById<TextView>(R.id.BCount).text = dataSnapshot.childrenCount.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error (blind users count)")
                }
            })

    }

    private fun getDate(timestamp: Long) :String {
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }
}