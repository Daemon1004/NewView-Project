package com.example.newview

import android.os.Bundle
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject

class CallBActivity : CallActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var userData: UserData

    private lateinit var volunteerRef : DatabaseReference
    private lateinit var volunteerListener : ValueEventListener
    private lateinit var callRef : DatabaseReference
    private lateinit var callListener : ValueEventListener

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

        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
            .addHeaders("Authorization", token)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val meetingId = response.getString("roomId")

                        Log.i("VideoSDK", "MeetingId: $meetingId")

                        setMeetingId(meetingId)
                        createHelp(meetingId)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                }
            })

    }
    private fun createHelp(meetingId : String) {

        val me = auth.uid
        val updates: MutableMap<String, Any> = hashMapOf(
            "calls/$me/meetingId" to meetingId,
            "calls/$me/needHelp" to true
        )
        database.updateChildren(updates)

        volunteerRef = database.child("calls").child(auth.uid!!).child("volunteer")
        volunteerListener = volunteerRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("firebase", "VolunteerListener. Get: $dataSnapshot")

                    val volunteer = dataSnapshot.value.toString()

                    database.child("calls").removeEventListener(volunteerListener)
                    if (volunteer != "null") {
                        call()
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error")
                }
            })

        callRef = database.child("calls").child(auth.uid!!)
        callListener = callRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("firebase", "CallListener. Get: $dataSnapshot")

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

        setMyName(userData.firstname + " " + userData.lastname)
        setMeetingId(meetingId)
        setCallStatus(findViewById(R.id.CallStatus))

    }

    private fun call() {
        initCall(true)
    }

    override fun onDestroy()
    {

        try {
            callRef.removeEventListener(callListener)
            volunteerRef.removeEventListener(volunteerListener)
            database.child("calls").child(auth.uid!!).setValue(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }
}