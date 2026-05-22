package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.domain.usecases.GetPagedProductsUseCase
import com.chaikasoft.app.domain.usecases.RefreshProductsOnLaunchUseCase
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.AppErrorUiMapper
import com.chaikasoft.app.ui.mappers.UiError
import com.chaikasoft.app.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val refreshProductsOnLaunchUseCase: RefreshProductsOnLaunchUseCase
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _syncError = MutableSharedFlow<UiError>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val syncError: Flow<UiError> = _syncError

    private val cartItemsFlow = MutableStateFlow<List<CartItemDomain>>(emptyList())

    private var isInitialized = false
    private var syncJob: Job? = null

    /**
     * Основной Flow с продуктами
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsFlow: Flow<PagingData<Product>> =
        _searchQuery
            .debounceExceptFirst(300)
            .distinctUntilChanged()
            .flatMapLatest { query: String ->
                getPagedProductsUseCase(query)
            }
            .map { pagingData: PagingData<ProductInfoDomain> ->
                pagingData.map { product: ProductInfoDomain ->
                    product.toUiModel()
                }
            }
            .cachedIn(viewModelScope)

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Инициализация данных - вызывается только один раз
     */
    fun attachCart(cartItems: StateFlow<List<CartItemDomain>>) {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            cartItems.collect {
                cartItemsFlow.value = it
            }
        }

        syncProductsInBackground()
    }

    /**
     * Debounce, который НЕ задерживает первое значение.
     * Использует channelFlow для правильного управления состоянием.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Flow<T>.debounceExceptFirst(timeoutMillis: Long): Flow<T> = channelFlow {
        var isFirst = true

        collectLatest { value ->
            if (isFirst) {
                isFirst = false
                send(value)
            } else {
                delay(timeoutMillis)
                send(value)
            }
        }
    }

    private fun syncProductsInBackground() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            _isSyncing.value = true
            try {
                when (val result = refreshProductsOnLaunchUseCase()) {
                    RefreshProductsResult.SkippedFreshCache -> {
                        Log.i(TAG, "Products refresh skipped: fresh cache")
                    }

                    is RefreshProductsResult.Success -> {
                        Log.i(TAG, "Products refresh success: count=${result.productCount}")
                    }

                    is RefreshProductsResult.RemoteFailure -> {
                        Log.w(TAG, "Products refresh remote failure: ${result.error}")
                        _syncError.emit(AppErrorUiMapper.map(result.error))
                    }

                    is RefreshProductsResult.LocalFailure -> {
                        Log.e(
                            TAG,
                            "Products refresh local failure: ${result.cause.message}",
                            result.cause
                        )
                        _syncError.emit(AppErrorUiMapper.map(AppError.Unknown(result.cause)))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected products refresh failure: ${e.message}", e)
                _syncError.emit(AppErrorUiMapper.map(AppError.Unknown(e)))
            } finally {
                _isSyncing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
    }

    private companion object {
        const val TAG = "ProductViewModel"
    }
}
