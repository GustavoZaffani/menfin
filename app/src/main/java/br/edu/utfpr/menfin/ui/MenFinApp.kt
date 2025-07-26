package br.edu.utfpr.menfin.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.utfpr.menfin.ui.drawer.Drawer
import br.edu.utfpr.menfin.ui.home.HomeScreen
import br.edu.utfpr.menfin.ui.mentor.hub.MentorHubScreen
import br.edu.utfpr.menfin.ui.onboarding.OnboardingScreen
import br.edu.utfpr.menfin.ui.splash.SplashScreen
import br.edu.utfpr.menfin.ui.transaction.form.TransactionFormScreen
import br.edu.utfpr.menfin.ui.transaction.list.TransactionListScreen
import br.edu.utfpr.menfin.ui.user.login.LoginScreen
import br.edu.utfpr.menfin.ui.user.register.RegisterScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Screens {
    const val SPLASH = "splash"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val TRANSACTION = "transactions"
    const val TRANSACTION_FORM = "transactionsForm"
    const val MENTOR = "mentor"
}

object Arguments {
    const val TRANSACTION_ID = "transactionId"
}

object Routes {
    const val SPLASH = Screens.SPLASH
    const val REGISTER = Screens.REGISTER
    const val LOGIN = Screens.LOGIN
    const val HOME = Screens.HOME
    const val ONBOARDING = Screens.ONBOARDING
    const val TRANSACTION_LIST = Screens.TRANSACTION
    const val TRANSACTION_FORM =
        "${Screens.TRANSACTION_FORM}?${Arguments.TRANSACTION_ID}={${Arguments.TRANSACTION_ID}}"
    const val MENTOR = Screens.MENTOR
}

@Composable
fun MenFinApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startRoute: String = Routes.SPLASH
) {

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startRoute

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ) {
        composable(route = Routes.SPLASH) {
            SplashScreen(onFinishSplash = { userLogged ->
                if (userLogged) navigateTo(navController, Routes.REGISTER)
                else navigateTo(navController, Routes.LOGIN)
            })
        }
        composable(route = Routes.REGISTER) {
            RegisterScreen(
                onRegisterSaved = { navigateTo(navController, Routes.LOGIN) },
            )
        }

        composable(route = Routes.LOGIN) {
            LoginScreen(
                onClickNewRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = { onboardingIsDone ->
                    val destination = if (onboardingIsDone) Routes.HOME else Routes.ONBOARDING
                    navigateTo(navController, destination)
                }
            )
        }

        composable(route = Routes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingFinished = { navigateTo(navController, Routes.HOME) },
            )
        }

        composable(route = Routes.HOME) {
            DefaultDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navController = navController
            ) {
                HomeScreen(
                    openDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }

        composable(route = Routes.TRANSACTION_LIST) {
            DefaultDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navController = navController
            ) {
                TransactionListScreen(
                    onNavigateToForm = { navController.navigate(Screens.TRANSACTION_FORM) },
                    openDrawer = { coroutineScope.launch { drawerState.open() } },
                    onTransactionPressed = { transaction ->
                        navController.navigate("${Screens.TRANSACTION_FORM}?${Arguments.TRANSACTION_ID}=${transaction._id}")
                    }
                )
            }
        }

        composable(
            route = Routes.TRANSACTION_FORM,
            arguments = listOf(
                navArgument(name = "id") { type = NavType.StringType; nullable = true }
            )
        ) {
            TransactionFormScreen(
                onTransactionSaved = { navigateTo(navController, Routes.TRANSACTION_LIST) },
                onBackPressed = { navController.popBackStack() },
            )
        }

        composable(route = Routes.MENTOR) {
            DefaultDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navController = navController
            ) {
               MentorHubScreen(
                   openDrawer = { coroutineScope.launch { drawerState.open() } }
               ) { }
            }
        }
    }
}

@Composable
fun DefaultDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Drawer(
        drawerState = drawerState,
        currentRoute = currentRoute,
        onLogoutSuccess = { navigateTo(navController, Routes.LOGIN) },
        onTransactionPressed = { navigateTo(navController, Routes.TRANSACTION_LIST) },
        onMentorPressed = { navigateTo(navController, Routes.MENTOR) },
    ) {
        content()
    }
}

private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
    }
}