package com.example.newview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
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

        /*
        val actionBar : ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setIcon(R.mipmap.ic_launcher)
        }
        */

        database = Firebase.database.reference
        auth = Firebase.auth

        stopService(Intent(this, CallService :: class.java))

        if (auth.uid == null) { finish() }
        showProgress(true)
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
    fun showProgress(show : Boolean) {
        findViewById<ProgressBar>(R.id.progressBar).visibility = if (show) { ProgressBar.VISIBLE } else { ProgressBar.INVISIBLE }
    }
    /*
    override fun onStart() {
        super.onStart()

        try {
            if (userData.isblind == false) {
                addCallListener()
            }
        } catch (e : UninitializedPropertyAccessException) {
            e.printStackTrace()
        }

    }
    override fun onStop() {
        super.onStop()

        try {
            if (userData.isblind == false) {
                removeCallListener()
            }
        } catch (e : UninitializedPropertyAccessException) {
            e.printStackTrace()
        }

    }
    */

    private fun userDataLoaded() {

        navLoad()

        showProgress(false)

        if (userData.isblind == null) { return }

        if (userData.isblind == false)
        {

            //addCallListener()

            startService(Intent(this, CallService :: class.java))

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

}