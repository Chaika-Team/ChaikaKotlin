package com.chaikasoft.app.ui.screens.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.trip.dashedBorder
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.TemplateDetailUiState
import com.chaikasoft.app.ui.viewModels.FillViewModel
import com.chaikasoft.app.ui.viewModels.TemplateViewModel

@Composable
fun TemplateDetailView(
    templateId: Int,
    viewModel: TemplateViewModel,
    fillViewModel: FillViewModel,
    navController: NavController,
) {
    val detailState = viewModel.templateDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(templateId) {
        viewModel.loadTemplateDetail(templateId)
    }

    when (val state = detailState.value) {
        TemplateDetailUiState.Idle,
        TemplateDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Загрузка...")
            }
        }

        is TemplateDetailUiState.Error -> {
            val errorMessage = state.cause.localizedMessage?.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.error_try_later)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = viewModel::retryLoadTemplateDetail) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }

        is TemplateDetailUiState.Content -> {
            TemplateDetailContent(
                template = state.template,
                fillViewModel = fillViewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun TemplateDetailContent(
    template: TemplateDomain,
    fillViewModel: FillViewModel,
    navController: NavController,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .dashedBorder(cornerRadius = 16.dp)
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.apple_juice),
                        contentDescription = "Template image",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .dashedBorder(cornerRadius = 16.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = template.templateName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                )
                template.content.forEachIndexed { index, item ->
                    ItemInfo(item)
                    if (index < template.content.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
        ButtonSurface(
            buttonText = "ПРИМЕНИТЬ",
            onClick = {
                fillViewModel.onApplyTemplate(template)
                navController.navigate(Routes.TEMPLATE_EDIT)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ItemInfo(item: TemplateContentDomain) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ProductId: ${item.productId}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "x${item.quantity}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

