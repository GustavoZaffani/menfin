package br.edu.utfpr.menfin.ui.transaction.list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.model.TransactionCategory
import br.edu.utfpr.menfin.data.model.TransactionModel
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.extensions.toBrazilianDateFormat
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.CardList
import br.edu.utfpr.menfin.ui.shared.components.ConfirmationDialog
import br.edu.utfpr.menfin.ui.shared.components.EmptyList
import br.edu.utfpr.menfin.ui.shared.components.Loading
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = viewModel(factory = TransactionListViewModel.Factory),
    onNavigateToForm: () -> Unit,
    openDrawer: () -> Unit,
    onTransactionPressed: (TransactionModel) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState.transactionDeleted) {
        if (viewModel.uiState.transactionDeleted) {
            Toast.makeText(
                context,
                context.getString(R.string.transaction_list_transaction_excluded_with_success),
                Toast.LENGTH_LONG
            ).show()

            viewModel.loadTransactions()
        }
    }

    if (viewModel.uiState.showConfirmationDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.transaction_list_delete_title_message),
            text = stringResource(R.string.transaction_list_delete_description_message),
            onDismiss = viewModel::dismissConfirmationDialog,
            onConfirm = viewModel::deleteTransaction
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TransactionAppBar(
                onRefreshPressed = { viewModel.loadTransactions() },
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = AppPrimaryColor
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.transaction_list_add_transaction_button),
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        if (viewModel.uiState.hasAnyLoading) {
            Loading(
                modifier = Modifier.padding(innerPadding),
                text = viewModel.uiState.loadingMessage,
            )
        } else if (viewModel.uiState.transactions.isEmpty()) {
            EmptyList(
                modifier = Modifier.padding(innerPadding),
                description = stringResource(R.string.transaction_list_empty_list_message),
            )
        } else {
            TransactionContent(
                modifier = Modifier.padding(innerPadding),
                transactions = viewModel.uiState.transactions,
                onTransactionPressed = onTransactionPressed,
                onTransactionLongPressed = viewModel::showConfirmationDialog,
            )
        }
    }
}

@Composable
fun TransactionContent(
    modifier: Modifier = Modifier,
    transactions: List<TransactionModel>,
    onTransactionPressed: (TransactionModel) -> Unit,
    onTransactionLongPressed: (TransactionModel) -> Unit
) {
    CardList(
        modifier = modifier,
        items = transactions,
        onItemPressed = onTransactionPressed,
        onLongItemPressed = onTransactionLongPressed,
    ) { transaction ->
        val valueColor =
            if (transaction.type == TransactionType.REVENUE.name) Color(0xFF16A34A) else Color(
                0xFFDC2626
            )
        val formattedValue =
            NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(transaction.value)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row {
                    Text(
                        text = transaction.date.toBrazilianDateFormat(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = " | ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = TransactionCategory.valueOf(transaction.category).label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = formattedValue,
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
private fun TransactionItemPreview() {
    MenfinTheme {
        TransactionContent(
            transactions = listOf(
                TransactionModel(
                    _id = 1,
                    description = "Compra de Material de Escritório",
                    value = 150.00,
                    category = "Escritório",
                    type = TransactionType.EXPENSE.name,
                    date = 1234545678L,
                    userId = 1
                ),
                TransactionModel(
                    _id = 2,
                    description = "Venda de Produto",
                    value = 300.00,
                    category = "Vendas",
                    type = TransactionType.REVENUE.name,
                    date = 1234545678L,
                    userId = 1
                )
            ),
            onTransactionPressed = {},
            onTransactionLongPressed = {}
        )
    }
}

@Composable
private fun TransactionAppBar(
    modifier: Modifier = Modifier,
    onRefreshPressed: () -> Unit,
    openDrawer: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.transaction_list_app_bar_title),
        showActions = true,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.generic_open_menu)
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshPressed) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.generic_to_update)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview() {
    MenfinTheme {
        TransactionListScreen(
            onNavigateToForm = {},
            onTransactionPressed = {},
            openDrawer = {}
        )
    }
}
