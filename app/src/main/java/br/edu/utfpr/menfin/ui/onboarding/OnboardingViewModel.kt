package br.edu.utfpr.menfin.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.OnboardingModel
import br.edu.utfpr.menfin.ui.shared.utils.FormField
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.isValid
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateFieldRequired
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class YesOrNo(val label: String) {
    YES("Sim"),
    NO("Não")
}

enum class KnowledgeLevel(val label: String) {
    NONE("Nenhum"),
    BEGINNER("Baixo"),
    INTERMEDIATE("Médio"),
    ADVANCED("Alto")
}

enum class ReadyToStart(val label: String) {
    YES("Sim"),
    OF_COURSE("Com certeza"),
}

data class FormState(
    val remuneration: FormField = FormField(),
    val isNegative: FormField = FormField(),
    val hasDependents: FormField = FormField(),
    val knowledgeLevel: FormField = FormField(),
    val mainGoal: FormField = FormField(),
    val isReady: FormField = FormField(),
) {
    val firstQuestionsValid
        get(): Boolean = isValid(
            listOf(
                remuneration,
                isNegative,
                hasDependents
            )
        )

    val secondQuestionsValid
        get(): Boolean = isValid(
            listOf(
                knowledgeLevel,
                mainGoal,
                isReady
            )
        )
}

data class OnboardingUiState(
    val currentStep: Int = 1,
    val formState: FormState = FormState(),
    val isSaving: Boolean = false,
    val onBoardingFinished: Boolean = false
)

class OnboardingViewModel(
    val onboardingDao: OnboardingDao,
    val dataStore: DataStore
) : ViewModel() {

    var uiState: OnboardingUiState by mutableStateOf(OnboardingUiState())

    private fun secondStepIsValid(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                knowledgeLevel = uiState.formState.knowledgeLevel.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.knowledgeLevel.value)
                ),
                mainGoal = uiState.formState.mainGoal.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.mainGoal.value)
                ),
                isReady = uiState.formState.isReady.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.isReady.value)
                )
            )
        )

        return uiState.formState.secondQuestionsValid
    }

    private fun firstStepIsValid(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                remuneration = uiState.formState.remuneration.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.remuneration.value)
                ),
                isNegative = uiState.formState.isNegative.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.isNegative.value)
                ),
                hasDependents = uiState.formState.hasDependents.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.hasDependents.value)
                )
            )
        )

        return uiState.formState.firstQuestionsValid
    }

    fun onPreviousStep() {
        uiState = uiState.copy(currentStep = 1)
    }

    fun onNextStep() {
        if (firstStepIsValid()) {
            uiState = uiState.copy(currentStep = 2)
        }
    }

    fun onSubmitOnboarding() {
        if (secondStepIsValid()) {
            viewModelScope.launch {
                uiState = uiState.copy(
                    isSaving = true
                )

                val onboardingModel = OnboardingModel(
                    _id = null,
                    userId = dataStore.userLoggedFlow.first()!!.id,
                    remuneration = uiState.formState.remuneration.value.toDouble(),
                    isNegative = uiState.formState.isNegative.value,
                    hasDependents = uiState.formState.hasDependents.value,
                    knowledgeLevel = uiState.formState.knowledgeLevel.value,
                    mainGoal = uiState.formState.mainGoal.value,
                    isReady = uiState.formState.isReady.value
                )

                onboardingDao.saveOnboarding(onboardingModel)

                uiState = uiState.copy(
                    isSaving = false,
                    onBoardingFinished = true
                )
            }
        }
    }

    fun onRemunerationChanged(remuneration: String) {
        if (uiState.formState.remuneration.value != remuneration) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    remuneration = uiState.formState.remuneration.copy(
                        value = remuneration,
                        errorMessageCode = validateFieldRequired(remuneration)
                    )
                )
            )
        }
    }

    fun onIsNegativeChanged(isNegative: String) {
        if (uiState.formState.isNegative.value != isNegative) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    isNegative = uiState.formState.isNegative.copy(
                        value = isNegative,
                        errorMessageCode = validateFieldRequired(isNegative)
                    )
                )
            )
        }
    }

    fun onHasDependentsChanged(hasDependents: String) {
        if (uiState.formState.hasDependents.value != hasDependents) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    hasDependents = uiState.formState.hasDependents.copy(
                        value = hasDependents,
                        errorMessageCode = validateFieldRequired(hasDependents)
                    )
                )
            )
        }
    }

    fun onKnowledgeLevelChanged(knowledgeLevel: String) {
        if (uiState.formState.knowledgeLevel.value != knowledgeLevel) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    knowledgeLevel = uiState.formState.knowledgeLevel.copy(
                        value = knowledgeLevel,
                        errorMessageCode = validateFieldRequired(knowledgeLevel)
                    )
                )
            )
        }
    }

    fun onMainGoalChanged(mainGoal: String) {
        if (uiState.formState.mainGoal.value != mainGoal) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    mainGoal = uiState.formState.mainGoal.copy(
                        value = mainGoal,
                        errorMessageCode = validateFieldRequired(mainGoal)
                    )
                )
            )
        }
    }

    fun onReadyToStartChanged(isReady: String) {
        if (uiState.formState.isReady.value != isReady) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    isReady = uiState.formState.isReady.copy(
                        value = isReady,
                        errorMessageCode = validateFieldRequired(isReady)
                    )
                )
            )
        }
    }

    fun onClearRemuneration() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                remuneration = uiState.formState.remuneration.copy(
                    value = ""
                )
            )
        )
    }

    fun onClearMainGoal() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                mainGoal = uiState.formState.mainGoal.copy(
                    value = ""
                )
            )
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                OnboardingViewModel(
                    onboardingDao = OnboardingDao(ctx = application!!),
                    dataStore = DataStore(context = application)
                )
            }
        }
    }
}
