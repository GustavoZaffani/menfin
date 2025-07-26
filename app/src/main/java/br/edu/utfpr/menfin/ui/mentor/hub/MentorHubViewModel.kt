package br.edu.utfpr.menfin.ui.mentor.hub

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.service.GeminiPromptBuilder
import br.edu.utfpr.menfin.data.service.GeminiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class QuickQuestion(val description: String) {
    HOW_TO_SAVE("Onde posso economizar?"),
    BIG_MONTH_EXPENSES("Qual foi meu maior gasto deste mês?"),
    LAUNCH_EXPENSES("Quanto gastei com alimentação?")
}

data class MentorHubUiState(
    val loadingAnswer: Boolean = false,
    val selectedQuestion: QuickQuestion? = null,
    val answer: String = "",
    val errorMessage: String = "",
)

class MentorHubViewModel(
    private val onboardingDao: OnboardingDao,
    private val transactionDao: TransactionDao,
    private val dataStore: DataStore,
    private val geminiService: GeminiService,
    private val promptBuilder: GeminiPromptBuilder
) : ViewModel() {

    var uiState: MentorHubUiState by mutableStateOf(MentorHubUiState())

    fun onQuestionSelected(question: QuickQuestion) {
        viewModelScope.launch {
            uiState = uiState.copy(
                selectedQuestion = question,
                errorMessage = "",
                loadingAnswer = true
            )

            try {
                val userId = dataStore.userLoggedFlow.first()?.id
                val onboardingData = onboardingDao.findByUser(userId!!)
                val transactions = transactionDao.findAllByUser(userId)

                val prompt = promptBuilder.buildPrompt(
                    onboardingData = onboardingData!!,
                    transactions = transactions,
                    question = question.description
                )

                val result = geminiService.getGenerativeContent(prompt)

                result.onSuccess { generatedAnswer ->
                    uiState = uiState.copy(
                        loadingAnswer = false,
                        answer = generatedAnswer
                    )
                }.onFailure { error ->
                    handleError(error.message ?: "Ocorreu um erro desconhecido.")
                }

            } catch (e: Exception) {
                handleError(e.message ?: "Falha ao processar a solicitação.")
            }
        }
    }

    private fun handleError(errorMessage: String) {
        uiState = uiState.copy(
            loadingAnswer = false,
            errorMessage = "Desculpe, não consegui processar sua pergunta. Tente novamente. (Erro: $errorMessage)"
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!

                MentorHubViewModel(
                    onboardingDao = OnboardingDao(application),
                    transactionDao = TransactionDao(application),
                    dataStore = DataStore(application),
                    geminiService = GeminiService(),
                    promptBuilder = GeminiPromptBuilder()
                )
            }
        }
    }

}