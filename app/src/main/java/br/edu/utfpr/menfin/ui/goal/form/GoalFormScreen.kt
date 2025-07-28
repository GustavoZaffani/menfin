package br.edu.utfpr.menfin.ui.goal.form

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
import br.edu.utfpr.menfin.data.model.GoalPriority
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.DefaultActionFormToolbar
import br.edu.utfpr.menfin.ui.shared.components.form.CurrencyField
import br.edu.utfpr.menfin.ui.shared.components.form.DatePickerField
import br.edu.utfpr.menfin.ui.shared.components.form.DropdownField
import br.edu.utfpr.menfin.ui.shared.components.form.TextField
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun GoalFormScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalFormViewModel = viewModel(factory = GoalFormViewModel.Factory),
    onBackPressed: () -> Unit,
    onGoalSaved: () -> Unit
) {
    val formState = viewModel.uiState.formState
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState.goalSaved) {
        if (viewModel.uiState.goalSaved) {
            Toast.makeText(
                context,
                R.string.goal_form_goal_saved,
                Toast.LENGTH_LONG
            ).show()
            onGoalSaved()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GoalAppBar(
                isSaving = viewModel.uiState.isSaving,
                onBackPressed = onBackPressed,
                onSavePressed = viewModel::saveGoal
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FormContent(
                formState = formState,
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onValueChanged = viewModel::onValueChanged,
                onPriorityChanged = viewModel::onPriorityChanged,
                onTargetDateChanged = viewModel::onTargetDateChanged,
                onClearDescription = viewModel::onClearDescription,
                onClearValue = viewModel::onClearValue,
                onClearTargetDate = viewModel::onClearTargetDate
            )
        }
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    onDescriptionChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    onPriorityChanged: (String) -> Unit,
    onTargetDateChanged: (String) -> Unit,
    onClearDescription: () -> Unit,
    onClearValue: () -> Unit,
    onClearTargetDate: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        DropdownField(
            selectedValue = formState.priority.value,
            label = stringResource(R.string.goal_form_priority_field),
            onValueChangedEvent = onPriorityChanged,
            errorMessageCode = formState.priority.errorMessageCode,
            options = GoalPriority.getDescriptionList()
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.goal_form_description_field),
            value = formState.description.value,
            onValueChange = onDescriptionChanged,
            errorMessageCode = formState.description.errorMessageCode,
            onClearValue = onClearDescription
        )
        CurrencyField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.goal_form_value_field),
            value = formState.value.value,
            onValueChange = onValueChanged,
            errorMessageCode = formState.value.errorMessageCode,
            onClearValue = onClearValue
        )
        DatePickerField(
            value = formState.targetDate.value,
            label = stringResource(R.string.goal_form_target_date_field),
            onValueChange = onTargetDateChanged,
            errorMessageCode = formState.targetDate.errorMessageCode,
            onClearValue = onClearTargetDate
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalFormContentPreview() {
    MenfinTheme {
        FormContent(
            formState = FormState(),
            onDescriptionChanged = {},
            onValueChanged = {},
            onPriorityChanged = {},
            onTargetDateChanged = {},
            onClearDescription = {},
            onClearValue = {},
            onClearTargetDate = {}
        )
    }
}

@Composable
private fun GoalAppBar(
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.goal_form_app_bar_title),
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
private fun GoalBarPreview() {
    MenfinTheme {
        GoalAppBar(
            onBackPressed = {},
            onSavePressed = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun GoalFormScreenPreview() {
    MenfinTheme {
        GoalFormScreen(
            onBackPressed = {},
            onGoalSaved = {}
        )
    }
}