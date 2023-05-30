package com.example.newview

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.PersonalDataSettings).setOnClickListener {
            (activity as MainActivity?)!!.loadFragment(PersonalDataSettingsFragment())
        }

        /*
        view.findViewById<Button>(R.id.PasswordSettings).setOnClickListener {
            (activity as MainActivity?)!!.loadFragment(PasswordSettingsFragment())
        }
        */

        view.findViewById<Button>(R.id.NotificationSettings).setOnClickListener {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            } else {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", requireContext().packageName)
                intent.putExtra("app_uid", requireContext().applicationInfo.uid)
            }
            requireContext().startActivity(intent)
        }

        view.findViewById<Button>(R.id.LogOutButton).setOnClickListener { (activity as MainActivity?)!!.signOut() }

    }
}