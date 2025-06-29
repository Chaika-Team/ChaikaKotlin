package com.example.chaika.ui.screens.profile.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chaika.R

@Composable
fun FaqsView() {
    var expandedItems by remember { mutableStateOf(mutableSetOf<Int>()) }

    val faqs = listOf(
        FAQ(
            question = stringResource(R.string.faqs_question_trip_add),
            answer = stringResource(R.string.faqs_answer_trip_add)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_trip_finish),
            answer = stringResource(R.string.faqs_answer_trip_finish)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_product_add),
            answer = stringResource(R.string.faqs_answer_product_add)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_trip_history),
            answer = stringResource(R.string.faqs_answer_trip_history)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_personal_data),
            answer = stringResource(R.string.faqs_answer_personal_data)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_app_loading),
            answer = stringResource(R.string.faqs_answer_app_loading)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_logout),
            answer = stringResource(R.string.faqs_answer_logout)
        ),
        FAQ(
            question = stringResource(R.string.faqs_question_offline),
            answer = stringResource(R.string.faqs_answer_offline)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        faqs.forEachIndexed { index, faq ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = faq.question,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (expandedItems.contains(index)) {
                                    expandedItems = expandedItems.toMutableSet().apply { remove(index) }
                                } else {
                                    expandedItems = expandedItems.toMutableSet().apply { add(index) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (expandedItems.contains(index)) {
                                    Icons.Default.ExpandLess
                                } else {
                                    Icons.Default.ExpandMore
                                },
                                contentDescription = if (expandedItems.contains(index)) {
                                    stringResource(R.string.faqs_collapse)
                                } else {
                                    stringResource(R.string.faqs_expand)
                                }
                            )
                        }
                    }
                    
                    if (expandedItems.contains(index)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = faq.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.faqs_not_found_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.faqs_not_found_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class FAQ(
    val question: String,
    val answer: String
)