package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chaika.domain.usecases.GetPagedTemplatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getPagedTemplatesUseCase: GetPagedTemplatesUseCase
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)
} 