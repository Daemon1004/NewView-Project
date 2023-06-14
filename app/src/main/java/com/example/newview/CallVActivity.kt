package com.example.newview

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CallVActivity : CallActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userData: UserData
    private lateinit var blind : String

    private lateinit var callRef : DatabaseReference
    private lateinit var callListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_call_v)

        val arguments = intent.extras

        database = Firebase.database.reference
        auth = Firebase.auth

        if (arguments != null) {
            @Suppress("DEPRECATION")
            userData = arguments.getSerializable("userData") as UserData
            blind = arguments.getString("blind").toString()
        } else {
            finish()
            return
        }

        callRef = database.child("calls").child(blind)
        callListener = callRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("firebase", "CallListener. Get: $dataSnapshot")

                    val data = dataSnapshot.value.toString()

                    if (data == "null")
                    {
                        finish()
                        //startActivity(intent)
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error")
                }
            })

        setMyName(userData.firstname + " " + userData.lastname)
        setVideoView(findViewById(R.id.MyVideoView))

        setProgressBar(findViewById(R.id.progressBar))
        setCallStatus(findViewById(R.id.CallStatus))

        database.child("calls").child(blind).child("meetingId").get().addOnSuccessListener {
            Log.i("firebase", "GetMeetingId. Get: $it")

            setMeetingId(it.value.toString())

            findViewById<View>(R.id.VideoLayout).visibility = View.VISIBLE
            initCall(false)
        }

    }
    override fun onDestroy() {

        callRef.removeEventListener(callListener)
        database.child("calls").child(blind).setValue(null)

        super.onDestroy()
    }
}