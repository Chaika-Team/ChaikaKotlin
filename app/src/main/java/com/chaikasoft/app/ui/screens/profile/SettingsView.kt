package com.chaikasoft.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.settings.AppLanguage
import com.chaikasoft.app.domain.models.settings.AppSettings
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.SettingsUiState
import com.chaikasoft.app.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsView(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshLanguage()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    SettingsContent(
        uiState = uiState,
        onNotificationsEnabledChange = viewModel::onNotificationsEnabledChange,
        onSoundEnabledChange = viewModel::onSoundEnabledChange,
        onVibrationEnabledChange = viewModel::onVibrationEnabledChange,
        onDarkThemeEnabledChange = viewModel::onDarkThemeEnabledChange,
        onAutoSyncEnabledChange = viewModel::onAutoSyncEnabledChange,
        onLanguageChange = viewModel::onLanguageChange
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onDarkThemeEnabledChange: (Boolean) -> Unit,
    onAutoSyncEnabledChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val settings = uiState.settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settingsScreen")
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsSection(
            title = stringResource(R.string.settings_notifications_title),
            tag = "settingsNotificationsSection"
        ) {
            SwitchRow(
                title = stringResource(R.string.settings_notifications_enable),
                description = stringResource(R.string.settings_notifications_description),
                checked = settings.notificationsEnabled,
                tag = "settingsNotificationsSwitch",
                onCheckedChange = onNotificationsEnabledChange
            )
            SwitchRow(
                title = stringResource(R.string.settings_sound_title),
                description = stringResource(R.string.settings_sound_description),
                checked = settings.soundEnabled,
                enabled = settings.notificationsEnabled,
                tag = "settingsSoundSwitch",
                onCheckedChange = onSoundEnabledChange
            )
            SwitchRow(
                title = stringResource(R.string.settings_vibration_title),
                description = stringResource(R.string.settings_vibration_description),
                checked = settings.vibrationEnabled,
                enabled = settings.notificationsEnabled,
                tag = "settingsVibrationSwitch",
                onCheckedChange = onVibrationEnabledChange
            )
        }

        SettingsSection(
            title = stringResource(R.string.settings_appearance_title),
            tag = "settingsAppearanceSection"
        ) {
            SwitchRow(
                title = stringResource(R.string.settings_dark_theme),
                description = stringResource(R.string.settings_dark_theme_description),
                checked = settings.darkThemeEnabled,
                tag = "settingsDarkThemeSwitch",
                onCheckedChange = onDarkThemeEnabledChange
            )
            LanguageSelector(
                selectedLanguage = uiState.language,
                onLanguageChange = onLanguageChange
            )
        }

        SettingsSection(
            title = stringResource(R.string.settings_data_title),
            tag = "settingsDataSection"
        ) {
            SwitchRow(
                title = stringResource(R.string.settings_auto_sync),
                description = stringResource(R.string.settings_auto_sync_description),
                checked = settings.autoSyncEnabled,
                tag = "settingsAutoSyncSwitch",
                onCheckedChange = onAutoSyncEnabledChange
            )
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun SettingsContentPreview() {
    ChaikaTheme {
        SettingsContent(
            uiState = SettingsUiState(
                settings = AppSettings(
                    notificationsEnabled = true,
                    soundEnabled = false,
                    vibrationEnabled = true,
                    darkThemeEnabled = false,
                    autoSyncEnabled = true
                ),
                language = AppLanguage.RU
            ),
            onNotificationsEnabledChange = {},
            onSoundEnabledChange = {},
            onVibrationEnabledChange = {},
            onDarkThemeEnabledChange = {},
            onAutoSyncEnabledChange = {},
            onLanguageChange = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun SettingsContentWidePreview() {
    ChaikaTheme {
        SettingsContent(
            uiState = SettingsUiState(language = AppLanguage.SYSTEM),
            onNotificationsEnabledChange = {},
            onSoundEnabledChange = {},
            onVibrationEnabledChange = {},
            onDarkThemeEnabledChange = {},
            onAutoSyncEnabledChange = {},
            onLanguageChange = {}
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    tag: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    tag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.testTag(tag)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languageOptions = listOf(
        AppLanguage.SYSTEM to stringResource(R.string.settings_language_system),
        AppLanguage.RU to stringResource(R.string.settings_language_russian),
        AppLanguage.EN to stringResource(R.string.settings_language_english)
    )
    val selectedLabel = languageOptions.firstOrNull { it.first == selectedLanguage }?.second
        ?: languageOptions.first().second

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settingsLanguageSelector")
            .padding(top = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_language_title),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.settings_language_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .testTag("settingsLanguageDropdown")
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { (language, label) ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            expanded = false
                            if (language != selectedLanguage) {
                                onLanguageChange(language)
                            }
                        },
                        modifier = Modifier.testTag("settingsLanguageOption_${language.name}")
                    )
                }
            }
        }
    }
}
