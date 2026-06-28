package com.chaikasoft.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview

@Composable
fun PersonalDataView(conductor: ConductorDomain?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.personal_data_basic_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PersonalDataRow(
                    label = stringResource(R.string.profile_family_name),
                    value = conductor?.familyName ?: "-"
                )
                PersonalDataRow(
                    label = stringResource(R.string.profile_first_name),
                    value = conductor?.name ?: "-"
                )
                PersonalDataRow(
                    label = stringResource(R.string.profile_middle_name),
                    value = conductor?.givenName ?: "-"
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.personal_data_service_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PersonalDataRow(
                    label = stringResource(R.string.profile_employee_id),
                    value = conductor?.employeeID ?: "-"
                )
                PersonalDataRow(
                    label = stringResource(R.string.personal_data_employee_id_label),
                    value = (conductor?.id ?: "-").toString()
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.personal_data_additional_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PersonalDataRow(
                    label = stringResource(R.string.profile_photo),
                    value = if (conductor?.image !=
                        null
                    ) {
                        stringResource(R.string.personal_data_photo_loaded)
                    } else {
                        stringResource(R.string.personal_data_photo_not_loaded)
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonalDataRow(label: String, value: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (maxWidth < 320.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.42f)
                )
                Text(
                    text = value,
                    modifier = Modifier.weight(0.58f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun PersonalDataViewPreview() {
    ChaikaTheme {
        PersonalDataView(conductor = previewConductor())
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun PersonalDataViewWidePreview() {
    ChaikaTheme {
        PersonalDataView(conductor = previewConductor())
    }
}

private fun previewConductor(): ConductorDomain = ConductorDomain(
    id = 1,
    name = "Александр",
    familyName = "Константинопольский",
    givenName = "Владимирович",
    employeeID = "EMP001",
    image = ""
)
