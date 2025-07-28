package br.edu.utfpr.menfin.ui.transaction.list

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import br.edu.utfpr.menfin.ui.shared.components.ConfirmationDialog
import br.edu.utfpr.menfin.ui.shared.components.EmptyList
import br.edu.utfpr.menfin.ui.shared.components.Loading
import br.edu.utfpr.menfin.ui.shared.components.TransactionList
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = viewModel(factory = TransactionListViewModel.Factory),
    onNavigateToForm: () -> Unit,
    openDrawer: () -> Unit,
    onTransactionPressed: (TransactionModel, TransactionClickAction) -> Unit
) {
    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }

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
                onTransactionPressed = {
                    viewModel.onTransactionPressed(it)
                    showModal = true
                },
                onTransactionLongPressed = viewModel::showConfirmationDialog,
            )
        }
    }

    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ActionBottomSheetContent(
                onEditClick = {
                    showModal = false
                    onTransactionPressed(
                        viewModel.uiState.transactionPressed!!,
                        TransactionClickAction.EDIT
                    )
                },
                onDuplicateClick = {
                    showModal = false
                    onTransactionPressed(
                        viewModel.uiState.transactionPressed!!,
                        TransactionClickAction.DUPLICATE
                    )
                }
            )
        }
    }
}

@Composable
private fun TransactionContent(
    modifier: Modifier = Modifier,
    transactions: List<TransactionModel>,
    onTransactionPressed: (TransactionModel) -> Unit,
    onTransactionLongPressed: (TransactionModel) -> Unit
) {
    TransactionList(
        modifier = modifier,
        items = transactions,
        onItemPressed = onTransactionPressed,
        onLongItemPressed = onTransactionLongPressed,
    ) { transaction ->
        TransactionListItem(transaction = transaction)
    }
}

@Composable
private fun TransactionListItem(transaction: TransactionModel) {

    val typeColor =
        if (transaction.type == TransactionType.REVENUE.name) Color(0xFF16A34A) else Color(
            0xFFDC2626
        )
    val formattedValue =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(transaction.value)
    val category = TransactionCategory.valueOf(transaction.category)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(typeColor)
            )

            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(typeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.getIcon(),
                    contentDescription = category.label,
                    tint = typeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.date.toBrazilianDateFormat()} | ${category.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = formattedValue,
                color = typeColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

fun TransactionCategory.getIcon(): ImageVector {
    return when (this) {
        TransactionCategory.FOOD -> Icons.Default.Fastfood
        TransactionCategory.TRANSPORT -> Icons.Default.DirectionsCar
        TransactionCategory.HOUSING -> Icons.Default.Home
        TransactionCategory.LEISURE -> Icons.Default.LocalActivity
        TransactionCategory.HEALTH -> Icons.Default.MedicalServices
        TransactionCategory.SALARY -> Icons.Default.AttachMoney
        TransactionCategory.OTHER -> Icons.Default.MoreHoriz
    }
}

@Composable
private fun ActionBottomSheetContent(
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        ListItem(
            headlineContent = {
                Text(stringResource(R.string.generic_edit))
            },
            leadingContent = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.generic_edit)
                )
            },
            modifier = Modifier
                .clickable(onClick = onEditClick)
        )
        ListItem(
            headlineContent = {
                Text(stringResource(R.string.generic_duplicate))
            },
            leadingContent = {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.generic_duplicate)
                )
            },
            modifier = Modifier
                .clickable(onClick = onDuplicateClick)
        )
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
private fun TransactionListScreenPreview() {
    MenfinTheme {
        TransactionListScreen(
            onNavigateToForm = {},
            onTransactionPressed = { _, _ -> },
            openDrawer = {}
        )
    }
}
