package com.example.newview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CallVActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_v)

        val arguments = intent.extras

        database = Firebase.database.reference
        auth = Firebase.auth

        lateinit var blind : String
        if (arguments != null) {
            @Suppress("DEPRECATION")
            userData = arguments.getSerializable("userData") as UserData
            blind = arguments.getString("blind").toString()
        } else {
            finish()
            return
        }

        database.child("calls").child(blind)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("firebase", "Get: $dataSnapshot")

                    val data = dataSnapshot.value.toString()

                    if (data == "null")
                    {
                        finish()
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error")
                }
            })

    }
}