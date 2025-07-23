package br.edu.utfpr.menfin.ui.user.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.dao.UserDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.local.UserLogged
import br.edu.utfpr.menfin.ui.shared.utils.FormField
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateFieldRequired
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FormState(
    val user: FormField = FormField(),
    val password: FormField = FormField()
) {
    val isValid
        get(): Boolean = FormFieldUtils.isValid(
            listOf(
                user,
                password
            )
        )
}

data class LoginRegisterUiState(
    val formState: FormState = FormState(),
    val isProcessing: Boolean = false,
    val loginSuccess: Boolean = false,
    val onboardingIsDone: Boolean = false,
)


class LoginViewModel(
    private val dataStore: DataStore,
    private val userDao: UserDao,
    private val onboardingDao: OnboardingDao
) : ViewModel() {

    var uiState: LoginRegisterUiState by mutableStateOf(LoginRegisterUiState())

    fun login() {
        if (!isValidForm()) {
            return
        }

        uiState = uiState.copy(
            isProcessing = true
        )

        viewModelScope.launch {
            delay(1000)
            userDao.findByUser(uiState.formState.user.value).let { user ->
                if (user == null) {
                    uiState = uiState.copy(
                        isProcessing = false,
                        formState = uiState.formState.copy(
                            user = uiState.formState.user.copy(
                                errorMessageCode = R.string.login_user_not_found
                            )
                        )
                    )
                } else if (user.password != uiState.formState.password.value) {
                    uiState = uiState.copy(
                        isProcessing = false,
                        formState = uiState.formState.copy(
                            password = uiState.formState.password.copy(
                                errorMessageCode = R.string.login_password_is_invalid
                            )
                        )
                    )
                } else {
                    dataStore.saveUserLogged(
                        UserLogged(user._id!!, user.name, user.user))
                    val onboardingIsDone = onboardingDao.findByUser(user._id) != null
                    uiState = uiState.copy(
                        isProcessing = false,
                        loginSuccess = true,
                        onboardingIsDone = onboardingIsDone
                    )
                }
            }
        }
    }

    fun onUserChanged(user: String) {
        if (uiState.formState.user.value != user) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    user = uiState.formState.user.copy(
                        value = user.lowercase(),
                        errorMessageCode = validateFieldRequired(user)
                    )
                )
            )
        }
    }

    fun onPasswordChanged(password: String) {
        if (uiState.formState.password.value != password) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    password = uiState.formState.password.copy(
                        value = password,
                        errorMessageCode = validateFieldRequired(password)
                    )
                )
            )
        }
    }

    fun onClearUser() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                user = uiState.formState.user.copy(
                    value = ""
                )
            )
        )
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                user = uiState.formState.user.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.user.value)
                ),
                password = uiState.formState.password.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.password.value)
                )
            )
        )

        return uiState.formState.isValid
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                LoginViewModel(
                    dataStore = DataStore(context = application!!),
                    userDao = UserDao(ctx = application),
                    onboardingDao = OnboardingDao(ctx = application)
                )
            }
        }
    }
}