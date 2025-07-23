package br.edu.utfpr.menfin.ui.user.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.shared.components.ClickableTextDefault
import br.edu.utfpr.menfin.ui.shared.components.Loading
import br.edu.utfpr.menfin.ui.shared.components.form.PasswordField
import br.edu.utfpr.menfin.ui.shared.components.form.TextField
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
    onClickNewRegister: () -> Unit,
    onLoginSuccess: (Boolean) -> Unit
) {
    LaunchedEffect(viewModel.uiState.loginSuccess) {
        if (viewModel.uiState.loginSuccess) {
            onLoginSuccess(viewModel.uiState.onboardingIsDone)
        }
    }

    if (viewModel.uiState.isProcessing) {
        Loading(text = stringResource(R.string.login_loading))
    } else {
        LoginContent(
            modifier = modifier.fillMaxSize(),
            formState = viewModel.uiState.formState,
            onUserChanged = viewModel::onUserChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onClearLogin = viewModel::onClearUser,
            onLogin = viewModel::login,
            onClickNewRegister = onClickNewRegister
        )
    }

}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    formState: FormState,
    onUserChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClearLogin: () -> Unit,
    onLogin: () -> Unit,
    onClickNewRegister: () -> Unit

) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Rounded.SupervisedUserCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )

        TextField(
            label = stringResource(R.string.login_field_user),
            value = formState.user.value,
            errorMessageCode = formState.user.errorMessageCode,
            onValueChange = onUserChanged,
            onClearValue = onClearLogin,
            keyboardCapitalization = KeyboardCapitalization.None
        )

        PasswordField(
            label = stringResource(R.string.login_field_password),
            value = formState.password.value,
            errorMessageCode = formState.password.errorMessageCode,
            onValueChange = onPasswordChanged,
            keyboardImeAction = ImeAction.Done
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = onLogin
        ) {
            Text(text = stringResource(R.string.generic_to_enter))
        }

        ClickableTextDefault(
            modifier = Modifier.padding(top = 8.dp),
            preText = stringResource(R.string.login_new_register_title),
            clickText = stringResource(R.string.login_new_register_action),
            onClick = onClickNewRegister
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MenfinTheme {
        LoginScreen(
            onClickNewRegister = {},
            onLoginSuccess = {}
        )
    }
}
