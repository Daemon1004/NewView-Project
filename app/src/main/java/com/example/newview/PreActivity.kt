package com.example.newview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button


class PreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre)

        findViewById<Button>(R.id.mode1).setOnClickListener {

            goToMain("V")

        }

        findViewById<Button>(R.id.mode2).setOnClickListener {

            goToMain("B")

        }

    }
    private fun goToMain (Arg: String)
    {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Mode", Arg)
        startActivity(intent)
        finish()
    }
}