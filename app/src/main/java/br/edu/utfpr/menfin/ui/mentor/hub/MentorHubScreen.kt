package br.edu.utfpr.menfin.ui.mentor.hub

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.components.RatingBar
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
    var showFeedbackDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState.feedbackSaved) {
        if (viewModel.uiState.feedbackSaved) {
            Toast.makeText(
                context,
                R.string.mentor_hub_thanks_for_feedback,
                Toast.LENGTH_LONG
            ).show()
            showFeedbackDialog = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MentorAppBar(
                openDrawer = openDrawer,
                onFeedbackPressed = { showFeedbackDialog = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChat,
                containerColor = AppPrimaryColor,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = stringResource(R.string.mentor_hub_open_chat),
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

    if (showFeedbackDialog) {
        FeedbackDialog(
            feedbackState = viewModel.uiState.feedbackState,
            onCommentChanged = viewModel::onCommentChanged,
            onRatingChanged = viewModel::onRatingChanged,
            onDismiss = {
                viewModel.onCancelFeedback()
                showFeedbackDialog = false
            },
            onSubmit = viewModel::onSubmitFeedback
        )
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
                stringResource(R.string.mentor_hub_suggestion_questions),
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
            contentDescription = stringResource(R.string.mentor_hub_ai_icon),
            tint = AppPrimaryColor,
            modifier = Modifier.size(48.dp)
        )
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.mentor_hub_apresentation),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.mentor_hub_choose_question_description),
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
                contentDescription = stringResource(R.string.mentor_hub_ai_icon),
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
    onFeedbackPressed: () -> Unit,
    openDrawer: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.app_name),
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
            IconButton(onClick = onFeedbackPressed) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.mentor_hub_give_feedback)
                )
            }
        }
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

@Composable
fun FeedbackDialog(
    modifier: Modifier = Modifier,
    feedbackState: FeedbackState,
    onDismiss: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.mentor_hub_rate_the_mentor),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.mentor_hub_rate_the_mentor_description),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                RatingBar(
                    rating = feedbackState.rating.value.toInt(),
                    onRatingChanged = onRatingChanged
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = feedbackState.comment.value,
                    onValueChange = onCommentChanged,
                    label = {
                        Text(text = stringResource(R.string.mentor_hub_feedback_comment_input))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.generic_to_cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSubmit,
                        enabled = feedbackState.feedbackCompleted
                    ) {
                        Text(text = stringResource(R.string.generic_send))
                    }
                }
            }
        }
    }
}