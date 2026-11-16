package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InteropDestination
import com.example.model.YapeContact
import com.example.model.formatPhoneSpaced
import com.example.model.formatSoles
import com.example.ui.components.RedValidationModal
import com.example.ui.components.SimulatedFaceVerificationModal
import com.example.ui.theme.YapeDivider
import com.example.ui.theme.YapePurple
import com.example.ui.theme.YapePurpleDark
import com.example.ui.theme.YapePurpleLight
import com.example.ui.theme.YapeTextPrimary
import com.example.ui.theme.YapeTextSecondary
import com.example.ui.theme.YapeTrayBackground
import com.example.ui.theme.YapeTurquoise
import com.example.ui.theme.YapeTurquoiseDark
import com.example.ui.theme.YapeTurquoiseLight
import com.example.ui.theme.YapeWhite
import com.example.viewmodel.YapeUiState

@Composable
fun YapeoScreen(
  uiState: YapeUiState,
  resolveTitularName: (phone: String, destination: InteropDestination) -> String?,
  onAttemptYapeo: (
    recipientName: String,
    phone: String,
    destination: InteropDestination,
    amount: Double,
    message: String,
  ) -> Boolean,
  onDismissValidationAlert: () -> Unit,
  onExpandLimitBiometric: () -> Unit,
  onNavigateToVoucher: () -> Unit,
  onBack: () -> Unit,
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Contactos, 1 = Nuevo número
  var step by remember { mutableIntStateOf(1) } // 1 = Destinatario, 2 = Monto
  var newPhoneInput by remember { mutableStateOf("") }
  var selectedDestination by remember { mutableStateOf(InteropDestination.YAPE) }
  var chosenName by remember { mutableStateOf("") }
  var chosenPhone by remember { mutableStateOf("") }
  var amountText by remember { mutableStateOf("120.00") }
  var messageText by remember { mutableStateOf("") }
  var showBiometricExpandModal by remember { mutableStateOf(false) }

  // Support QR scan prefill
  LaunchedEffect(uiState.prefillPhone) {
    if (uiState.prefillPhone != null && uiState.prefillName != null) {
      chosenPhone = uiState.prefillPhone
      chosenName = uiState.prefillName
      selectedDestination = uiState.prefillDestination ?: InteropDestination.YAPE
      amountText = uiState.prefillAmount ?: "120.00"
      step = 2
    }
  }

  val resolvedNewPhoneTitular = remember(newPhoneInput, selectedDestination) {
    resolveTitularName(newPhoneInput, selectedDestination)
  }

  val isNewPhoneValid = newPhoneInput.length == 9 && newPhoneInput.startsWith("9")

  Scaffold(
    containerColor = YapeTrayBackground,
    topBar = {
      Surface(
        color = YapePurple,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = {
              if (step == 2) step = 1 else onBack()
            },
            modifier = Modifier.testTag("yapeo_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Volver",
              tint = YapeWhite,
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (step == 1) "Yapear a celular" else "Ingresa el monto",
              color = YapeWhite,
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              text = "Interoperabilidad BCRP • Circular N° 0024-2022",
              color = YapeWhite.copy(alpha = 0.8f),
              style = MaterialTheme.typography.labelSmall,
            )
          }
        }
      }
    },
  ) { innerPadding ->
    AnimatedContent(
      targetState = step,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding()
        .imePadding(),
      label = "yapeo_step",
    ) { currentStep ->
      if (currentStep == 1) {
        // STEP 1: Contacts / New Number + Interop Destination Selector
        Column(modifier = Modifier.fillMaxSize()) {
          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = YapeWhite,
            contentColor = YapePurple,
            indicator = { tabPositions ->
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = YapeTurquoise,
                height = 3.dp,
              )
            },
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              modifier = Modifier.testTag("yapeo_tab_contacts"),
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Rounded.Contacts, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Contactos", fontWeight = FontWeight.Bold)
                }
              },
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              modifier = Modifier.testTag("yapeo_tab_new_number"),
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Rounded.Dialpad, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Yapear a nuevo número", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              },
            )
          }

          if (selectedTab == 0) {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              item {
                InteropDestinationSelector(
                  selectedDestination = selectedDestination,
                  onSelect = { selectedDestination = it },
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "CONTACTOS DE PRUEBA (${uiState.contacts.size})",
                  style = MaterialTheme.typography.labelSmall,
                  color = YapeTextSecondary,
                  fontWeight = FontWeight.Bold,
                )
              }

              items(uiState.contacts, key = { it.id }) { contact ->
                ContactCardItem(
                  contact = contact,
                  onClick = {
                    chosenName = contact.name
                    chosenPhone = contact.phone
                    selectedDestination = contact.defaultDestination
                    step = 2
                  },
                )
              }
            }
          } else {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = YapeWhite),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Número de celular destino",
                    style = MaterialTheme.typography.titleMedium,
                    color = YapeTextPrimary,
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Debe tener 9 dígitos y empezar con el número 9.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = YapeTextSecondary,
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  OutlinedTextField(
                    value = newPhoneInput,
                    onValueChange = { raw ->
                      val digits = raw.filter { it.isDigit() }.take(9)
                      newPhoneInput = digits
                    },
                    label = { Text("Celular (ej. 987654321)") },
                    prefix = { Text("+51 ", fontWeight = FontWeight.Bold, color = YapePurple) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = newPhoneInput.isNotEmpty() && !newPhoneInput.startsWith("9"),
                    supportingText = {
                      if (newPhoneInput.isNotEmpty() && !newPhoneInput.startsWith("9")) {
                        Text("El número debe iniciar con 9")
                      } else {
                        Text("${newPhoneInput.length}/9 dígitos")
                      }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = YapePurple,
                      focusedLabelColor = YapePurple,
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("new_phone_input"),
                  )
                }
              }

              InteropDestinationSelector(
                selectedDestination = selectedDestination,
                onSelect = { selectedDestination = it },
              )

              AnimatedVisibility(visible = isNewPhoneValid && resolvedNewPhoneTitular != null) {
                Card(
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = YapeTurquoiseLight),
                  border = BorderStroke(1.dp, YapeTurquoise),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verified_titular_card"),
                ) {
                  Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.Verified,
                      contentDescription = "Titular verificado",
                      tint = YapeTurquoiseDark,
                      modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "Titular verificado • ${selectedDestination.shortTag}",
                        style = MaterialTheme.typography.labelSmall,
                        color = YapeTurquoiseDark,
                        fontWeight = FontWeight.Bold,
                      )
                      Text(
                        text = resolvedNewPhoneTitular ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = YapeTextPrimary,
                        fontWeight = FontWeight.Bold,
                      )
                      Text(
                        text = formatPhoneSpaced(newPhoneInput),
                        style = MaterialTheme.typography.bodyMedium,
                        color = YapeTextSecondary,
                      )
                    }
                  }
                }
              }

              Button(
                onClick = {
                  if (isNewPhoneValid && resolvedNewPhoneTitular != null) {
                    chosenName = resolvedNewPhoneTitular
                    chosenPhone = newPhoneInput
                    step = 2
                  }
                },
                enabled = isNewPhoneValid && resolvedNewPhoneTitular != null,
                colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(52.dp)
                  .testTag("continue_to_amount_button"),
              ) {
                Text("Continuar al monto", fontWeight = FontWeight.Bold, color = YapeWhite)
              }
            }
          }
        }
      } else {
        // STEP 2: Amount Entry + Quick Test Chips + RF-03 / RF-06 Validations
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          // Selected Verified Recipient Card
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = YapeWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                shape = CircleShape,
                color = YapePurpleLight,
                modifier = Modifier.size(44.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = YapeTurquoiseDark)
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = chosenName,
                  style = MaterialTheme.typography.titleMedium,
                  color = YapeTextPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "${formatPhoneSpaced(chosenPhone)} • ${selectedDestination.displayName}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = YapeTextSecondary,
                )
              }
              TextButton(onClick = { step = 1 }) {
                Text("Cambiar", color = YapePurple, fontWeight = FontWeight.Bold)
              }
            }
          }

          // Interop Destination Quick Selector
          InteropDestinationSelector(
            selectedDestination = selectedDestination,
            onSelect = { selectedDestination = it },
          )

          // Amount Input Card with large Purple "S/"
          Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = YapeWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(
                text = "¿Cuánto deseas yapear?",
                style = MaterialTheme.typography.bodyMedium,
                color = YapeTextSecondary,
              )
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
              ) {
                Text(
                  text = "S/",
                  color = YapePurple,
                  fontSize = 40.sp,
                  fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                  value = amountText,
                  onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) {
                      amountText = filtered.take(9)
                    }
                  },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  textStyle = TextStyle(
                    color = YapePurple,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                  ),
                  cursorBrush = SolidColor(YapeTurquoise),
                  modifier = Modifier
                    .width(180.dp)
                    .testTag("yapeo_amount_input"),
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Quick Test Chips required by prompt
              Text(
                text = "CHIPS RÁPIDOS DE PRUEBA (RF-03 / RF-06)",
                style = MaterialTheme.typography.labelSmall,
                color = YapeTextSecondary,
                fontWeight = FontWeight.Bold,
              )
              Spacer(modifier = Modifier.height(8.dp))

              val quickChips = listOf("0.09", "0.10", "120.00", "500.00", "500.01")
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                quickChips.forEach { chipVal ->
                  val isSelected = amountText == chipVal
                  FilterChip(
                    selected = isSelected,
                    onClick = { amountText = chipVal },
                    label = {
                      Text(
                        text = "S/ $chipVal",
                        fontWeight = FontWeight.Bold,
                      )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = YapePurpleLight,
                      selectedLabelColor = YapePurpleDark,
                    ),
                    modifier = Modifier.testTag("quick_chip_$chipVal"),
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it.take(60) },
                label = { Text("Agregar mensaje (opcional)") },
                placeholder = { Text("Ej. Almuerzo, pago de pedido...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = YapePurple,
                  focusedLabelColor = YapePurple,
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("yapeo_message_input"),
              )

              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = "Saldo: ${formatSoles(uiState.balance)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = YapeTextSecondary,
                )
                Text(
                  text = "Hoy: ${formatSoles(uiState.dailySentAmount)} / ${formatSoles(uiState.effectiveDailyLimit)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = YapePurple,
                  fontWeight = FontWeight.Bold,
                )
              }
            }
          }

          val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
          Button(
            onClick = {
              val success = onAttemptYapeo(
                chosenName,
                chosenPhone,
                selectedDestination,
                parsedAmount,
                messageText,
              )
              if (success) {
                onNavigateToVoucher()
              }
            },
            enabled = parsedAmount > 0.0,
            colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("confirm_yapear_button"),
          ) {
            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null, tint = YapeWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Yapear ${if (parsedAmount > 0) formatSoles(parsedAmount) else ""}",
              color = YapeWhite,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
            )
          }
        }
      }
    }
  }

  uiState.validationAlert?.let { alert ->
    RedValidationModal(
      alert = alert,
      onDismiss = onDismissValidationAlert,
      onBiometricExpandClick = if (alert.canExpandLimitBiometric) {
        { showBiometricExpandModal = true }
      } else null,
    )
  }

  if (showBiometricExpandModal) {
    SimulatedFaceVerificationModal(
      title = "Ampliación de Tope Diario",
      subtitle = "Valida tu rostro en RENIEC para ampliar tu límite diario hasta S/ 3,500.00",
      onSuccess = {
        showBiometricExpandModal = false
        onExpandLimitBiometric()
      },
      onDismiss = { showBiometricExpandModal = false },
    )
  }
}

@Composable
private fun InteropDestinationSelector(
  selectedDestination: InteropDestination,
  onSelect: (InteropDestination) -> Unit,
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = YapeWhite),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.AccountBalance,
          contentDescription = null,
          tint = YapePurple,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Destino interoperable (BCRP)",
          style = MaterialTheme.typography.labelLarge,
          color = YapePurpleDark,
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InteropDestination.entries.forEach { dest ->
          val selected = dest == selectedDestination
          Surface(
            color = if (selected) YapePurpleLight else YapeTrayBackground,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
              width = if (selected) 1.5.dp else 1.dp,
              color = if (selected) YapePurple else YapeDivider,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { onSelect(dest) }
              .testTag("interop_dest_${dest.name.lowercase()}"),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = dest.displayName,
                  style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                  color = if (selected) YapePurpleDark else YapeTextPrimary,
                )
                Text(
                  text = dest.subtitle,
                  style = MaterialTheme.typography.labelSmall,
                  color = YapeTextSecondary,
                )
              }
              if (selected) {
                Icon(
                  imageVector = Icons.Rounded.CheckCircle,
                  contentDescription = "Seleccionado",
                  tint = YapeTurquoiseDark,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ContactCardItem(
  contact: YapeContact,
  onClick: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = YapeWhite),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("contact_card_${contact.id}"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = YapePurpleLight,
        modifier = Modifier.size(46.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = contact.initials,
            color = YapePurpleDark,
            fontWeight = FontWeight.Bold,
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = contact.name,
          style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
          color = YapeTextPrimary,
        )
        Text(
          text = formatPhoneSpaced(contact.phone),
          style = MaterialTheme.typography.bodyMedium,
          color = YapeTextSecondary,
        )
      }
      Surface(
        color = YapeTurquoiseLight,
        shape = RoundedCornerShape(50),
      ) {
        Text(
          text = contact.defaultDestination.shortTag,
          color = YapeTurquoiseDark,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
      }
    }
  }
}
