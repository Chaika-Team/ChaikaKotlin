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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateItemDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.ui.components.product.ProductImage
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.trip.dashedBorder
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.TemplateDetailUiState
import com.chaikasoft.app.ui.viewmodels.FillViewModel
import com.chaikasoft.app.ui.viewmodels.TemplateViewModel

@Composable
fun TemplateDetailView(
    templateId: Int,
    viewModel: TemplateViewModel,
    fillViewModel: FillViewModel,
    navController: NavController
) {
    val detailState = viewModel.templateDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(templateId) {
        viewModel.loadTemplateDetail(templateId)
    }

    when (val state = detailState.value) {
        TemplateDetailUiState.Idle,
        TemplateDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.template_detail_loading))
            }
        }

        is TemplateDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(state.messageRes),
                        color = MaterialTheme.colorScheme.error
                    )
                    if (state.retryable) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = viewModel::retryLoadTemplateDetail) {
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }
            }
        }

        is TemplateDetailUiState.Content -> {
            TemplateDetailContent(
                detail = state.detail,
                onApplyTemplate = { template ->
                    fillViewModel.onApplyTemplate(template)
                    navController.navigate(Routes.TEMPLATE_EDIT)
                }
            )
        }
    }
}

@Composable
private fun TemplateDetailContent(
    detail: ResolvedTemplateDetailDomain,
    onApplyTemplate: (TemplateDomain) -> Unit
) {
    val template = detail.template
    val hasMissingProducts = detail.items.any { it.product == null }

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
                        contentDescription = stringResource(
                            R.string.template_detail_image_content_description
                        ),
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
                detail.items.forEachIndexed { index, item ->
                    TemplateItemInfo(item)
                    if (index < detail.items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                    }
                }
                if (hasMissingProducts) {
                    Text(
                        text = stringResource(R.string.template_detail_missing_products),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        ButtonSurface(
            buttonText = stringResource(R.string.template_detail_apply),
            enabled = !hasMissingProducts,
            onClick = {
                onApplyTemplate(template)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun TemplateItemInfo(item: ResolvedTemplateItemDomain) {
    val product = item.product
    val productName = product?.name
        ?: stringResource(R.string.template_unknown_product_name, item.productId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(
            imageUrl = product?.image.orEmpty(),
            contentDescription = productName,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = productName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (product == null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.template_item_quantity, item.quantity),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
            textAlign = TextAlign.End
        )
    }
}
