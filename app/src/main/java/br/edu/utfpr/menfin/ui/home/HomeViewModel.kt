package br.edu.utfpr.menfin.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.FeedbackDao
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.TransactionModel
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.data.service.GeminiPromptBuilder
import br.edu.utfpr.menfin.data.service.GeminiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val loading: Boolean = false,
    val errorMessage: String = "",
    val currentMonth: Calendar = Calendar.getInstance(),
    val revenue: Double = 0.0,
    val expenses: Double = 0.0,
    val insights: List<String> = emptyList(),
) {
    val balance: Double
        get() = revenue - expenses
}

class HomeViewModel(
    private val transactionDao: TransactionDao,
    private val feedbackDao: FeedbackDao,
    private val onboardingDao: OnboardingDao,
    private val dataStore: DataStore,
    private val geminiService: GeminiService,
    private val promptBuilder: GeminiPromptBuilder
) : ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState())

    init {
        loadInfos()
    }

    private fun loadInfos() {
        viewModelScope.launch {
            uiState = uiState.copy(
                loading = true
            )

            val userId = dataStore.userLoggedFlow.first()!!.id
            val targetDate = uiState.currentMonth.apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val transactions = transactionDao.findByUserAndMonth(userId, targetDate)
            val onboardingData = onboardingDao.findByUser(userId)
            val feedbacks = feedbackDao.findAllByUser(userId)
            val values = getExpenseAndRevenueValues(transactions)

            val prompt = promptBuilder.buildInsightsHomePrompt(
                onboardingData = onboardingData!!,
                transactions = transactions,
                feedbacks = feedbacks
            )

            geminiService.getGenerativeContent(prompt).onSuccess { insights ->
                uiState = uiState.copy(
                    insights = insights.split(";").map { it.trim() },
                    revenue = values.first,
                    expenses = values.second,
                    loading = false
                )
            }.onFailure {
                uiState = uiState.copy(
                    errorMessage = "Ocorreu um erro ao obter os dados do mês.",
                    loading = false
                )
            }
        }
    }

    private fun getExpenseAndRevenueValues(
        transactions: List<TransactionModel>
    ): Pair<Double, Double> {
        var revenue = 0.0
        var expenses = 0.0

        transactions.forEach { transaction ->
            if (TransactionType.valueOf(transaction.type) == TransactionType.REVENUE) {
                revenue += transaction.value
            } else {
                expenses += transaction.value
            }
        }

        return Pair(revenue, expenses)
    }

    fun onPreviousMonth() {
        uiState = uiState.copy(
            currentMonth = (uiState.currentMonth.clone() as Calendar).apply {
                add(
                    Calendar.MONTH,
                    -1
                )
            }
        )
        loadInfos()
    }

    fun onNextMonth() {
        uiState = uiState.copy(
            currentMonth = (uiState.currentMonth.clone() as Calendar).apply {
                add(
                    Calendar.MONTH,
                    1
                )
            }
        )
        loadInfos()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                HomeViewModel(
                    dataStore = DataStore(context = application!!),
                    feedbackDao = FeedbackDao(ctx = application),
                    transactionDao = TransactionDao(ctx = application),
                    onboardingDao = OnboardingDao(ctx = application),
                    geminiService = GeminiService(),
                    promptBuilder = GeminiPromptBuilder()
                )
            }
        }
    }
}