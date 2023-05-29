package com.example.newview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeFragment = if (intent.getStringExtra("Mode") == "B") { HomeFragmentB() } else { HomeFragmentV() }

        loadFragment(homeFragment)
        findViewById<BottomNavigationView>(R.id.nav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }

    }
    fun loadFragment (Fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, Fragment)
        transaction.commit()
    }
}