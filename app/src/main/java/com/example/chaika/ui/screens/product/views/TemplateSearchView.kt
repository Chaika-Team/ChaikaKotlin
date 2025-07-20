package com.example.chaika.ui.screens.product.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.ui.viewModels.TemplateViewModel

@Composable
fun TemplateSearchView(
    viewModel: TemplateViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val templatesPaging = viewModel.templatesPagingFlow.collectAsLazyPagingItems()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            templatesPaging.itemCount == 0 -> {
                Log.d("TemplateSearchView", "Нет шаблонов для отображения")
                Text("Нет шаблонов", modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(templatesPaging.itemCount) { index ->
                        val template = templatesPaging[index]
                        if (template != null) {
                            Log.d("TemplateSearchView", "Отрисовка карточки шаблона: ${template.templateName}")
                            TemplateCard(
                                template = template,
                                modifier = Modifier.fillMaxWidth().height(144.dp)
                            )
                        } else {
                            Log.d("TemplateSearchView", "template == null для индекса $index")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: TemplateDomain,
    modifier: Modifier = Modifier
) {
    var showMore by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .padding(8.dp)
    ) {
        val (imageRef, titleRef, listRef, moreRef, buttonRef) = createRefs()

        // Картинка
        Box(
            modifier = Modifier
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(86.dp)
                    height = Dimension.fillToConstraints
                }
        ) {
            // Здесь можно использовать AsyncImage или Coil
            // Пока просто плейсхолдер
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                contentDescription = "Template image",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Название шаблона
        Text(
            text = template.templateName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(imageRef.end, margin = 12.dp)
                top.linkTo(parent.top, margin = 12.dp)
                end.linkTo(buttonRef.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        // Список содержимого
        Column(
            modifier = Modifier.constrainAs(listRef) {
                start.linkTo(imageRef.end, margin = 12.dp)
                top.linkTo(titleRef.bottom, margin = 8.dp)
                end.linkTo(buttonRef.start, margin = 8.dp)
                bottom.linkTo(parent.bottom, margin = 12.dp)
                width = Dimension.fillToConstraints
                height = Dimension.preferredWrapContent
            }
        ) {
            val maxVisible = if (showMore) template.content.size else 3
            template.content.take(maxVisible).forEach { item ->
                Text(text = "ProductId: ${item.productId} x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            if (!showMore && template.content.size > 3) {
                Text(
                    text = "Подробнее...",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { showMore = true }
                )
            }
        }

        // Кнопка справа
        androidx.compose.material3.Button(
            onClick = { /* TODO: handle click */ },
            modifier = Modifier
                .constrainAs(buttonRef) {
                    end.linkTo(parent.end, margin = 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(50.dp)
                    height = Dimension.value(128.dp)
                },
            shape = MaterialTheme.shapes.medium
        ) {
            Text(">")
        }
    }
}