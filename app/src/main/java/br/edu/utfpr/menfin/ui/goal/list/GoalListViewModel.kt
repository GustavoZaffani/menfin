package br.edu.utfpr.menfin.ui.goal.list

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GoalListUiState(
    val loading: Boolean = false,
    val goals: List<GoalModel> = emptyList()
)

class GoalListViewModel(
    val goalDao: GoalDao,
    val dataStore: DataStore
) : ViewModel() {

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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                GoalListViewModel(
                    goalDao = GoalDao(ctx = application!!),
                    dataStore = DataStore(context = application)
                )
            }
        }
    }
}
