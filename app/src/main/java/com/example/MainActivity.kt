package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CreditosScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ServiciosScreen
import com.example.ui.screens.VoucherScreen
import com.example.ui.screens.YapeoScreen
import com.example.ui.theme.YapeMirrorTheme
import com.example.ui.theme.YapePurpleDark
import com.example.viewmodel.YapeViewModel

object YapeRoutes {
  const val LOGIN = "login"
  const val HOME = "home"
  const val YAPEO = "yapeo"
  const val VOUCHER = "voucher"
  const val SERVICIOS = "servicios"
  const val CREDITOS = "creditos"
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      YapeMirrorTheme {
        YapeMirrorApp()
      }
    }
  }
}

@Composable
fun YapeMirrorApp(
  viewModel: YapeViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = rememberNavController()

  LaunchedEffect(uiState.isAuthenticated) {
    if (uiState.isAuthenticated) {
      navController.navigate(YapeRoutes.HOME) {
        popUpTo(YapeRoutes.LOGIN) { inclusive = true }
      }
    }
  }

  // Adaptive container: full-screen on phones, sleek centered mobile viewport on tablets
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF1F0A26)),
    contentAlignment = Alignment.Center,
  ) {
    val isExpanded = maxWidth > 540.dp
    Surface(
      color = YapePurpleDark,
      shadowElevation = if (isExpanded) 18.dp else 0.dp,
      modifier = if (isExpanded) {
        Modifier
          .widthIn(max = 420.dp)
          .fillMaxSize()
      } else {
        Modifier.fillMaxSize()
      },
    ) {
      NavHost(
        navController = navController,
        startDestination = YapeRoutes.LOGIN,
      ) {
        composable(YapeRoutes.LOGIN) {
          LoginScreen(
            uiState = uiState,
            onDigitPress = viewModel::onPinDigitPressed,
            onBackspace = viewModel::onPinBackspace,
            onClearPin = viewModel::onPinClear,
            onUnlockWithReniec = viewModel::unlockWithReniecBiometric,
          )
        }

        composable(YapeRoutes.HOME) {
          HomeScreen(
            uiState = uiState,
            onRoleSelected = viewModel::setUserRole,
            onToggleBalance = viewModel::toggleBalanceVisibility,
            onNavigateToYapeo = {
              viewModel.clearQrPrefill()
              navController.navigate(YapeRoutes.YAPEO)
            },
            onNavigateToServicios = {
              navController.navigate(YapeRoutes.SERVICIOS)
            },
            onNavigateToCreditos = {
              navController.navigate(YapeRoutes.CREDITOS)
            },
            onQrScanned = { phone, name, destination, amount ->
              viewModel.setQrPrefill(phone, name, destination, amount)
              navController.navigate(YapeRoutes.YAPEO)
            },
            onGenerateApprovalCode = viewModel::generateApprovalCode,
            onNotificationsOpened = viewModel::markNotificationsRead,
            onLogout = {
              viewModel.logout()
              navController.navigate(YapeRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
              }
            },
          )
        }

        composable(YapeRoutes.YAPEO) {
          YapeoScreen(
            uiState = uiState,
            resolveTitularName = viewModel::resolveTitularNameForPhone,
            onAttemptYapeo = viewModel::attemptYapeo,
            onDismissValidationAlert = viewModel::dismissValidationAlert,
            onExpandLimitBiometric = viewModel::expandDailyLimitWithBiometric,
            onNavigateToVoucher = {
              navController.navigate(YapeRoutes.VOUCHER) {
                popUpTo(YapeRoutes.YAPEO) { inclusive = true }
              }
            },
            onBack = { navController.popBackStack() },
          )
        }

        composable(YapeRoutes.VOUCHER) {
          VoucherScreen(
            voucher = uiState.activeVoucher,
            onGoHome = {
              navController.navigate(YapeRoutes.HOME) {
                popUpTo(YapeRoutes.HOME) { inclusive = true }
              }
            },
          )
        }

        composable(YapeRoutes.SERVICIOS) {
          ServiciosScreen(
            uiState = uiState,
            onPayService = viewModel::payUtilityService,
            onDismissValidationAlert = viewModel::dismissValidationAlert,
            onNavigateToVoucher = {
              navController.navigate(YapeRoutes.VOUCHER) {
                popUpTo(YapeRoutes.SERVICIOS) { inclusive = true }
              }
            },
            onBack = { navController.popBackStack() },
          )
        }

        composable(YapeRoutes.CREDITOS) {
          CreditosScreen(
            currentBalance = uiState.balance,
            onDisburseCredit = viewModel::disburseCredit,
            onNavigateToVoucher = {
              navController.navigate(YapeRoutes.VOUCHER) {
                popUpTo(YapeRoutes.CREDITOS) { inclusive = true }
              }
            },
            onBack = { navController.popBackStack() },
          )
        }
      }
    }
  }
}
