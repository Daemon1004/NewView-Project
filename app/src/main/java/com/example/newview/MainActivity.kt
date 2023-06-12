package com.example.newview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database.reference
        auth = Firebase.auth

        if (auth.uid == null) { finish() }
        database.child("users").child(auth.uid!!).get().addOnSuccessListener {

            Log.i("firebase", "Got value ${it.value}")

            userData = if (it.value != null) {
                it.getValue<UserData>() as UserData
            } else {
                UserData()
            }

            if (userData.since == null)
            {
                val now: Long = System.currentTimeMillis()
                database.child("users").child(auth.uid!!).child("since").setValue(now)
                Log.i("firebase", "First start detected")
                userData.since = now
            }
            if (userData.isblind != null) {
                if (userData.status == null && userData.isblind == false) {
                    Log.i("firebase", "V. Status is null")
                    database.child("users").child(auth.uid!!).child("status").setValue(false)
                    Log.i("firebase", "V. Fixed status")
                    userData.status = false
                } else if (userData.status != null && userData.isblind == true) {
                    Log.i("firebase", "B. Deleting status for blind user")
                    database.child("users").child(auth.uid!!).child("status").setValue(null)
                    Log.i("firebase", "B. Fixed status")
                    userData.status = null
                }
            }

            userDataLoaded()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
            finish()
        }

    }
    private fun navLoad()
    {
        findViewById<BottomNavigationView>(R.id.nav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    if (userData.isblind != null) {
                        loadFragment(
                            if (userData.isblind == true) { HomeFragmentB() } else { HomeFragmentV() }
                        )
                        true
                    } else {
                        false
                    }
                }
                R.id.settings -> {
                    loadFragment(
                        if (userData.isblind != null) { SettingsFragment() } else { PersonalDataSettingsFragment() }
                    )
                    true
                }
                else -> {
                    false
                }
            }
        }
        findViewById<BottomNavigationView>(R.id.nav).selectedItemId = if (userData.isblind != null) { R.id.home } else { R.id.settings }
    }
    private fun userDataLoaded() {

        navLoad()

        if (userData.isblind == null) { return }

        if (userData.isblind == false)
        {

            database.child("calls").orderByChild("needHelp").equalTo(true).limitToFirst(1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount > 0) {
                            //Log.i("firebase", "Gets: $dataSnapshot")
                            lateinit var data : DataSnapshot
                            for (postSnapshot in dataSnapshot.children)
                            {
                                Log.i("firebase", "Call1 gets: $postSnapshot")
                                data = postSnapshot
                            }

                            val blind = data.key

                            if (blind != null && userData.status == true) {
                                //database.child("calls").child(blind).child("volunteer").setValue(auth.uid)
                                //database.child("calls").child(blind).child("needHelp").setValue(null)
                                val updates: MutableMap<String, Any> = hashMapOf(
                                    "calls/$blind/volunteer" to (auth.uid as String),
                                    "calls/$blind/needHelp" to false
                                )
                                database.updateChildren(updates)
                            }

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("firebase", "Error (calls)")
                    }
                })

            database.child("calls").orderByChild("volunteer").equalTo(auth.uid).limitToFirst(1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount > 0) {
                            //Log.i("firebase", "Gets: $dataSnapshot")
                            lateinit var data : DataSnapshot
                            for (postSnapshot in dataSnapshot.children)
                            {
                                Log.i("firebase", "Call2 gets: $postSnapshot")
                                data = postSnapshot
                            }

                            val blind = data.key

                            if (blind != null) {
                                startCallVActivity(blind)
                            }

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("firebase", "Error (calls)")
                    }
                })

        }

    }
    fun loadFragment (Fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, Fragment)
        transaction.commit()
    }

    fun reload () {
        overridePendingTransition(0, 0)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    fun signOut () {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun startCallBActivity() {
        val intent = Intent(this, CallBActivity::class.java)
        intent.putExtra("userData", userData)
        startActivity(intent)
    }
    fun startCallVActivity(blind : String) {
        val intent = Intent(this, CallVActivity::class.java)
        intent.putExtra("userData", userData)
        intent.putExtra("blind", blind)
        startActivity(intent)
    }
}