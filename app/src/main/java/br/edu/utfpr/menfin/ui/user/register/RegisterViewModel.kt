package br.edu.utfpr.menfin.ui.user.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.dao.UserDao
import br.edu.utfpr.menfin.data.model.UserModel
import br.edu.utfpr.menfin.extensions.toMillisFromBrazilianDateFormat
import br.edu.utfpr.menfin.ui.shared.utils.FormField
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.runValidations
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateEmailFormat
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateFieldRequired
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateLettersOnly
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateMinLength
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validatePasswordComplexity
import kotlinx.coroutines.launch

data class FormState(
    val name: FormField = FormField(),
    val birthday: FormField = FormField(),
    val email: FormField = FormField(),
    val user: FormField = FormField(),
    val password: FormField = FormField(),
) {
    val isValid
        get(): Boolean = FormFieldUtils.isValid(
            listOf(
                name,
                email,
                birthday,
                email,
                user,
                password
            )
        )
}

data class RegisterUiState(
    val formState: FormState = FormState(),
    val isSaving: Boolean = false,
    val registerSaved: Boolean = false
)

class RegisterViewModel(private val userDao: UserDao) : ViewModel() {

    private val tag: String = "RegisterViewModel"
    var uiState: RegisterUiState by mutableStateOf(RegisterUiState())

    fun save() {
        if (!isValidForm()) {
            return
        }

        uiState = uiState.copy(
            isSaving = true
        )

        viewModelScope.launch {
            val userModel = UserModel(
                _id = null,
                name = uiState.formState.name.value,
                birthday = uiState.formState.birthday.value.toMillisFromBrazilianDateFormat()!!,
                email = uiState.formState.email.value,
                user = uiState.formState.user.value,
                password = uiState.formState.password.value
            )

            userDao.saveUser(userModel)
            userDao.saveUser(userModel)

            uiState = uiState.copy(
                isSaving = false,
                registerSaved = true
            )
        }
    }

    fun onNameChanged(name: String) {
        if (uiState.formState.name.value != name) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    name = uiState.formState.name.copy(
                        value = name,
                        errorMessageCode = validateFieldRequired(name)
                    )
                )
            )
        }
    }

    fun onBirthdayChanged(birthday: String) {
        if (uiState.formState.birthday.value != birthday) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    birthday = uiState.formState.birthday.copy(
                        value = birthday,
                        errorMessageCode = validateFieldRequired(birthday)
                    )
                )
            )
        }
    }

    fun onEmailChanged(email: String) {
        if (uiState.formState.email.value != email) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    email = uiState.formState.email.copy(
                        value = email,
                        errorMessageCode = runValidations(
                            email,
                            ::validateFieldRequired,
                            ::validateEmailFormat
                        )
                    )
                )
            )
        }
    }

    fun onUserChanged(user: String) {
        if (uiState.formState.user.value != user) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    user = uiState.formState.user.copy(
                        value = user.lowercase(),
                        errorMessageCode = runValidations(
                            user,
                            ::validateFieldRequired,
                            ::validateLettersOnly,
                            ::validateIfExistsUser
                        )
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
                        errorMessageCode = runValidations(
                            password,
                            ::validateFieldRequired,
                            validateMinLength(8),
                            ::validatePasswordComplexity
                        )
                    )
                )
            )
        }
    }

    fun onClearValueName() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                name = uiState.formState.name.copy(
                    value = ""
                )
            )
        )
    }

    fun onClearValueBirthday() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                birthday = uiState.formState.birthday.copy(
                    value = ""
                )
            )
        )
    }

    fun onClearValueEmail() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                email = uiState.formState.email.copy(
                    value = ""
                )
            )
        )
    }

    fun onClearValueUser() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                user = uiState.formState.user.copy(
                    value = ""
                )
            )
        )
    }

    fun validateIfExistsUser(user: String): Int? {
        val userFounded = userDao.findByUser(user)

        return if (userFounded != null) {
            R.string.register_error_user_already_exists
        } else null
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                name = uiState.formState.name.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.name.value)
                ),
                birthday = uiState.formState.birthday.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.birthday.value)
                ),
                email = uiState.formState.email.copy(
                    errorMessageCode = runValidations(
                        uiState.formState.email.value,
                        ::validateFieldRequired,
                        ::validateEmailFormat
                    )
                ),
                user = uiState.formState.user.copy(
                    errorMessageCode = runValidations(
                        uiState.formState.user.value,
                        ::validateFieldRequired,
                        ::validateLettersOnly,
                        ::validateIfExistsUser
                    )
                ),
                password = uiState.formState.password.copy(
                    errorMessageCode = runValidations(
                        uiState.formState.password.value,
                        ::validateFieldRequired,
                        validateMinLength(8),
                        ::validatePasswordComplexity
                    )
                )
            )
        )

        return uiState.formState.isValid
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                RegisterViewModel(
                    userDao = UserDao(ctx = application!!)
                )
            }
        }
    }
}