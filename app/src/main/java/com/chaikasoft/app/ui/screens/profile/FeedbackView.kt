package com.chaikasoft.app.ui.screens.profile

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview

@Composable
fun FeedbackView() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(true) }
    var showEmailError by remember { mutableStateOf(false) }

    val categories = listOf(
        stringResource(R.string.feedback_category_general),
        stringResource(R.string.feedback_category_support),
        stringResource(R.string.feedback_category_suggestion),
        stringResource(R.string.feedback_category_bug),
        stringResource(R.string.feedback_category_other)
    )

    fun validateEmail(email: String): Boolean =
        email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()

    LaunchedEffect(email) {
        isEmailValid = validateEmail(email)
        showEmailError = email.isNotEmpty() && !isEmailValid
    }

    LaunchedEffect(Unit) {
        if (selectedCategory.isEmpty()) {
            selectedCategory = categories.first()
        }
    }

    FeedbackContent(
        isSubmitted = isSubmitted,
        categories = categories,
        selectedCategory = selectedCategory,
        name = name,
        email = email,
        subject = subject,
        message = message,
        showEmailError = showEmailError,
        isSubmitEnabled = name.isNotBlank() &&
            message.isNotBlank() &&
            (email.isBlank() || validateEmail(email)),
        onCategoryChange = { selectedCategory = it },
        onNameChange = { name = it },
        onEmailChange = { email = it },
        onSubjectChange = { subject = it },
        onMessageChange = { message = it },
        onSubmit = {
            // Backlog ChaikaKotlin #204
            isSubmitted = true
        },
        onSendAnother = {
            isSubmitted = false
            name = ""
            email = ""
            subject = ""
            message = ""
            selectedCategory = categories.first()
        }
    )
}

@PhoneScalablePreviews
@Composable
private fun FeedbackViewPreview() {
    ChaikaTheme {
        FeedbackView()
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun FeedbackViewWidePreview() {
    ChaikaTheme {
        FeedbackView()
    }
}
