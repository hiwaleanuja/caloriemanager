package com.syracuse.caloriemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.syracuse.caloriemanager.databinding.ActivityAuthBinding
import com.syracuse.caloriemanager.databinding.ActivityMainBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val finalHost = NavHostFragment.create(R.navigation.auth_flow)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, finalHost)
            .setPrimaryNavigationFragment(finalHost) // equivalent to app:defaultNavHost="true"
            .commit()
    }
}