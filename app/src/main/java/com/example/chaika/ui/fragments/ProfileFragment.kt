package com.example.chaika.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chaika.R
import com.example.chaika.databinding.FragmentProfileBinding
import com.example.chaika.ui.viewModels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Получаем ViewModel для профиля
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Наблюдаем за данными проводника
        viewModel.conductor.observe(viewLifecycleOwner) { conductor ->
            conductor?.let {
                // Загружаем фотографию (если путь не пустой), иначе устанавливаем дефолтное изображение
                if (it.image.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(File(it.image))
                        .into(binding.ivProfilePhoto)
                } else {
                    binding.ivProfilePhoto.setImageResource(R.drawable.ic_profile)
                }

                // Объединяем фамилию и имя
                binding.tvFullName.text = it.name
                // Отображаем табельный номер
                binding.tvEmployeeID.text = getString(R.string.employee_id, it.employeeID)
            }
        }

        // Обработка кнопки "Выйти" – вызываем logout через ViewModel
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        // Наблюдаем за успешным выходом, чтобы перейти к экрану авторизации
        viewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.action_profileFragment_to_authFragment)
            }
        }

        // Обработка hotBar – переход на главную страницу
        binding.btnMain.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
        }
        // Если нажата кнопка "Профиль", мы уже на этом экране – можно ничего не делать
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
