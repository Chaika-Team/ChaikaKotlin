import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.ui.components.product.ProductComponent
import com.example.chaika.ui.theme.ChaikaTheme
import com.example.chaika.ui.theme.LightColorScheme
import com.example.chaika.ui.viewModels.ProductViewModel


@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.syncWithCartOnChange()
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

    if (uiState.error != null) {
        Text(
            text = "Error: ${uiState.error}",
            color = LightColorScheme.error,
            modifier = Modifier.fillMaxSize().wrapContentSize()
        )
        return
    }

    ChaikaTheme {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                color = MaterialTheme.colorScheme.primary
            )
            return@ChaikaTheme
        }

        if (uiState.error != null) {
            Text(
                text = "Error: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
            return@ChaikaTheme
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                count = pagingData.itemCount,
                key = pagingData.itemKey{it.id}
            ) { index ->
                val product = pagingData[index]
                if (product != null) {
                    ProductComponent(
                        product = product,
                        onAddToCart = { viewModel.addToCart(product.id) },
                        onQuantityIncrease = { viewModel.updateQuantity(product.id, +1) },
                        onQuantityDecrease = { viewModel.updateQuantity(product.id, -1) }
                    )
                }
            }
        }
    }
}
