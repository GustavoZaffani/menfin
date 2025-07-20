package br.edu.utfpr.menfin.ui.user.register

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.ui.shared.components.form.PasswordField
import br.edu.utfpr.menfin.ui.shared.components.form.TextField
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.shared.components.SectionHeader
import br.edu.utfpr.menfin.ui.shared.components.form.DatePickerField
import br.edu.utfpr.menfin.ui.shared.components.form.EmailField
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onRegisterSaved: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState.registerSaved) {
        if (viewModel.uiState.registerSaved) {
            Toast.makeText(
                context,
                "Usuário criado com sucesso!",
                Toast.LENGTH_LONG
            ).show()
            onRegisterSaved()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            WelcomeCard(
                icon = Icons.Outlined.People,
                user = viewModel.uiState.formState.name.value
            )
            FormContent(
                formState = viewModel.uiState.formState,
                allFormDisable = viewModel.uiState.isSaving,
                onNameChanged = viewModel::onNameChanged,
                onBirthdayChanged = viewModel::onBirthdayChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onUserChanged = viewModel::onUserChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onClearValueName = viewModel::onClearValueName,
                onClearValueBirthday = viewModel::onClearValueBirthday,
                onClearValueEmail = viewModel::onClearValueEmail,
                onClearValueUser = viewModel::onClearValueUser
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = viewModel::save
            ) {
                if (viewModel.uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(text = stringResource(R.string.generic_to_register))
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    MenfinTheme {
        RegisterScreen(
            onRegisterSaved = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    allFormDisable: Boolean = false,
    onNameChanged: (String) -> Unit,
    onBirthdayChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onUserChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClearValueName: () -> Unit,
    onClearValueBirthday: () -> Unit,
    onClearValueEmail: () -> Unit,
    onClearValueUser: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        SectionHeader(
            text = stringResource(R.string.register_section_general_data)
        )
        TextField(
            label = stringResource(R.string.register_field_name),
            value = formState.name.value,
            onValueChange = onNameChanged,
            errorMessageCode = formState.name.errorMessageCode,
            onClearValue = onClearValueName,
            enabled = !allFormDisable
        )
        DatePickerField(
            label = "Data de nascimento",
            value = formState.birthday.value,
            onValueChange = onBirthdayChanged,
            errorMessageCode = formState.birthday.errorMessageCode,
            onClearValue = onClearValueBirthday
        )
        EmailField(
            label = stringResource(R.string.register_field_email),
            value = formState.email.value,
            onValueChange = onEmailChanged,
            errorMessageCode = formState.email.errorMessageCode,
            onClearValue = onClearValueEmail,
            enabled = !allFormDisable
        )
        SectionHeader(
            text = stringResource(R.string.register_section_auth)
        )
        TextField(
            label = "Usuário",
            value = formState.user.value,
            onValueChange = onUserChanged,
            errorMessageCode = formState.user.errorMessageCode,
            onClearValue = onClearValueUser,
            enabled = !allFormDisable
        )
        PasswordField(
            label = stringResource(R.string.register_field_password),
            value = formState.password.value,
            onValueChange = onPasswordChanged,
            errorMessageCode = formState.password.errorMessageCode,
            keyboardImeAction = ImeAction.Done,
            enabled = !allFormDisable
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormContentPreview() {
    MenfinTheme {
        FormContent(
            formState = FormState(),
            onNameChanged = {},
            onBirthdayChanged = {},
            onEmailChanged = {},
            onUserChanged = {},
            onPasswordChanged = {},
            onClearValueName = {},
            onClearValueBirthday = {},
            onClearValueEmail = {},
            onClearValueUser = {}
        )
    }
}
