package com.example.chaika.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chaika.R
import com.example.chaika.databinding.FragmentMainBinding
import com.example.chaika.ui.adapters.ProductAdapter
import com.example.chaika.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    // Получаем MainViewModel, где уже инжектирован юзкейс для пагинации и для обновления данных
    private val mainViewModel: MainViewModel by viewModels()

    // Адаптер для отображения списка товаров
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Заголовок страницы
        binding.tvTitle.text = "Главная страница"

        // Инициализируем адаптер и устанавливаем его в RecyclerView
        productAdapter = ProductAdapter()
        binding.rvProducts.adapter = productAdapter

        // Собираем PagingData из ViewModel и передаём в адаптер
        lifecycleScope.launch {
            mainViewModel.productsFlow.collectLatest { pagingData ->
                productAdapter.submitData(pagingData)
            }
        }

        // Обработка кнопки "Обновить" – по нажатию загружаем данные с сервера
        binding.btnRefresh.setOnClickListener {
            mainViewModel.refreshProducts(limit = 100, offset = 0)
            productAdapter.refresh() // Инвалидирует PagingSource, заставляя перезагрузить данные
        }

        // Обработка кнопок нижней панели (hotBar)
        binding.btnMain.setOnClickListener {
            // Остаёмся на главном экране
        }
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
