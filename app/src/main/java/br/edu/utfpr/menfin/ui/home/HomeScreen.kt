package br.edu.utfpr.menfin.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.shared.components.AnimationTypingText
import br.edu.utfpr.menfin.ui.shared.components.AppBar
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeAppBar(
                openDrawer = openDrawer
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(128.dp)
            )
            AnimationTypingText(text = stringResource(R.string.app_name), typingSpeed = 200L)
        }
    }
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
private fun HomeAppBarPreview() {
    MenfinTheme {
        HomeAppBar(
            openDrawer = {}
        )
    }
}