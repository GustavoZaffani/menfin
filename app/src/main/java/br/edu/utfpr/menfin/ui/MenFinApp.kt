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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.utfpr.menfin.ui.splash.SplashScreen
import br.edu.utfpr.menfin.ui.user.register.RegisterScreen
import kotlinx.coroutines.CoroutineScope

object Screens {
    const val SPLASH = "splash"
    const val REGISTER = "register"
}

object Routes {
    const val SPLASH = Screens.SPLASH
    const val REGISTER = Screens.REGISTER
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
            SplashScreen (onFinishSplash = { userLogged ->
                if (userLogged) navigateTo(navController, Routes.REGISTER)
                else navigateTo(navController, Routes.REGISTER)
            })
        }
        composable(route = Routes.REGISTER) {
            RegisterScreen(
               onRegisterSaved = { navigateTo(navController, Routes.SPLASH) },
            )
        }
    }
}

private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
    }
}