import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.ui.components.product.ProductItem
import com.example.chaika.ui.viewModels.ProductViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.material.progressindicator.CircularProgressIndicator


@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    // Добавляем обработчик жизненного цикла
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.syncWithCart()
                Lifecycle.Event.ON_STOP -> viewModel.clearState()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

//    if (isLoading) {
//        CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize())
//        return
//    }

    if (uiState.error != null) {
        Text(
            text = "Error: ${uiState.error}",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxSize().wrapContentSize()
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(pagingData.itemCount) { index ->
            val product = pagingData[index] ?: return@items
            ProductItem(
                product = product,
                onAddToCart = { viewModel.addToCart(product.id) },
                onQuantityIncrease = { viewModel.updateQuantity(product.id, +1) },
                onQuantityDecrease = { viewModel.updateQuantity(product.id, -1) }
            )
        }
    }
}