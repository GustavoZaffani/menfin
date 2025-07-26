package br.edu.utfpr.menfin.ui.transaction.form

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.model.TransactionCategory
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.DefaultActionFormToolbar
import br.edu.utfpr.menfin.ui.shared.components.form.CurrencyField
import br.edu.utfpr.menfin.ui.shared.components.form.DatePickerField
import br.edu.utfpr.menfin.ui.shared.components.form.DropdownField
import br.edu.utfpr.menfin.ui.shared.components.form.TextField
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun TransactionFormScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionFormViewModel = viewModel(factory = TransactionFormViewModel.Factory),
    onBackPressed: () -> Unit,
    onTransactionSaved: () -> Unit
) {
    val formState = viewModel.uiState.formState
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState.transactionSaved) {
        if (viewModel.uiState.transactionSaved) {
            Toast.makeText(
                context,
                R.string.transaction_form_transaction_saved,
                Toast.LENGTH_LONG
            ).show()
            onTransactionSaved()
        }
    }

    LaunchedEffect(viewModel.uiState.generalErrorMessage) {
        if (viewModel.uiState.generalErrorMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                viewModel.uiState.generalErrorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TransactionAppBar(
                isNewTransaction = viewModel.uiState.isNewTransaction,
                isSaving = viewModel.uiState.isSaving,
                onBackPressed = onBackPressed,
                onSavePressed = viewModel::saveTransaction
            )
        }
    ) { innerPadding ->
        FormContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            formState = formState,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onValueChanged = viewModel::onValueChanged,
            onCategoryChanged = viewModel::onCategoryChanged,
            onTypeChanged = viewModel::onTypeChanged,
            onDateChanged = viewModel::onDateChanged,
            onClearDescription = viewModel::onClearDescription,
            onClearValue = viewModel::onClearValue,
            onClearDate = viewModel::onClearDate
        )

    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    onDescriptionChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onTypeChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onClearDescription: () -> Unit,
    onClearValue: () -> Unit,
    onClearDate: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        DropdownField(
            selectedValue = formState.type.value,
            label = stringResource(R.string.transaction_form_transaction_type_field),
            onValueChangedEvent = onTypeChanged,
            errorMessageCode = formState.type.errorMessageCode,
            options = TransactionType.getDescriptionList()
        )
        CurrencyField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.transaction_form_value_field),
            value = formState.value.value,
            onValueChange = onValueChanged,
            errorMessageCode = formState.value.errorMessageCode,
            onClearValue = onClearValue
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.transaction_form_description_field),
            value = formState.description.value,
            onValueChange = onDescriptionChanged,
            errorMessageCode = formState.description.errorMessageCode,
            onClearValue = onClearDescription
        )
        DropdownField(
            selectedValue = formState.category.value,
            label = stringResource(R.string.transaction_form_category_field),
            onValueChangedEvent = onCategoryChanged,
            errorMessageCode = formState.category.errorMessageCode,
            options = TransactionCategory.getDescriptionList()
        )
        DatePickerField(
            value = formState.date.value,
            label = stringResource(R.string.transaction_form_date_field),
            onValueChange = onDateChanged,
            errorMessageCode = formState.date.errorMessageCode,
            onClearValue = onClearDate
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionFormContentPreview() {
    MenfinTheme {
        FormContent(
            formState = FormState(),
            onDescriptionChanged = {},
            onValueChanged = {},
            onCategoryChanged = {},
            onTypeChanged = {},
            onDateChanged = {},
            onClearDescription = {},
            onClearValue = {},
            onClearDate = {}
        )
    }
}

@Composable
private fun TransactionAppBar(
    modifier: Modifier = Modifier,
    isNewTransaction: Boolean,
    isSaving: Boolean = false,
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    val title = if (isNewTransaction) {
        stringResource(R.string.transaction_form_app_bar_title_new_transaction)
    } else stringResource(R.string.transaction_form_app_bar_title_edit_transaction)

    AppBar(
        modifier = modifier,
        title = title,
        showActions = true,
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.generic_to_back)
                )
            }
        },
        actions = {
            DefaultActionFormToolbar(
                isSaving = isSaving,
                onSavePressed = onSavePressed
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionBarPreview() {
    MenfinTheme {
        TransactionAppBar(
            isNewTransaction = true,
            onBackPressed = {},
            onSavePressed = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TransactionListScreenPreview() {
    MenfinTheme {
        TransactionFormScreen(
            onBackPressed = {},
            onTransactionSaved = {}
        )
    }
}
