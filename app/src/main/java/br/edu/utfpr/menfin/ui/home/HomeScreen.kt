package br.edu.utfpr.menfin.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.Loading
import br.edu.utfpr.menfin.ui.shared.components.MonthNavigator
import br.edu.utfpr.menfin.ui.shared.utils.buildAnnotatedStringWithMarkdown
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeAppBar(openDrawer = openDrawer)
        },
    ) { innerPadding ->
        if (viewModel.uiState.loading) {
            Loading(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.home_loading_information)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MonthNavigator(
                        currentMonth = viewModel.uiState.currentMonth,
                        onPreviousMonth = viewModel::onPreviousMonth,
                        onNextMonth = viewModel::onNextMonth
                    )
                }
                item {
                    BalanceCard(
                        revenue = viewModel.uiState.revenue,
                        expenses = viewModel.uiState.expenses,
                        balance = viewModel.uiState.balance
                    )
                }

                item {
                    InsightsPanel(
                        insights = viewModel.uiState.insights
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceCard(revenue: Double, expenses: Double, balance: Double) {
    val balanceColor = if (balance >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.home_balance_month),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currencyFormat.format(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = balanceColor
            )
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.home_revenue),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = currencyFormat.format(revenue),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF16A34A)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.home_expenses),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        currencyFormat.format(expenses),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsPanel(
    modifier: Modifier = Modifier,
    insights: List<String>
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppPrimaryColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Insights,
                    contentDescription = stringResource(R.string.home_insights),
                    tint = AppPrimaryColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.home_menfin_insights),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            insights.forEach { insight ->
                Text(
                    buildAnnotatedStringWithMarkdown(insight),
                    style = MaterialTheme.typography.bodyMedium
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }
    }
}

@Composable
private fun HomeAppBar(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.app_name),
        showActions = false,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.generic_open_menu)
                )
            }
        },
        actions = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MenfinTheme {
        HomeScreen(
            openDrawer = {}
        )
    }
}
