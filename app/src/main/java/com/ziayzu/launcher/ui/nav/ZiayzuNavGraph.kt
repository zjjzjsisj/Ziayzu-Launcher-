package com.ziayzu.launcher.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ziayzu.launcher.core.Prefs
import com.ziayzu.launcher.core.Session
import com.ziayzu.launcher.ui.screens.HomeScreen
import com.ziayzu.launcher.ui.screens.LoginScreen
import com.ziayzu.launcher.ui.screens.OnboardingScreen
import com.ziayzu.launcher.ui.screens.PlayScreen
import com.ziayzu.launcher.ui.screens.SettingsScreen
import com.ziayzu.launcher.ui.screens.SplashScreen
import com.ziayzu.launcher.ui.screens.VersionsScreen

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val HOME = "home"
    const val VERSIONS = "versions"
    const val SETTINGS = "settings"
    const val PLAY = "play"
}

@Composable
fun ZiayzuNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onNext = {
                nav.navigate(if (Prefs.seenOnboarding) Routes.HOME else Routes.ONBOARDING) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinish = {
                Prefs.seenOnboarding = true
                nav.navigate(if (Session.account != null) Routes.HOME else Routes.LOGIN) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onDone = { nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onSkip = { nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        composable(Routes.HOME) { HomeScreen(nav = { nav.navigate(it) }) }
        composable(Routes.VERSIONS) { VersionsScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.PLAY) { PlayScreen(onBack = { nav.popBackStack() }) }
    }
}
