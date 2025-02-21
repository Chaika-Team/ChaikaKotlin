package com.example.chaika.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.chaika.R
import com.example.chaika.databinding.ActivityMainBinding
import com.example.chaika.ui.viewModels.DeepLinkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "ChaikaMainActivity"
    }

    // Получаем DeepLinkViewModel, scope – Activity
    private val deepLinkViewModel: DeepLinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Если приложение запущено по deep link, обработаем Intent
        intent?.let { handleDeepLinkIntent(it) }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        Log.d(TAG, "onCreate: intent.data = ${intent?.data}")
        navController.handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: intent.data = ${intent?.data}")
        intent?.let {
            setIntent(it)
            // Если Intent содержит deep link (например, redirect с OAuth),
            // отправляем его в DeepLinkViewModel
            if (it.data != null) {
                deepLinkViewModel.postDeepLink(it)
            }
            // Передаём deep link NavController для навигации (если задан в nav_graph)
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.handleDeepLink(it)
        }
    }

    private fun handleDeepLinkIntent(intent: Intent) {
        if (intent.data != null) {
            deepLinkViewModel.postDeepLink(intent)
        }
    }
}
