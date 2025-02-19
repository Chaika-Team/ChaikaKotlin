package com.example.chaika.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chaika.R
import com.example.chaika.databinding.FragmentAuthCheckBinding
import com.example.chaika.ui.view_models.AuthCheckViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthCheckFragment : Fragment() {

    private var _binding: FragmentAuthCheckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthCheckViewModel by viewModels()

    companion object {
        private const val TAG = "AuthCheckFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Наблюдаем за результатом проверки токена
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrEmpty()) {
                Log.d(TAG, "Token exists, navigating to MainFragment")
                findNavController().navigate(R.id.action_authCheckFragment_to_mainFragment)
            } else {
                Log.d(TAG, "No token found, navigating to AuthFragment")
                findNavController().navigate(R.id.action_authCheckFragment_to_authFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
