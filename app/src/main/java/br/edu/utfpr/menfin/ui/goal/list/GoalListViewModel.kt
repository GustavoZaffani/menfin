package br.edu.utfpr.menfin.ui.goal.list

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.FeedbackDao
import br.edu.utfpr.menfin.data.dao.GoalDao
import br.edu.utfpr.menfin.data.dao.OnboardingDao
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.GoalModel
import br.edu.utfpr.menfin.data.service.GeminiPromptBuilder
import br.edu.utfpr.menfin.data.service.GeminiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class InsightType(val icon: ImageVector, val color: Color) {
    ATTENTION(Icons.Default.Warning, Color(0xFFFFA000)),
    POSITIVE(Icons.Default.CheckCircle, Color(0xFF388E3C))
}

@Serializable
data class InsightItem(
    val text: String,
    val type: InsightType
)

data class GoalListUiState(
    val loading: Boolean = false,
    val goals: List<GoalModel> = emptyList(),
    val insights: List<InsightItem> = emptyList(),
    val showInsights: Boolean = false,
    val generatingInsights: Boolean = false
) {
    val hasAnyLoading: Boolean get() = loading || generatingInsights
    val loadingText: String get() = if (generatingInsights) "Gerando insights..." else "Carregando metas..."
}

class GoalListViewModel(
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val feedbackDao: FeedbackDao,
    private val onboardingDao: OnboardingDao,
    private val dataStore: DataStore,
    private val geminiService: GeminiService,
    private val promptBuilder: GeminiPromptBuilder
) : ViewModel() {

    private val jsonParser = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    var uiState: GoalListUiState by mutableStateOf(GoalListUiState())

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {
            uiState = uiState.copy(
                goals = emptyList(),
                loading = true
            )

            delay(750)

            val userId = dataStore.userLoggedFlow.first()?.id ?: 0
            val goals = goalDao.findAllByUserId(userId)

            uiState = uiState.copy(
                goals = goals,
                loading = false
            )
        }
    }

    fun onGenerateInsights() {
        viewModelScope.launch {
            uiState = uiState.copy(
                generatingInsights = true,
                insights = emptyList(),
                showInsights = false
            )

            val userId = dataStore.userLoggedFlow.first()?.id
            val onboardingData = onboardingDao.findByUser(userId!!)
            val transactions = transactionDao.findAllByUser(userId)
            val feedbacks = feedbackDao.findAllByUser(userId)
            val goals = goalDao.findAllByUserId(userId)

            val prompt = promptBuilder.buildGoalInsightsPrompt(
                onboardingData = onboardingData!!,
                transactions = transactions,
                feedbacks = feedbacks,
                goals = goals
            )

            geminiService.getGenerativeContent(prompt)
                .onSuccess { generatedInsights ->
                    val insights = jsonParser.decodeFromString<List<InsightItem>>(generatedInsights
                        .replace(Regex("```[a-zA-Z]*"), "")
                        .replace("```", "").trim())

                    uiState = uiState.copy(
                        generatingInsights = false,
                        insights = insights,
                        showInsights = true
                    )
                }.onFailure { error ->
                    Log.e("GoalListViewModel", "Error generating insights: ${error.message}")
                    uiState = uiState.copy(
                        generatingInsights = false,
                        showInsights = false,
                        insights = emptyList()
                    )
                }
        }
    }

    fun onCloseInsights() {
        uiState = uiState.copy(
            generatingInsights = false,
            showInsights = false,
            insights = emptyList()
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                GoalListViewModel(
                    goalDao = GoalDao(ctx = application!!),
                    dataStore = DataStore(context = application),
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
