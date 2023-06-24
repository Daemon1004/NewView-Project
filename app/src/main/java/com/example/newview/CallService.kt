package com.example.newview

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class CallService : Service() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    private lateinit var userData: UserData

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

        database = Firebase.database.reference
        auth = Firebase.auth

        Log.i("Service", "Created")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (auth.uid != null) {
            database.child("users").child(auth.uid!!).get().addOnSuccessListener {

                Log.i("firebase", "Got value ${it.value}")

                userData = if (it.value != null) {
                    it.getValue<UserData>() as UserData
                } else {
                    UserData()
                }

                addCallListener()

            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }

        Log.i("Service", "Started")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {

        removeCallListener()

        Log.i("Service", "Destroyed")
        super.onDestroy()
    }

    private lateinit var callRef : DatabaseReference
    private lateinit var callListener : ValueEventListener
    private var listenerExist : Boolean = false
    private fun addCallListener() {
        if (userData.isblind == null || userData.status == null) { return }
        if (listenerExist) { removeCallListener() }
        listenerExist = true
        callRef = database.child("calls")
        callListener = callRef.orderByChild("needHelp").equalTo(true).limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount > 0) {
                        lateinit var data: DataSnapshot
                        for (postSnapshot in dataSnapshot.children) {
                            Log.i("firebase", "Call1 gets: $postSnapshot")
                            data = postSnapshot
                        }

                        val blind = data.key

                        if (blind != null) {

                            val updates: MutableMap<String, Any> = hashMapOf(
                                "calls/$blind/volunteer" to (auth.uid as String),
                                "calls/$blind/needHelp" to false
                            )
                            database.updateChildren(updates)

                            notifyCall(blind)

                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error (calls)")
                }
            })
    }
    private fun removeCallListener()
    {
        if (!listenerExist) { return }
        listenerExist = false
        callRef.removeEventListener(callListener)
    }

    companion object {
        const val NOTIFY_ID = 101
        const val CHANNEL_ID = "Call channel"
    }

    @SuppressLint("MissingPermission")
    private fun notifyCall(blind : String) {

        val intent = Intent(applicationContext, CallVActivity::class.java)
        intent.putExtra("userData", userData)
        intent.putExtra("blind", blind)
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.CallAcceptNotify))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFY_ID, builder.build())

    }

}