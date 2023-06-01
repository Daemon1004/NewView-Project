package com.example.newview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

class PersonalDataSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_data_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myActivity = (activity as MainActivity?)!!
        val userData = myActivity.userData

        val firstNameView = view.findViewById<EditText>(R.id.FirstName)
        val lastNameView = view.findViewById<EditText>(R.id.LastName)
        val isBlindView = view.findViewById<CheckBox>(R.id.IsBlindCheckbox)

        firstNameView.setText(userData.firstname ?: "")
        lastNameView.setText(userData.lastname ?: "")
        isBlindView.isChecked = userData.isblind ?: false

        val auth = myActivity.auth
        val database = myActivity.database

        if (auth.uid == null) { myActivity.finish() }
        view.findViewById<Button>(R.id.ApplySettings).setOnClickListener {

            userData.firstname = firstNameView.text.toString()
            userData.lastname = lastNameView.text.toString()
            userData.isblind = isBlindView.isChecked

            database.child("users").child(auth.uid!!).setValue(userData).addOnSuccessListener {

                Log.i("firebase", "Saved user data")
                myActivity.reload()

            }.addOnFailureListener{

                Log.e("firebase", "Error setting user data")
                myActivity.reload()

            }

        }

    }
}