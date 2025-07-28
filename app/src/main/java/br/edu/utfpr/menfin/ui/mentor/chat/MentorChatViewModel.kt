package br.edu.utfpr.menfin.ui.mentor.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.ChatHistoryDao
import br.edu.utfpr.menfin.data.dao.FeedbackDao
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.ChatHistoryModel
import br.edu.utfpr.menfin.data.model.Sender
import br.edu.utfpr.menfin.data.service.GeminiPromptBuilder
import br.edu.utfpr.menfin.data.service.GeminiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MentorChatUiState(
    val messages: List<ChatHistoryModel> = listOf(),
    val question: String = "",
    val errorMessage: String = ""
)

class MentorChatViewModel(
    private val onboardingDao: OnboardingDao,
    private val transactionDao: TransactionDao,
    private val chatHistoryDao: ChatHistoryDao,
    private val feedbackDao: FeedbackDao,
    private val dataStore: DataStore,
    private val geminiService: GeminiService,
    private val promptBuilder: GeminiPromptBuilder
) : ViewModel() {
    var uiState: MentorChatUiState by mutableStateOf(MentorChatUiState())

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            try {
                val userId = dataStore.userLoggedFlow.first()?.id
                val chatHistory = chatHistoryDao.getChatHistoryByUser(userId!!)
                uiState = uiState.copy(messages = chatHistory)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro ao carregar histórico de chat: ${e.message}")
            }
        }
    }

    fun sendQuestion() {
        viewModelScope.launch {
            try {
                val userId = dataStore.userLoggedFlow.first()?.id

                val userMessage = buildUserMessage(userId!!)
                saveChatMessage(userMessage)
                val systemMessage = buildSystemMessage()

                uiState = uiState.copy(
                    messages = uiState.messages + userMessage + systemMessage,
                    errorMessage = "",
                    question = ""
                )

                val onboardingData = onboardingDao.findByUser(userId)
                val transactions = transactionDao.findAllByUser(userId)
                val feedbacks = feedbackDao.findAllByUser(userId)

                val prompt = promptBuilder.buildChatPrompt(
                    onboardingData = onboardingData!!,
                    transactions = transactions,
                    chatHistory = uiState.messages.dropLast(1),
                    feedbacks = feedbacks
                )

                val result = geminiService.getGenerativeContent(prompt)

                result.onSuccess { generatedAnswer ->
                    val mentorResponse = buildMentorResponse(userId, generatedAnswer)
                    saveChatMessage(mentorResponse)

                    uiState = uiState.copy(
                        messages = uiState.messages.dropLast(1) + mentorResponse,
                        errorMessage = ""
                    )
                }.onFailure { error ->
                    handleError(error.message ?: "Ocorreu um erro desconhecido.")
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Falha ao processar a solicitação.")
            }
        }
    }

    private fun buildMentorResponse(userId: Int, answer: String): ChatHistoryModel {
        return ChatHistoryModel(
            text = answer,
            userId = userId,
            sender = Sender.MENTOR,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun buildUserMessage(userId: Int): ChatHistoryModel {
        return ChatHistoryModel(
            text = uiState.question,
            userId = userId,
            sender = Sender.USER,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun buildSystemMessage(): ChatHistoryModel {
        return ChatHistoryModel(
            text = "",
            userId = 0,
            sender = Sender.SYSTEM_LOADER,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun saveChatMessage(message: ChatHistoryModel) {
        viewModelScope.launch {
            try {
                chatHistoryDao.saveChatHistory(message)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro ao salvar mensagem: ${e.message}")
            }
        }
    }

    fun onChangeQuestion(newQuestion: String) {
        uiState = uiState.copy(
            question = newQuestion
        )
    }

    private fun handleError(errorMessage: String) {
        uiState = uiState.copy(
            messages = uiState.messages.dropLast(1),
            errorMessage = "Desculpe, não consegui processar sua pergunta. Tente novamente. (Erro: $errorMessage)"
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!

                MentorChatViewModel(
                    onboardingDao = OnboardingDao(application),
                    transactionDao = TransactionDao(application),
                    dataStore = DataStore(application),
                    geminiService = GeminiService(),
                    promptBuilder = GeminiPromptBuilder(),
                    chatHistoryDao = ChatHistoryDao(application),
                    feedbackDao = FeedbackDao(application)
                )
            }
        }
    }
}