package com.example.chaika.ui.screens.product

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.ui.viewModels.TemplateViewModel
import androidx.navigation.NavController
import com.example.chaika.domain.models.TemplateContentDomain
import com.example.chaika.ui.components.template.ButtonSurface
import com.example.chaika.ui.components.trip.dashedBorder
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.FillViewModel

@Composable
fun TemplateDetailView(
    templateId: Int,
    viewModel: TemplateViewModel,
    fillViewModel: FillViewModel,
    navController: NavController,
) {
    val templateState = produceState<TemplateDomain?>(initialValue = null, templateId) {
        value = viewModel.getTemplateDetail(templateId)
        Log.i("TemplateDetailView", "Got value: $value")
    }
    val template = templateState.value

    if (template == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Загрузка...")
        }
        return
    }

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
                        painter = painterResource(id = R.drawable.apple_juice), // TODO: заменить на реальное изображение
                        contentDescription = "Фото шаблона",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .dashedBorder(cornerRadius = 16.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(2f).align(Alignment.CenterVertically)
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
                template.content.take(Int.MAX_VALUE).forEachIndexed { index, item ->
                    // TODO: Replace ItemInfo with actual realisation
                    ItemInfo(item)
                    if (index < template.content.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
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
                navController.navigate("template_edit/${template.id}")
                      },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

// Временное отображение
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
