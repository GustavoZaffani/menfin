package br.edu.utfpr.menfin.ui.transaction.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.TransactionModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class TransactionClickAction {
    EDIT,
    DUPLICATE
}

data class TransactionListUiState(
    val loading: Boolean = false,
    val processingDelete: Boolean = false,
    val transactions: List<TransactionModel> = emptyList(),
    val showConfirmationDialog: Boolean = false,
    val transactionIdToDelete: Int = 0,
    val transactionDeleted: Boolean = false,
    val transactionPressed: TransactionModel? = null
) {
    val loadingMessage: String
        get() = if (loading) "Carregando lançamentos..." else if (processingDelete) "Excluindo lançamento..." else ""

    val hasAnyLoading: Boolean
        get() = loading || processingDelete
}

class TransactionListViewModel(
    val transactionDao: TransactionDao,
    val dataStore: DataStore
) : ViewModel() {

    var uiState: TransactionListUiState by mutableStateOf(TransactionListUiState())

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            uiState = uiState.copy(
                transactions = emptyList(),
                loading = true
            )

            delay(750)

            val userId = dataStore.userLoggedFlow.first()?.id ?: 0
            val transactions = transactionDao.findAllByUser(userId)

            uiState = uiState.copy(
                transactions = transactions,
                loading = false
            )
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            uiState = uiState.copy(
                processingDelete = true,
                showConfirmationDialog = false,
                transactionDeleted = false
            )

            delay(750)

            transactionDao.deleteById(uiState.transactionIdToDelete)

            uiState = uiState.copy(
                processingDelete = false,
                transactionDeleted = true,
                transactionIdToDelete = 0
            )
        }
    }

    fun showConfirmationDialog(transaction: TransactionModel) {
        uiState = uiState.copy(
            showConfirmationDialog = true,
            transactionIdToDelete = transaction._id!!
        )
    }

    fun dismissConfirmationDialog() {
        uiState = uiState.copy(
            showConfirmationDialog = false,
            transactionIdToDelete = 0
        )
    }

    fun onTransactionPressed(transaction: TransactionModel) {
        uiState = uiState.copy(
            transactionPressed = transaction
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]

                TransactionListViewModel(
                    transactionDao = TransactionDao(ctx = application!!),
                    dataStore = DataStore(context = application)
                )
            }
        }
    }
}
