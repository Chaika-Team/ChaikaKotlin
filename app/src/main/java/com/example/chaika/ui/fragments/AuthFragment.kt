package com.example.chaika.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chaika.R
import com.example.chaika.databinding.FragmentAuthBinding
import com.example.chaika.domain.usecases.PerformAuthorizationUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Фрагмент для авторизации пользователя.
 */
@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var startAuthorizationUseCase: StartAuthorizationUseCase

    @Inject
    lateinit var performAuthorizationUseCase: PerformAuthorizationUseCase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка запуска авторизации теперь вызывает юзкейс
        binding.loginButton.setOnClickListener {
            startAuthorizationUseCase()
        }
    }

    /**
     * Обрабатывает Intent из MainActivity после завершения авторизации.
     *
     * @param intent Intent с результатом авторизации.
     */
    fun handleIntent(intent: Intent?) {
        intent?.let {
            lifecycleScope.launch {
                try {
                    val accessToken = performAuthorizationUseCase(it)
                    Toast.makeText(
                        requireContext(),
                        "Access Token: $accessToken",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_authFragment_to_mainFragment)
                } catch (e: Exception) {
                    Log.e("AuthFragment", "Authorization failed: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        "Authorization failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
