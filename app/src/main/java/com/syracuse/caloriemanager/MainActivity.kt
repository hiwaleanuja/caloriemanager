package com.syracuse.caloriemanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.syracuse.caloriemanager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser == null) {
            val authActivity = Intent(this@MainActivity, AuthActivity::class.java)
            startActivity(authActivity)
            finish()
        } else {
            val trackingActivity = Intent(this@MainActivity, TrackingActivity::class.java)
            startActivity(trackingActivity)
            finish()
        }
    }

    companion object {
        @JvmStatic val TAG = "MainActivity"
    }
}