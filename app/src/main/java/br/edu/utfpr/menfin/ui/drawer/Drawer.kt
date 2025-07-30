package br.edu.utfpr.menfin.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AddToHomeScreen
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.Routes
import br.edu.utfpr.menfin.ui.theme.MenfinTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    currentRoute: String,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: DrawerViewModel = viewModel(factory = DrawerViewModel.Factory),
    onLogoutSuccess: () -> Unit,
    onTransactionPressed: () -> Unit,
    onGoalPressed: () -> Unit,
    onHomePressed: () -> Unit,
    onMentorPressed: () -> Unit,
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel.uiState.logoutSuccess) {
        if (viewModel.uiState.logoutSuccess) {
            onLogoutSuccess()
        }
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val drawerWidth = maxWidth * 0.7f
                DrawerSheet(
                    modifier = Modifier.width(drawerWidth),
                    userLogged = viewModel.uiState.userLogged,
                    currentRoute = currentRoute,
                    onTransactionPressed = onTransactionPressed,
                    onMentorPressed = onMentorPressed,
                    onGoalPressed = onGoalPressed,
                    onHomePressed = onHomePressed,
                    closeDrawer = { coroutineScope.launch { drawerState.close() } },
                    onLogout = viewModel::logout
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun DrawerSheet(
    modifier: Modifier = Modifier,
    currentRoute: String,
    userLogged: String,
    onTransactionPressed: () -> Unit,
    onMentorPressed: () -> Unit,
    onGoalPressed: () -> Unit,
    onHomePressed: () -> Unit,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = modifier.fillMaxSize()) {
            HeaderDrawer(
                user = userLogged,
                onLogout = {
                    closeDrawer()
                    onLogout()
                }
            )

            DrawerItem(
                imageVector = Icons.Default.Home,
                label = stringResource(R.string.drawer_menu_home),
                isSelected = currentRoute == Routes.HOME,
                onClick = {
                    closeDrawer()
                    onHomePressed()
                }
            )

            DrawerItemGroupTitle(title = stringResource(R.string.drawer_header_menu_register))

            DrawerItem(
                imageVector = Icons.AutoMirrored.Outlined.Segment,
                label = stringResource(R.string.drawer_menu_transaction),
                isSelected = currentRoute == Routes.TRANSACTION_LIST,
                onClick = {
                    closeDrawer()
                    onTransactionPressed()
                }
            )

            DrawerItemGroupTitle(title = stringResource(R.string.drawer_header_menu_mentor))

            DrawerItem(
                imageVector = Icons.Default.AutoAwesome,
                label = stringResource(R.string.app_name),
                isSelected = currentRoute == Routes.MENTOR,
                onClick = {
                    closeDrawer()
                    onMentorPressed()
                }
            )

            DrawerItemGroupTitle(title = stringResource(R.string.drawer_header_menu_goals))

            DrawerItem(
                imageVector = Icons.Default.Flag,
                label = stringResource(R.string.drawer_menu_goals),
                isSelected = currentRoute == Routes.GOAL_LIST,
                onClick = {
                    closeDrawer()
                    onGoalPressed()
                }
            )
        }
    }
}

@Composable
private fun DrawerItemGroupTitle(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun DrawerItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = label
            )
        },
        label = { Text(text = label) },
        selected = isSelected,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
private fun DrawerPreview() {
    MenfinTheme {
        DrawerSheet(
            currentRoute = Routes.SPLASH,
            userLogged = "CR7",
            closeDrawer = {},
            onTransactionPressed = {},
            onMentorPressed = {},
            onGoalPressed = {},
            onHomePressed = {},
            onLogout = {}
        )
    }
}

@Composable
private fun HeaderDrawer(
    modifier: Modifier = Modifier,
    user: String,
    onLogout: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = stringResource(R.string.generic_to_leave),
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 16.dp)
                )
            }

            Column {
                Text(
                    text = "Bem vindo, $user!",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HeaderDrawerPreview() {
    MenfinTheme {
        HeaderDrawer(
            user = "MenFin",
            onLogout = {}
        )
    }
}