package br.edu.utfpr.menfin.ui.shared.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.model.TransactionCategory
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    modifier: Modifier = Modifier,
    selectedValue: String,
    options: List<String>,
    label: String,
    @StringRes
    errorMessageCode: Int? = null,
    enabled: Boolean = true,
    onValueChangedEvent: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Column {
            CustomTextField(
                modifier = modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = label,
                value = selectedValue,
                onValueChange = onValueChangedEvent,
                readOnly = true,
                enabled = enabled,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option: String ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            expanded = false
                            onValueChangedEvent(option)
                        }
                    )
                }
            }

            errorMessageCode?.let {
                Text(
                    text = stringResource(errorMessageCode),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropdownFieldPreview() {
    MenfinTheme {
        DropdownField(
            selectedValue = TransactionCategory.SALARY.label,
            label = "Unidade de medida",
            onValueChangedEvent = {},
            options = listOf(
                TransactionCategory.SALARY.label,
                TransactionCategory.FOOD.label,
                TransactionCategory.OTHER.label
            ),
            errorMessageCode = R.string.error_field_required
        )
    }
}