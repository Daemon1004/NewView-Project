package com.example.newview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CallBActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_b)

        val arguments = intent.extras

        database = Firebase.database.reference
        auth = Firebase.auth

        if (arguments != null) {
            @Suppress("DEPRECATION")
            userData = arguments.getSerializable("userData") as UserData
        } else {
            finish()
            return
        }

        database.child("calls").child(auth.uid!!).child("needHelp").setValue(true)

        lateinit var volunteerListener : ValueEventListener
        volunteerListener = database.child("calls").child(auth.uid!!).child("volunteer")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("firebase", "Get: $dataSnapshot")

                    val volunteer = dataSnapshot.value.toString()

                    database.child("calls").removeEventListener(volunteerListener)
                    if (volunteer != "null") { call(volunteer) }

                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error")
                }
            })

    }

    private fun call(volunteer : String) {
        val me = auth.uid
        findViewById<CardView>(R.id.CardCallStatus).visibility = CardView.INVISIBLE



    }

    override fun onPause() {
        super.onPause()

        if (isFinishing) {

            database.child("calls").child(auth.uid!!).setValue(null)

        }

    }
}