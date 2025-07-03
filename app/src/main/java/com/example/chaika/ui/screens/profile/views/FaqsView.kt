package com.example.chaika.ui.screens.profile.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import com.example.chaika.ui.components.profile.FaqCard

@Composable
fun FaqsView() {
    var expandedItems by remember { mutableStateOf(setOf<Int>()) }

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
            FaqCard(
                question = faq.question,
                answer = faq.answer,
                expanded = expandedItems.contains(index),
                onExpandToggle = {
                    expandedItems = if (expandedItems.contains(index)) {
                        expandedItems - index
                    } else {
                        expandedItems + index
                    }
                },
                expandContentDescription = stringResource(R.string.faqs_expand),
                collapseContentDescription = stringResource(R.string.faqs_collapse)
            )
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