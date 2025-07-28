package br.edu.utfpr.menfin.ui.goal.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.GoalDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.GoalModel
import br.edu.utfpr.menfin.data.model.GoalPriority
import br.edu.utfpr.menfin.extensions.toMillisFromBrazilianDateFormat
import br.edu.utfpr.menfin.ui.shared.utils.FormField
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.runValidations
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateFieldRequired
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateMonetaryValue
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateValueGreaterThanZero
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FormState(
    val description: FormField = FormField(),
    val value: FormField = FormField(),
    val priority: FormField = FormField(),
    val targetDate: FormField = FormField()
) {
    val isValid
        get(): Boolean = FormFieldUtils.isValid(
            listOf(
                description,
                value,
                priority,
                targetDate
            )
        )
}

data class GoalFormUiState(
    val formState: FormState = FormState(),
    val isSaving: Boolean = false,
    val goalSaved: Boolean = false
)

class GoalFormViewModel(
    private val goalDao: GoalDao,
    private val dataStore: DataStore
) : ViewModel() {

    var uiState: GoalFormUiState by mutableStateOf(GoalFormUiState())

    fun saveGoal() {
        if (!isValidForm()) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isSaving = true
            )

            val goal = GoalModel(
                _id = null,
                description = uiState.formState.description.value,
                value = uiState.formState.value.value.toDouble(),
                priority = GoalPriority.fromDescription(uiState.formState.priority.value),
                targetDate = uiState.formState.targetDate.value.toMillisFromBrazilianDateFormat()!!,
                userId = dataStore.userLoggedFlow.first()?.id ?: 0
            )

            goalDao.save(goal)

            uiState = uiState.copy(
                isSaving = false,
                goalSaved = true
            )
        }
    }

    fun onDescriptionChanged(description: String) {
        if (uiState.formState.description.value != description) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    description = uiState.formState.description.copy(
                        value = description,
                        errorMessageCode = validateFieldRequired(description)
                    )
                )
            )
        }
    }

    fun onValueChanged(value: String) {
        if (uiState.formState.value.value != value) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    value = uiState.formState.value.copy(
                        value = value,
                        errorMessageCode = runValidations(
                            value,
                            ::validateFieldRequired,
                            ::validateMonetaryValue,
                            ::validateValueGreaterThanZero
                        )
                    )
                )
            )
        }
    }

    fun onPriorityChanged(priority: String) {
        if (uiState.formState.priority.value != priority) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    priority = uiState.formState.priority.copy(
                        value = priority,
                        errorMessageCode = validateFieldRequired(priority)
                    )
                )
            )
        }
    }

    fun onTargetDateChanged(targetDate: String) {
        if (uiState.formState.targetDate.value != targetDate) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    targetDate = uiState.formState.targetDate.copy(
                        value = targetDate,
                        errorMessageCode = validateFieldRequired(targetDate)
                    )
                )
            )
        }
    }

    fun onClearDescription() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                description = uiState.formState.description.copy(value = "")
            )
        )
    }

    fun onClearValue() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                value = uiState.formState.value.copy(value = "")
            )
        )
    }

    fun onClearTargetDate() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                targetDate = uiState.formState.targetDate.copy(value = "")
            )
        )
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                description = uiState.formState.description.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.description.value)
                ),
                value = uiState.formState.value.copy(
                    errorMessageCode = runValidations(
                        uiState.formState.value.value,
                        ::validateFieldRequired,
                        ::validateMonetaryValue,
                        ::validateValueGreaterThanZero
                    )
                ),
                priority = uiState.formState.priority.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.priority.value)
                ),
                targetDate = uiState.formState.targetDate.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.targetDate.value)
                )
            )
        )

        return uiState.formState.isValid
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                GoalFormViewModel(
                    goalDao = GoalDao(ctx = application!!),
                    dataStore = DataStore(context = application)
                )
            }
        }
    }
}
