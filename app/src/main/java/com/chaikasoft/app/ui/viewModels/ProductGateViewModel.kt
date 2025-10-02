package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductGateViewModel @Inject constructor(
    private val hasAnyOnce: HasAnyPackageItemsOnceUseCase
) : ViewModel() {

    enum class Target { PACKAGE, ENTRY }

    suspend fun decide(): Target =
        if (hasAnyOnce()) Target.PACKAGE else Target.ENTRY
}
