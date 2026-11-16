package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InteropDestination
import com.example.model.TransactionType
import com.example.model.UserRole
import com.example.model.YapeTransaction
import com.example.model.formatSoles
import com.example.ui.components.QrScannerDialog
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.YapeDivider
import com.example.ui.theme.YapeMagenta
import com.example.ui.theme.YapeMagentaLight
import com.example.ui.theme.YapePurple
import com.example.ui.theme.YapePurpleDark
import com.example.ui.theme.YapePurpleLight
import com.example.ui.theme.YapeTextPrimary
import com.example.ui.theme.YapeTextSecondary
import com.example.ui.theme.YapeTrayBackground
import com.example.ui.theme.YapeTurquoise
import com.example.ui.theme.YapeTurquoiseDark
import com.example.ui.theme.YapeTurquoiseLight
import com.example.ui.theme.YapeWarningAmber
import com.example.ui.theme.YapeWarningAmberLight
import com.example.ui.theme.YapeWhite
import com.example.viewmodel.YapeUiState

private data class HomeQuickAction(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val accentColor: Color,
  val isNew: Boolean = false,
)

@Composable
fun HomeScreen(
  uiState: YapeUiState,
  onRoleSelected: (UserRole) -> Unit,
  onToggleBalance: () -> Unit,
  onNavigateToYapeo: () -> Unit,
  onNavigateToServicios: () -> Unit,
  onNavigateToCreditos: () -> Unit,
  onQrScanned: (phone: String, name: String, destination: InteropDestination, amount: String) -> Unit,
  onGenerateApprovalCode: () -> String,
  onNotificationsOpened: () -> Unit,
  onLogout: () -> Unit,
) {
  val isAyudante = uiState.userRole == UserRole.AYUDANTE
  var showQrDialog by remember { mutableStateOf(false) }
  var selectedTxDetail by remember { mutableStateOf<YapeTransaction?>(null) }
  var activeUtilitySheetTitle by remember { mutableStateOf<String?>(null) }
  var activeUtilitySheetBody by remember { mutableStateOf<String?>(null) }
  var showNotificationsDialog by remember { mutableStateOf(false) }

  val quickActions = remember {
    listOf(
      HomeQuickAction("recargar", "Recargar celular", Icons.Rounded.PhoneAndroid, YapePurple),
      HomeQuickAction("servicios", "Yapear servicios", Icons.Rounded.ReceiptLong, YapeTurquoiseDark),
      HomeQuickAction("creditos", "Créditos", Icons.Rounded.RequestQuote, YapeMagenta, isNew = true),
      HomeQuickAction("dolares", "Cambiar dólares", Icons.Rounded.CurrencyExchange, YapePurple),
      HomeQuickAction("tienda", "Yape Tienda", Icons.Rounded.ShoppingBag, YapeMagenta),
      HomeQuickAction("promos", "Promos", Icons.Rounded.LocalOffer, YapeTurquoiseDark),
      HomeQuickAction("codigo", "Código de aprobación", Icons.Rounded.Key, YapePurple),
      HomeQuickAction("empresa", "Yape Empresa", Icons.Rounded.Storefront, YapePurpleDark),
    )
  }

  val displayedTransactions = remember(uiState.transactions, isAyudante) {
    if (isAyudante) {
      uiState.transactions.filter { it.type == TransactionType.COBRO_RECIBIDO }
    } else {
      uiState.transactions
    }
  }

  Scaffold(
    containerColor = YapePurple,
    bottomBar = {
      // Fixed Bottom Action Bar: "Escanear QR" and "Yapear"
      Surface(
        color = YapeWhite,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          OutlinedButton(
            onClick = { showQrDialog = true },
            enabled = !isAyudante,
            border = BorderStroke(
              width = 2.dp,
              color = if (isAyudante) YapeDivider else YapeTurquoise,
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .testTag("home_scan_qr_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.QrCodeScanner,
              contentDescription = null,
              tint = if (isAyudante) YapeTextSecondary else YapeTurquoiseDark,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Escanear QR",
              color = if (isAyudante) YapeTextSecondary else YapeTurquoiseDark,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
            )
          }

          Button(
            onClick = onNavigateToYapeo,
            enabled = !isAyudante,
            colors = ButtonDefaults.buttonColors(
              containerColor = YapeTurquoise,
              disabledContainerColor = Color(0xFFD5D2DC),
              disabledContentColor = Color(0xFF7D7588),
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(52.dp)
              .testTag("home_yapear_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.Send,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Yapear",
              fontWeight = FontWeight.Bold,
              maxLines = 1,
            )
          }
        }
      }
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = innerPadding.calculateBottomPadding())
        .background(YapeTrayBackground),
      contentPadding = PaddingValues(bottom = 16.dp),
    ) {
      // Purple Header + Role Switcher + 8 Quick Action Icons
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(listOf(YapePurple, YapePurpleDark))
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
          // Greeting & Top Action Icons
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f),
            ) {
              Surface(
                shape = CircleShape,
                color = YapeTurquoise,
                modifier = Modifier.size(42.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Perfil de usuario",
                    tint = YapeWhite,
                  )
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Hola, Jimmy Pereira",
                  color = YapeWhite,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = if (isAyudante) "Yape Empresa • Rol Ayudante" else "Yape con BCP • Perú",
                  color = YapeWhite.copy(alpha = 0.8f),
                  style = MaterialTheme.typography.labelSmall,
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = {
                  onNotificationsOpened()
                  showNotificationsDialog = true
                },
                modifier = Modifier.testTag("home_notifications_button"),
              ) {
                BadgedBox(
                  badge = {
                    if (uiState.unreadNotifications > 0) {
                      Badge(containerColor = YapeMagenta) {
                        Text("${uiState.unreadNotifications}", color = YapeWhite)
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notificaciones",
                    tint = YapeWhite,
                  )
                }
              }
              IconButton(
                onClick = onLogout,
                modifier = Modifier.testTag("home_logout_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.Logout,
                  contentDescription = "Cerrar sesión y volver al PIN",
                  tint = YapeWhite,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Selector Titular vs Ayudante (RBAC CP-010)
          Surface(
            color = YapePurpleDark.copy(alpha = 0.72f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, YapeWhite.copy(alpha = 0.18f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              UserRole.entries.forEach { role ->
                val selected = uiState.userRole == role
                Surface(
                  color = if (selected) YapeTurquoise else Color.Transparent,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRoleSelected(role) }
                    .testTag("role_selector_${role.name.lowercase()}"),
                ) {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 6.dp),
                  ) {
                    Text(
                      text = role.title,
                      color = if (selected) YapeWhite else YapeWhite.copy(alpha = 0.78f),
                      fontSize = 11.sp,
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                      textAlign = TextAlign.Center,
                      maxLines = 2,
                      lineHeight = 13.sp,
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 8 Circular White Icons Grid (4 x 2)
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(quickActions.subList(0, 4), quickActions.subList(4, 8)).forEach { rowItems ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                rowItems.forEach { item ->
                  QuickActionCircle(
                    action = item,
                    enabled = !isAyudante || item.id == "empresa",
                    onClick = {
                      when (item.id) {
                        "servicios" -> onNavigateToServicios()
                        "creditos" -> onNavigateToCreditos()
                        "codigo" -> {
                          val otp = onGenerateApprovalCode()
                          activeUtilitySheetTitle = "Código de aprobación"
                          activeUtilitySheetBody =
                            "Tu código dinámico de seguridad para compras online es:\n\n$otp\n\nVálido por 60 segundos."
                        }
                        "dolares" -> {
                          activeUtilitySheetTitle = "Cambiar dólares"
                          activeUtilitySheetBody =
                            "Tipo de cambio preferencial Yape hoy:\n• Compra: S/ 3.715\n• Venta: S/ 3.745\n\nTu saldo disponible actual es ${formatSoles(uiState.balance)}."
                        }
                        "recargar" -> {
                          activeUtilitySheetTitle = "Recargar celular"
                          activeUtilitySheetBody =
                            "Operadores disponibles: Claro, Movistar, Entel y Bitel desde S/ 3.00 sin comisión usando tu saldo Yape."
                        }
                        "tienda" -> {
                          activeUtilitySheetTitle = "Yape Tienda"
                          activeUtilitySheetBody =
                            "Explora tecnología, electrodomésticos y delivery con hasta 40% de descuento pagando directo con tu saldo."
                        }
                        "promos" -> {
                          activeUtilitySheetTitle = "Promos Yape"
                          activeUtilitySheetBody =
                            "• Combos desde S/ 9.90 en restaurantes aliados\n• 2x1 en cines los martes y jueves\n• Cashback en grifos afiliados"
                        }
                        "empresa" -> {
                          activeUtilitySheetTitle = "Yape Empresa (RBAC)"
                          activeUtilitySheetBody =
                            "Con Yape Empresa puedes asignar ayudantes de tienda para que confirmen cobros en tiempo real sin acceder al saldo ni realizar transferencias (CP-010)."
                        }
                      }
                    },
                    modifier = Modifier.weight(1f),
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
        }
      }

      // Balance Card OR RBAC Ayudante Restriction Banner (CP-010)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(YapeTrayBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
          if (isAyudante) {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = YapeWarningAmberLight),
              border = BorderStroke(1.5.dp, YapeWarningAmber),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rbac_ayudante_banner"),
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  shape = CircleShape,
                  color = YapeWarningAmber.copy(alpha = 0.2f),
                  modifier = Modifier.size(44.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Rounded.Lock,
                      contentDescription = "Modo restringido",
                      tint = Color(0xFFB45309),
                    )
                  }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Control de acceso RBAC (CP-010)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF92400E),
                    fontWeight = FontWeight.Bold,
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Modo Ayudante: Solo tienes permiso para visualizar cobros recibidos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF78350F),
                    fontWeight = FontWeight.SemiBold,
                  )
                }
              }
            }
          } else {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = YapeWhite),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleBalance)
                .testTag("balance_toggle_card"),
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = CircleShape,
                      color = YapePurpleLight,
                      modifier = Modifier.size(36.dp),
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(
                          imageVector = Icons.Rounded.AccountBalanceWallet,
                          contentDescription = null,
                          tint = YapePurple,
                          modifier = Modifier.size(20.dp),
                        )
                      }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = if (uiState.isBalanceVisible) "Ocultar saldo" else "Mostrar saldo",
                      color = YapePurple,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                    )
                  }
                  Icon(
                    imageVector = if (uiState.isBalanceVisible) {
                      Icons.Rounded.VisibilityOff
                    } else {
                      Icons.Rounded.Visibility
                    },
                    contentDescription = if (uiState.isBalanceVisible) "Ocultar saldo" else "Mostrar saldo",
                    tint = YapePurple,
                  )
                }

                AnimatedVisibility(visible = uiState.isBalanceVisible) {
                  Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                      text = "Saldo disponible",
                      color = YapeTextSecondary,
                      style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                      text = formatSoles(uiState.balance),
                      color = YapeTextPrimary,
                      style = MaterialTheme.typography.displayLarge.copy(fontSize = 30.sp),
                      modifier = Modifier.testTag("home_balance_text"),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val ratio = (uiState.dailySentAmount / uiState.effectiveDailyLimit)
                      .toFloat()
                      .coerceIn(0f, 1f)
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                      Text(
                        text = "Acumulado enviado hoy",
                        color = YapeTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                      )
                      Text(
                        text = "${formatSoles(uiState.dailySentAmount)} de ${formatSoles(uiState.effectiveDailyLimit)}",
                        color = YapePurple,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("home_daily_sent_text"),
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                      progress = { ratio },
                      color = if (ratio >= 0.9f) YapeMagenta else YapeTurquoise,
                      trackColor = YapeTrayBackground,
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Bottom White Tray: "Últimos movimientos"
      item {
        Surface(
          shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
          color = YapeWhite,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = if (isAyudante) "Cobros recibidos" else "Últimos movimientos",
                style = MaterialTheme.typography.titleMedium,
                color = YapeTextPrimary,
                fontWeight = FontWeight.Bold,
              )
              Surface(
                color = YapePurpleLight,
                shape = RoundedCornerShape(50),
              ) {
                Text(
                  text = "${displayedTransactions.size} recientes",
                  color = YapePurple,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }

      items(displayedTransactions, key = { it.id }) { tx ->
        Surface(
          color = YapeWhite,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { selectedTxDetail = tx },
        ) {
          Column {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                shape = CircleShape,
                color = if (tx.isIncome) YapeTurquoiseLight else YapePurpleLight,
                modifier = Modifier.size(42.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = if (tx.isIncome) {
                      Icons.Rounded.ArrowDownward
                    } else {
                      Icons.Rounded.ArrowUpward
                    },
                    contentDescription = null,
                    tint = if (tx.isIncome) YapeTurquoiseDark else YapePurple,
                    modifier = Modifier.size(20.dp),
                  )
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = tx.title,
                  style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                  color = YapeTextPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "${tx.subtitle} • ${tx.timestampLabel}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                  color = YapeTextSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = (if (tx.isIncome) "+ " else "- ") + formatSoles(tx.amount),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = if (tx.isIncome) YapeTurquoiseDark else YapeTextPrimary,
                fontWeight = FontWeight.Bold,
              )
            }
            HorizontalDivider(
              color = YapeDivider.copy(alpha = 0.7f),
              modifier = Modifier.padding(horizontal = 16.dp),
            )
          }
        }
      }
    }
  }

  if (showQrDialog) {
    QrScannerDialog(
      onDismiss = { showQrDialog = false },
      onSelectTestQr = { phone, name, destination, amount ->
        showQrDialog = false
        onQrScanned(phone, name, destination, amount)
      },
    )
  }

  selectedTxDetail?.let { tx ->
    AlertDialog(
      onDismissRequest = { selectedTxDetail = null },
      containerColor = YapeWhite,
      title = {
        Text(
          text = tx.title,
          color = YapePurpleDark,
          fontWeight = FontWeight.Bold,
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = (if (tx.isIncome) "+ " else "- ") + formatSoles(tx.amount),
            style = MaterialTheme.typography.headlineMedium,
            color = if (tx.isIncome) YapeTurquoiseDark else YapePurple,
          )
          Text("Tipo: ${tx.subtitle}", style = MaterialTheme.typography.bodyMedium)
          Text("Destino: ${tx.destinationLabel}", style = MaterialTheme.typography.bodyMedium)
          Text("Fecha: ${tx.timestampLabel}", style = MaterialTheme.typography.bodyMedium)
          if (tx.message.isNotBlank()) {
            Text("Mensaje: \"${tx.message}\"", style = MaterialTheme.typography.bodyMedium)
          }
          Text(
            text = "N° Operación: ${tx.operationNumber} • Cód. Seg: ${tx.securityCode}",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            color = YapeTextSecondary,
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { selectedTxDetail = null }) {
          Text("Cerrar", color = YapePurple, fontWeight = FontWeight.Bold)
        }
      },
    )
  }

  if (activeUtilitySheetTitle != null && activeUtilitySheetBody != null) {
    AlertDialog(
      onDismissRequest = {
        activeUtilitySheetTitle = null
        activeUtilitySheetBody = null
      },
      containerColor = YapeWhite,
      title = {
        Text(
          text = activeUtilitySheetTitle!!,
          color = YapePurpleDark,
          fontWeight = FontWeight.Bold,
        )
      },
      text = {
        Text(
          text = activeUtilitySheetBody!!,
          style = MaterialTheme.typography.bodyLarge,
          color = YapeTextPrimary,
        )
      },
      confirmButton = {
        Button(
          onClick = {
            activeUtilitySheetTitle = null
            activeUtilitySheetBody = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
        ) {
          Text("Entendido", color = YapeWhite, fontWeight = FontWeight.Bold)
        }
      },
    )
  }

  if (showNotificationsDialog) {
    AlertDialog(
      onDismissRequest = { showNotificationsDialog = false },
      containerColor = YapeWhite,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Rounded.Shield, contentDescription = null, tint = YapePurple)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Notificaciones Yape", color = YapePurpleDark, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(color = YapeTurquoiseLight, shape = RoundedCornerShape(12.dp)) {
            Text(
              text = "• María Fernanda Quispe te yapeó S/ 145.00 hoy a las 10:18 a. m.",
              style = MaterialTheme.typography.bodyMedium,
              color = YapeTextPrimary,
              modifier = Modifier.padding(10.dp),
            )
          }
          Surface(color = YapeMagentaLight, shape = RoundedCornerShape(12.dp)) {
            Text(
              text = "• Tienes un Crédito Yape pre-aprobado de hasta S/ 500.00 al instante.",
              style = MaterialTheme.typography.bodyMedium,
              color = YapeTextPrimary,
              modifier = Modifier.padding(10.dp),
            )
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showNotificationsDialog = false }) {
          Text("Cerrar", color = YapePurple, fontWeight = FontWeight.Bold)
        }
      },
    )
  }
}

@Composable
private fun QuickActionCircle(
  action: HomeQuickAction,
  enabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(enabled = enabled, onClick = onClick)
      .padding(vertical = 4.dp, horizontal = 2.dp)
      .testTag("home_action_${action.id}"),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      Surface(
        shape = CircleShape,
        color = if (enabled) YapeWhite else YapeWhite.copy(alpha = 0.45f),
        shadowElevation = if (enabled) 3.dp else 0.dp,
        modifier = Modifier.size(50.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = action.icon,
            contentDescription = action.label,
            tint = if (enabled) action.accentColor else action.accentColor.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp),
          )
        }
      }
      if (action.isNew && enabled) {
        Surface(
          color = YapeMagenta,
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text = "Nuevo",
            color = YapeWhite,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
          )
        }
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = action.label,
      color = YapeWhite.copy(alpha = if (enabled) 0.96f else 0.5f),
      fontSize = 11.sp,
      lineHeight = 13.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
