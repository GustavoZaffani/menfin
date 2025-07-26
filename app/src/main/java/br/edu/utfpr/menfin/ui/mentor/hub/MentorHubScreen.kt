package br.edu.utfpr.menfin.ui.mentor.hub

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.utils.buildAnnotatedStringWithMarkdown
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorHubScreen(
    modifier: Modifier = Modifier,
    viewModel: MentorHubViewModel = viewModel(factory = MentorHubViewModel.Factory),
    openDrawer: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MentorAppBar(openDrawer = openDrawer)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChat,
                containerColor = AppPrimaryColor,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Abrir Chat",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        MentorHubContent(
            modifier = Modifier.padding(innerPadding),
            quickQuestions = QuickQuestion.entries,
            onQuestionClicked = {
                viewModel.onQuestionSelected(it)
                showModal = true
            }
        )
    }

    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            AnswerBottomSheetContent(
                question = viewModel.uiState.selectedQuestion!!,
                answer = viewModel.uiState.answer,
                errorMessage = viewModel.uiState.errorMessage,
                isLoading = viewModel.uiState.loadingAnswer
            )
        }
    }
}

@Composable
fun MentorHubContent(
    modifier: Modifier = Modifier,
    quickQuestions: List<QuickQuestion>,
    onQuestionClicked: (QuickQuestion) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WelcomeHeader()

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                "Sugestões de perguntas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )
            quickQuestions.forEach { question ->
                QuestionCard(
                    question = question.description,
                    onClick = { onQuestionClicked(question) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun WelcomeHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Ícone do Mentor",
            tint = AppPrimaryColor,
            modifier = Modifier.size(48.dp)
        )
        Text(
            textAlign = TextAlign.Center,
            text = "Olá! Sou MenFin, seu mentor financeiro",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Comece com uma pergunta rápida ou abra nosso chat para uma conversa detalhada.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}


@Composable
fun QuestionCard(question: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Ícone de IA",
                tint = AppPrimaryColor.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = question,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AnswerBottomSheetContent(
    question: QuickQuestion,
    answer: String = "",
    errorMessage: String = "",
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 250.dp)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = AppPrimaryColor)
        } else {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = question.description,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = buildAnnotatedStringWithMarkdown(answer),
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MentorAppBar(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = "MenFin",
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
    )
}

@Preview(showBackground = true)
@Composable
fun MentorHubScreenPreview() {
    MenfinTheme {
        MentorHubScreen(
            openDrawer = {},
            onNavigateToChat = {},
        )
    }
}
