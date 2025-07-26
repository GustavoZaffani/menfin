package br.edu.utfpr.menfin.ui.mentor.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.data.model.ChatHistoryModel
import br.edu.utfpr.menfin.data.model.Sender
import br.edu.utfpr.menfin.extensions.formatTimestamp
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.shared.utils.buildAnnotatedStringWithMarkdown
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import kotlinx.coroutines.launch

@Composable
fun MentorChatScreen(
    modifier: Modifier = Modifier,
    viewModel: MentorChatViewModel = viewModel(factory = MentorChatViewModel.Factory),
    onBackPressed: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.uiState.messages.size) {
        if (viewModel.uiState.messages.isNotEmpty()) {
            keyboardController?.hide()
            coroutineScope.launch {
                listState.animateScrollToItem(viewModel.uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MentorChatAppBar(onBackPressed = onBackPressed)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(viewModel.uiState.messages) { message ->
                    MessageRow(message = message)
                }
            }

            ChatInput(
                value = viewModel.uiState.question,
                onValueChange = viewModel::onChangeQuestion,
                onSendClick = {
                    viewModel.sendQuestion()
                }
            )
        }
    }
}

@Composable
private fun MessageRow(
    message: ChatHistoryModel
) {
    val isFromUser = message.sender == Sender.USER
    val arrangement = if (isFromUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Top
    ) {
        if (!isFromUser) {
            ChatAvatar(sender = Sender.MENTOR)
            Spacer(modifier = Modifier.width(8.dp))
        }

        MessageBubble(
            message = message
        )

        if (isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            ChatAvatar(sender = Sender.USER)
        }
    }
}

@Composable
private fun ChatAvatar(sender: Sender) {
    val icon = if (sender == Sender.MENTOR) Icons.Default.AutoAwesome else Icons.Default.Person
    val backgroundColor = if (sender == Sender.MENTOR) Color(0xFFE0E0E0) else AppPrimaryColor
    val iconColor = if (sender == Sender.MENTOR) AppPrimaryColor else Color.White

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.mentor_chat_avatar),
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Composable
private fun MessageBubble(message: ChatHistoryModel) {
    val isFromUser = message.sender == Sender.USER
    val isSystemLoader = message.sender == Sender.SYSTEM_LOADER
    val backgroundColor = if (isFromUser) AppPrimaryColor else Color(0xFFE0E0E0)
    val textColor = if (isFromUser) Color.White else Color.Black
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isFromUser) 16.dp else 0.dp,
        bottomEnd = if (isFromUser) 0.dp else 16.dp
    )

    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(bubbleShape)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (isSystemLoader) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .padding(8.dp),
                color = if (isFromUser) Color.White else AppPrimaryColor,
                strokeWidth = 2.dp
            )
        } else {
            Column {
                Text(
                    text = if (isFromUser) stringResource(R.string.generic_you) else stringResource(
                        R.string.app_name
                    ),
                    color = if (isFromUser) textColor.copy(alpha = 0.8f) else AppPrimaryColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildAnnotatedStringWithMarkdown(message.text),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.timestamp.formatTimestamp(),
                    color = textColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.mentor_chat_typing_your_question)
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSendClick() }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(AppPrimaryColor, CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                    disabledContentColor = Color.Gray
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.mentor_chat_send_message)
                )
            }
        }
    }
}

@Composable
private fun MentorChatAppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    AppBar(
        modifier = modifier,
        title = stringResource(R.string.mentor_chat_app_bar_title),
        showActions = false,
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.generic_to_back)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MentorChatScreenPreview() {
    MenfinTheme {
        MentorChatScreen(onBackPressed = {})
    }
}
