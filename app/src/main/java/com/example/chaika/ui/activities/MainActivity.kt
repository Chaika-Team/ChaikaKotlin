package com.example.chaika.ui.activities


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.chaika.ui.components.bottomBa.BottomBar
import com.example.chaika.ui.navigation.NavGraph
import com.example.chaika.ui.theme.ProductTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavGraph(navController = navController)
                    }
                }

            }
        }
    }
}

//    private lateinit var binding: ActivityMainBinding
//
//    companion object {
//        private const val TAG = "ChaikaMainActivity"
//    }

    // Получаем DeepLinkViewModel, scope – Activity
//    private val deepLinkViewModel: DeepLinkViewModel by viewModels()

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Если приложение запущено по deep link, обработаем Intent
//        intent?.let { handleDeepLinkIntent(it) }
//
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        Log.d(TAG, "onCreate: intent.data = ${intent?.data}")
//        navController.handleDeepLink(intent)

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.d(TAG, "onNewIntent: intent.data = ${intent?.data}")
//        intent?.let {
//            setIntent(it)
//            // Если Intent содержит deep link (например, redirect с OAuth),
//            // отправляем его в DeepLinkViewModel
//            if (it.data != null) {
//                deepLinkViewModel.postDeepLink(it)
//            }
//            // Передаём deep link NavController для навигации (если задан в nav_graph)
//            val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//            navHostFragment.navController.handleDeepLink(it)
//        }
//    }
//
//    private fun handleDeepLinkIntent(intent: Intent) {
//        if (intent.data != null) {
//            deepLinkViewModel.postDeepLink(intent)
//        }
//    }
