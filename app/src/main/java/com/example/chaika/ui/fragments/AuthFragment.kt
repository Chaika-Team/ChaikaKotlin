package com.example.chaika.ui.fragments

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
import com.example.chaika.ui.view_models.DeepLinkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "ChaikaAuthFragment"
    }

    // Фрагментский ViewModel для авторизации
    private val authViewModel: AuthViewModel by viewModels()

    // Получаем общий (activity‑scопированный) DeepLinkViewModel
    private val deepLinkViewModel: DeepLinkViewModel by viewModels({ requireActivity() })

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

        // При нажатии на кнопку «Войти» запускаем авторизацию через OAuth
        binding.loginButton.setOnClickListener {
            Log.d(TAG, "loginButton clicked. Starting authorization.")
            val authIntent = authViewModel.getAuthIntent()
            startActivity(authIntent)
        }

        // Подписываемся на deep link-события из DeepLinkViewModel
        deepLinkViewModel.deepLinkIntent.observe(viewLifecycleOwner, Observer { intent ->
            if (intent != null) {
                Log.d(TAG, "Deep link Intent received: ${intent.data}")
                authViewModel.processDeepLink(intent)
                // После обработки очищаем deep link, чтобы не повторять обмен кода на токен
                deepLinkViewModel.clearDeepLink()
            }
        })

        // Наблюдаем за полученным access token – при его наличии переходим к MainFragment
        authViewModel.accessToken.observe(viewLifecycleOwner, Observer { token ->
            if (token != null) {
                Log.d(TAG, "Access Token received: $token")
                Toast.makeText(requireContext(), "Authorization successful", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
        })

        // Наблюдаем за ошибками авторизации
        authViewModel.error.observe(viewLifecycleOwner, Observer { errorMsg ->
            if (errorMsg != null) {
                Log.e(TAG, "Authorization error: $errorMsg")
                Toast.makeText(
                    requireContext(),
                    "Authorization error: $errorMsg",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
