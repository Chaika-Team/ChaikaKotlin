package com.chaikasoft.app.ui.screens.product

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.AppErrorException
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.template.TemplateCard
import com.chaikasoft.app.ui.mappers.AppErrorUiMapper
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewmodels.TemplateViewModel

@Composable
fun TemplateSearchView(
    viewModel: TemplateViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val templatesPaging = viewModel.templatesPagingFlow.collectAsLazyPagingItems()

    Column(modifier = modifier.fillMaxSize()) {
        TemplateSearchContent(
            templatesPaging = templatesPaging,
            onTemplateClick = { template ->
                navController.navigate("template_detail/${template.id}")
            },
            modifier = Modifier.weight(1f)
        )

        ButtonSurface(
            buttonText = stringResource(R.string.template_skip),
            onClick = { navController.navigate(Routes.TEMPLATE_EDIT) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TemplateSearchContent(
    templatesPaging: LazyPagingItems<TemplateDomain>,
    onTemplateClick: (TemplateDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = templatesPaging.loadState.refresh
    val itemCount = templatesPaging.itemCount

    Box(modifier = modifier) {
        when {
            refreshState is LoadState.Loading && itemCount == 0 -> LoadingContent()
            refreshState is LoadState.Error && itemCount == 0 -> TemplateErrorContent(
                error = refreshState.error,
                onRetry = { templatesPaging.retry() }
            )
            itemCount == 0 -> EmptyTemplatesContent()
            else -> TemplateList(
                templatesPaging = templatesPaging,
                itemCount = itemCount,
                onTemplateClick = onTemplateClick
            )
        }
    }
}

@Composable
private fun EmptyTemplatesContent(modifier: Modifier = Modifier) {
    Log.d("TemplateSearchView", "No templates to display")
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.template_empty))
    }
}

@Composable
private fun TemplateList(
    templatesPaging: LazyPagingItems<TemplateDomain>,
    itemCount: Int,
    onTemplateClick: (TemplateDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        items(itemCount) { index ->
            TemplateListItem(
                template = templatesPaging[index],
                index = index,
                onTemplateClick = onTemplateClick
            )
        }

        item {
            AppendStateContent(
                appendState = templatesPaging.loadState.append,
                onRetry = { templatesPaging.retry() }
            )
        }
    }
}

@Composable
private fun TemplateListItem(
    template: TemplateDomain?,
    index: Int,
    onTemplateClick: (TemplateDomain) -> Unit
) {
    if (template == null) {
        Log.d("TemplateSearchView", "Template is null for index $index")
        return
    }

    Log.d("TemplateSearchView", "Rendering template card: ${template.templateName}")
    TemplateCard(
        template = template,
        onClick = { onTemplateClick(template) }
    )
}

@Composable
private fun AppendStateContent(
    appendState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (appendState) {
        is LoadState.Loading -> AppendLoadingContent(modifier = modifier)
        is LoadState.Error -> TemplateErrorContent(
            error = appendState.error,
            onRetry = onRetry,
            modifier = modifier.fillMaxWidth()
        )
        is LoadState.NotLoading -> Unit
    }
}

@Composable
private fun AppendLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TemplateErrorContent(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiError = AppErrorUiMapper.map(error.toAppError())

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(uiError.messageRes),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (uiError.retryable) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

private fun Throwable.toAppError(): AppError = when (this) {
    is AppErrorException -> error
    is Exception -> AppError.Unknown(this)
    else -> AppError.Unknown(Exception(this))
}
