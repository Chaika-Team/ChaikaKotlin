package com.example.chaika.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chaika.R
import com.example.chaika.databinding.FragmentAuthBinding
import com.example.chaika.ui.view_models.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "ChaikaAuthFragment"
    }

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: arguments = ${arguments}")

        // При нажатии на кнопку «Войти» запускаем авторизацию.
        binding.loginButton.setOnClickListener {
            Log.d(TAG, "loginButton clicked. Starting authorization.")
            val authIntent = authViewModel.getAuthIntent()
            startActivity(authIntent)
        }

        // Пытаемся извлечь deep link Intent из аргументов.
        val deepLinkIntent = extractDeepLinkIntent()
        if (deepLinkIntent != null) {
            Log.d(TAG, "Deep link Intent found: ${deepLinkIntent.data}")
            authViewModel.processDeepLink(deepLinkIntent)
        } else {
            Log.d(TAG, "No deep link Intent found in arguments")
        }

        // Наблюдаем за результатом авторизации.
        authViewModel.accessToken.observe(viewLifecycleOwner, Observer { token ->
            if (token != null) {
                Log.d(TAG, "Access Token received: $token")
                Toast.makeText(requireContext(), "Authorization successful", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
        })

        authViewModel.error.observe(viewLifecycleOwner, Observer { errorMsg ->
            if (errorMsg != null) {
                Log.e(TAG, "Authorization error: $errorMsg")
                Toast.makeText(
                    requireContext(),
                    "Authorization error: $errorMsg",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Извлекает deep link Intent из аргументов, переданных NavController.
     * (Заметьте, что ключ "android-support-nav:controller:deepLinkIntent" используется, хотя он помечен как deprecated,
     * но в текущей реализации Navigation Component deep link передаёт Intent под этим ключом.)
     */
    private fun extractDeepLinkIntent(): Intent? {
        val args = arguments
        return args?.get("android-support-nav:controller:deepLinkIntent") as? Intent
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
