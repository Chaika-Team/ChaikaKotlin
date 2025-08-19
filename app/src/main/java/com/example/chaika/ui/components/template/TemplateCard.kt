package com.example.chaika.ui.components.template

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.domain.models.TemplateContentDomain
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.ui.components.trip.dashedBorder
import com.example.chaika.ui.theme.ChaikaTheme

@Composable
fun TemplateCard(
    template: TemplateDomain,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (imageRef, titleRef, listRef, moreRef, buttonRef) = createRefs()

                // Картинка
                Box(
                    modifier = Modifier
                        .constrainAs(imageRef) {
                            start.linkTo(parent.start, margin = 4.dp)
                            top.linkTo(parent.top, margin = 4.dp)
                            bottom.linkTo(parent.bottom, margin = 4.dp)
                            width = Dimension.value(86.dp)
                            height = Dimension.fillToConstraints
                        }
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .dashedBorder(cornerRadius = 16.dp)
                ) {
                    // Здесь можно использовать AsyncImage или Coil
                    // Пока просто плейсхолдер
                    Image(
                        painter = painterResource(R.drawable.ic_menu_gallery),
                        contentDescription = "Template image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .padding(4.dp)
                    )
                }

                // Название шаблона
                Text(
                    text = template.templateName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.constrainAs(titleRef) {
                        start.linkTo(imageRef.end, margin = 12.dp)
                        top.linkTo(parent.top, margin = 2.dp)
                        end.linkTo(buttonRef.start, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    }
                )

                // Список содержимого
//                ColumnOfContent(
//                    content = template.content,
//                    modifier = Modifier.constrainAs(listRef) {
//                        start.linkTo(imageRef.end, margin = 12.dp)
//                        top.linkTo(titleRef.bottom)
//                        end.linkTo(buttonRef.start, margin = 8.dp)
//                        bottom.linkTo(parent.bottom, margin = 12.dp)
//                        width = Dimension.fillToConstraints
//                        height = Dimension.preferredValue(102.dp)
//                    }
//                )

                // Временно вместо списка
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.constrainAs(listRef) {
                        start.linkTo(imageRef.end, margin = 12.dp)
                        top.linkTo(titleRef.bottom)
                        end.linkTo(buttonRef.start, margin = 8.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.preferredValue(102.dp)
                    }
                )

                // Кнопка справа
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .constrainAs(buttonRef) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.value(50.dp)
                            height = Dimension.value(128.dp)
                        }
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                ) {
                    Text(">")
                }
                
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
    }
}

@Composable
@Preview
fun TemplateCardPreview() {
    ChaikaTheme {
        TemplateCard(
            template = TemplateDomain(
                id = 1,
                templateName = "Template 1",
                description = "Description",
                content = listOf(
                    TemplateContentDomain(productId = 1, quantity = 1),
                    TemplateContentDomain(productId = 2, quantity = 2),
                    TemplateContentDomain(productId = 3, quantity = 3),
                    TemplateContentDomain(productId = 3, quantity = 3),
                    TemplateContentDomain(productId = 3, quantity = 3),
                    TemplateContentDomain(productId = 3, quantity = 3)
                )
            ),
            onClick = { }
        )
    }
}