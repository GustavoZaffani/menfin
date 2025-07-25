package br.edu.utfpr.menfin.ui.transaction.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.dao.TransactionDao
import br.edu.utfpr.menfin.data.local.DataStore
import br.edu.utfpr.menfin.data.model.TransactionCategory
import br.edu.utfpr.menfin.data.model.TransactionModel
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.extensions.toBrazilianDateFormat
import br.edu.utfpr.menfin.extensions.toMillisFromBrazilianDateFormat
import br.edu.utfpr.menfin.ui.Arguments
import br.edu.utfpr.menfin.ui.shared.utils.FormField
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.runValidations
import br.edu.utfpr.menfin.ui.shared.utils.FormFieldUtils.Companion.validateFieldRequired
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FormState(
    val type: FormField = FormField(),
    val value: FormField = FormField(),
    val description: FormField = FormField(),
    val category: FormField = FormField(),
    val date: FormField = FormField()
) {
    val isValid
        get(): Boolean = FormFieldUtils.isValid(
            listOf(
                type,
                value,
                description,
                category,
                date
            )
        )
}

data class TransactionFormUiState(
    val transactionId: Int = 0,
    val isTransactionLoading: Boolean = false,
    val formState: FormState = FormState(),
    val isSaving: Boolean = false,
    val transactionSaved: Boolean = false,
    val generalErrorMessage: String = ""
) {
    val isNewTransaction: Boolean get() = transactionId == 0
}

class TransactionFormViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val transactionDao: TransactionDao,
    private val dataStore: DataStore
) : ViewModel() {

    var uiState: TransactionFormUiState by mutableStateOf(TransactionFormUiState())
    private val transactionId: Int? =
        savedStateHandle.get<String>(Arguments.TRANSACTION_ID)?.toInt()

    init {
        if (transactionId != null) {
            uiState = uiState.copy(
                transactionId = transactionId
            )
            loadTransaction()
        }
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            uiState = uiState.copy(
                isTransactionLoading = true
            )
            val transaction = transactionDao.findById(transactionId!!)
            uiState = uiState.copy(
                isTransactionLoading = false,
                formState = FormState(
                    type = FormField(TransactionType.valueOf(transaction!!.type).label),
                    value = FormField(transaction.value.toString()),
                    description = FormField(transaction.description),
                    category = FormField(TransactionCategory.valueOf(transaction.category).label),
                    date = FormField(transaction.date.toBrazilianDateFormat())
                )
            )
        }
    }

    private suspend fun isTransactionValid(): Boolean {
        uiState = uiState.copy(
            generalErrorMessage = ""
        )

        val existingTransaction = transactionDao.findDuplicateByUser(
            type = TransactionType.fromDescription(uiState.formState.type.value).name,
            value = uiState.formState.value.value.toDouble(),
            description = uiState.formState.description.value,
            date = uiState.formState.date.value.toMillisFromBrazilianDateFormat()!!,
            userId = dataStore.userLoggedFlow.first()!!.id
        )

        if (existingTransaction != null && existingTransaction._id != transactionId) {
            uiState = uiState.copy(
                generalErrorMessage = "Já existe um lançamento com os mesmos dados."
            )
        }

        return existingTransaction == null
    }

    fun saveTransaction() {
        if (!isValidForm()) {
            return
        }

        viewModelScope.launch {
            if (!isTransactionValid()) return@launch

            uiState = uiState.copy(
                isSaving = true
            )

            val newTransaction = TransactionModel(
                _id = transactionId.let { if (it == 0) null else it },
                type = TransactionType.fromDescription(uiState.formState.type.value).name,
                value = uiState.formState.value.value.toDouble(),
                description = uiState.formState.description.value,
                category = TransactionCategory.fromDescription(uiState.formState.category.value).name,
                date = uiState.formState.date.value.toMillisFromBrazilianDateFormat()!!,
                userId = dataStore.userLoggedFlow.first()!!.id
            )

            transactionDao.saveTransaction(newTransaction)

            uiState = uiState.copy(
                isSaving = false,
                transactionSaved = true
            )
        }
    }


    fun onTypeChanged(type: String) {
        if (uiState.formState.type.value != type) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    type = uiState.formState.type.copy(
                        value = type,
                        errorMessageCode = validateFieldRequired(type)
                    )
                )
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

    fun onCategoryChanged(category: String) {
        if (uiState.formState.category.value != category) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    category = uiState.formState.category.copy(
                        value = category,
                        errorMessageCode = validateFieldRequired(category)
                    )
                )
            )
        }
    }

    fun onDateChanged(date: String) {
        if (uiState.formState.date.value != date) {
            uiState = uiState.copy(
                formState = uiState.formState.copy(
                    date = uiState.formState.date.copy(
                        value = date,
                        errorMessageCode = validateFieldRequired(date)
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

    fun onClearDate() {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                date = uiState.formState.date.copy(value = "")
            )
        )
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                type = uiState.formState.type.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.type.value)
                ),
                value = uiState.formState.value.copy(
                    errorMessageCode = runValidations(
                        uiState.formState.value.value,
                        ::validateFieldRequired,
                        ::validateMonetaryValue,
                        ::validateValueGreaterThanZero
                    )
                ),
                description = uiState.formState.description.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.description.value)
                ),
                category = uiState.formState.category.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.category.value)
                ),
                date = uiState.formState.date.copy(
                    errorMessageCode = validateFieldRequired(uiState.formState.date.value)
                )
            )
        )

        return uiState.formState.isValid
    }

    fun validateMonetaryValue(value: String): Int? {
        if (value.isBlank()) {
            return null
        }

        return try {
            value.replace(',', '.').toDouble()
            null
        } catch (e: NumberFormatException) {
            R.string.error_invalid_monetary_value
        }
    }

    fun validateValueGreaterThanZero(value: String): Int? {
        if (value.isBlank()) {
            return null
        }

        return if ((value.toDoubleOrNull() ?: 0.0) <= 0.0) {
            R.string.error_invalid_monetary_min_value
        } else {
            null
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                val savedStateHandle = this.createSavedStateHandle()

                TransactionFormViewModel(
                    transactionDao = TransactionDao(ctx = application!!),
                    dataStore = DataStore(context = application),
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
