package com.chaikasoft.app.ui.screens.product

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.template.TemplateCard
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.TemplateViewModel

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
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(templatesPaging.itemCount) { index ->
                            val template = templatesPaging[index]
                            if (template != null) {
                                Log.d("TemplateSearchView", "Отрисовка карточки шаблона: ${template.templateName}")
                                TemplateCard(
                                    template = template,
                                    onClick = {
                                        navController.navigate("template_detail/${template.id}")
                                    }
                                )
                            } else {
                                Log.d("TemplateSearchView", "template == null для индекса $index")
                            }
                        }
                    }
                    ButtonSurface(
                        buttonText = "ПРОПУСТИТЬ",
                        onClick = { navController.navigate(Routes.TEMPLATE_EDIT) },
                        modifier = modifier
                    )
                }
            }
        }
    }
}
