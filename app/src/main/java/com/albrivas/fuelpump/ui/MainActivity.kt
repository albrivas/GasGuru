package com.albrivas.fuelpump.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.albrivas.fuelpump.core.uikit.theme.MyApplicationTheme
import com.albrivas.fuelpump.feature.onboarding_welcome.navigation.OnboardingRoutes
import com.albrivas.fuelpump.feature.splash.state.SplashUiState
import com.albrivas.fuelpump.navigation.navigationbar.route.NavigationBarRoute
import com.albrivas.fuelpump.navigation.root.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: SplashUiState by mutableStateOf(SplashUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { result ->
                        uiState = when {
                            result.isSuccess -> result.getOrNull() ?: SplashUiState.Loading
                            result.isFailure -> SplashUiState.Error
                            else -> SplashUiState.Loading
                        }
                    }
                    .collect()
            }
        }

        splash.setKeepOnScreenCondition {
            when (uiState) {
                SplashUiState.Loading -> true
                SplashUiState.Success -> false
                SplashUiState.Error -> false
            }
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) {
                when (uiState) {
                    SplashUiState.Success -> MainNavigation(startDestination = NavigationBarRoute)
                    SplashUiState.Error -> MainNavigation(startDestination = OnboardingRoutes.OnboardingWelcomeRoute)
                    else -> Unit
                }
            }
        }
    }
}
