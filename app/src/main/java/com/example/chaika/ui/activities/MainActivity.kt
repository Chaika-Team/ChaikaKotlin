package com.example.chaika.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.chaika.R
import com.example.chaika.databinding.ActivityMainBinding
import com.example.chaika.ui.fragments.AuthFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Главная активность приложения.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MMainActivity", "onCreate called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MMainActivity", "ViewBinding и setContentView успешно вызваны")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment == null) {
            Log.e("MMainActivity", "NavHostFragment не найден!")
        } else {
            Log.d("MMainActivity", "NavHostFragment успешно найден")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("MMainActivity", "onNewIntent called with intent: $intent")

        // Передаём Intent в AuthFragment через NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val fragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment
        if (fragment is AuthFragment) {
            Log.d("MMainActivity", "Passing intent to AuthFragment")
            fragment.handleIntent(intent)
        } else {
            Log.e("MMainActivity", "AuthFragment not found in NavHostFragment")
        }
    }
}
