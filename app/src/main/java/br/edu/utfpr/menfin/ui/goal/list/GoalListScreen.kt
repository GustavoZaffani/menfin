package br.edu.utfpr.menfin.ui.goal.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.model.GoalModel
import br.edu.utfpr.menfin.data.model.GoalPriority
import br.edu.utfpr.menfin.extensions.toBrazilianDateFormat
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.CommonList
import br.edu.utfpr.menfin.ui.shared.components.EmptyList
import br.edu.utfpr.menfin.ui.shared.components.Loading
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GoalListScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalListViewModel = viewModel(factory = GoalListViewModel.Factory),
    onNavigateToForm: () -> Unit,
    openDrawer: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GoalAppBar(
                onRefreshPressed = { viewModel.loadGoals() },
                onInsightPressed = viewModel::onGenerateInsights,
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
                    contentDescription = stringResource(R.string.goal_list_add_new_goal),
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        if (viewModel.uiState.hasAnyLoading) {
            Loading(
                modifier = Modifier.padding(innerPadding),
                text = viewModel.uiState.loadingText,
            )
        } else if (viewModel.uiState.goals.isEmpty()) {
            EmptyList(
                modifier = Modifier.padding(innerPadding),
                description = stringResource(R.string.goal_list_empty_list_message),
            )
        } else {
            GoalContent(
                modifier = Modifier.padding(innerPadding),
                goals = viewModel.uiState.goals
            )
        }
    }

    if (viewModel.uiState.showInsights) {
        GeneralInsightsDialog(
            insights = viewModel.uiState.insights,
            onDismiss = viewModel::onCloseInsights
        )
    }
}

@Composable
private fun GoalContent(
    modifier: Modifier = Modifier,
    goals: List<GoalModel>
) {
    CommonList(
        modifier = modifier,
        items = goals
    ) { goal ->
        GoalListItem(goal = goal)
    }
}

@Composable
private fun GoalListItem(goal: GoalModel) {

    val typeColor = when (goal.priority) {
        GoalPriority.HIGH -> Color(0xFFDC2626)
        GoalPriority.MEDIUM -> Color(0xFF4771DE)
        else -> Color(0xFF16A34A)
    }

    val formattedValue =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(goal.value)

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
                    imageVector = goal.priority.getIcon(),
                    contentDescription = goal.priority.label,
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
                    text = goal.description,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data limite: ${goal.targetDate.toBrazilianDateFormat()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = formattedValue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

fun GoalPriority.getIcon(): ImageVector {
    return when (this) {
        GoalPriority.HIGH -> Icons.Default.ArrowCircleUp
        GoalPriority.MEDIUM -> Icons.Default.Circle
        GoalPriority.LOW -> Icons.Default.ArrowCircleDown
    }
}

@Preview
@Composable
private fun GoalItemPreview() {
    MenfinTheme {
        GoalContent(
            goals = listOf(
                GoalModel(
                    _id = 1,
                    description = "Comprar um carro",
                    value = 50000.0,
                    priority = GoalPriority.HIGH,
                    targetDate = System.currentTimeMillis() + 1000000000L,
                    userId = 1,
                    createdAt = System.currentTimeMillis()
                ),
                GoalModel(
                    _id = 2,
                    description = "Viajar para a Europa",
                    value = 20000.0,
                    priority = GoalPriority.MEDIUM,
                    targetDate = System.currentTimeMillis() + 2000000000L,
                    userId = 1,
                    createdAt = System.currentTimeMillis()
                )
            )
        )
    }
}

@Composable
private fun GoalAppBar(
    modifier: Modifier = Modifier,
    onRefreshPressed: () -> Unit,
    onInsightPressed: () -> Unit,
    openDrawer: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.goal_list_app_bar_title),
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
            IconButton(onClick = onInsightPressed) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.goal_list_show_insights),
                )
            }
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
private fun GoalListScreenPreview() {
    MenfinTheme {
        GoalListScreen(
            onNavigateToForm = {},
            openDrawer = {}
        )
    }
}


@Composable
private fun GeneralInsightsDialog(
    insights: List<InsightItem>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.heightIn(max = 500.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = stringResource(R.string.goal_list_insights),
                        tint = AppPrimaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.goal_list_menfin_insights),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(insights) { insight ->
                        InsightRow(insight = insight)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.goal_list_sure_and_close))
                }
            }
        }
    }
}

@Composable
private fun InsightRow(insight: InsightItem) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = insight.type.icon,
            contentDescription = stringResource(R.string.goal_list_insight_type),
            tint = insight.type.color,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = insight.text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GeneralInsightsDialogPreview() {
    MenfinTheme {
        val mockInsights = listOf(
            InsightItem(
                text = "Sua meta 'Viagem para a Europa' precisa de atenção, pois o prazo está curto e o ritmo de economia atual não é suficiente.",
                type = InsightType.ATTENTION
            )
        )
        GeneralInsightsDialog(
            insights = mockInsights,
            onDismiss = {}
        )
    }
}