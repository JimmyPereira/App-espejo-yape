package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UtilityCompany
import com.example.model.formatSoles
import com.example.ui.components.RedValidationModal
import com.example.ui.theme.JetBrainsMonoFontFamily
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
fun ServiciosScreen(
  uiState: YapeUiState,
  onPayService: (UtilityCompany, String) -> Boolean,
  onDismissValidationAlert: () -> Unit,
  onNavigateToVoucher: () -> Unit,
  onBack: () -> Unit,
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCompany by remember { mutableStateOf<UtilityCompany?>(null) }
  var supplyCodeInput by remember { mutableStateOf("") }
  var queriedReceipt by remember { mutableStateOf(false) }

  val filteredCompanies = remember(searchQuery, uiState.utilityCompanies) {
    if (searchQuery.isBlank()) {
      uiState.utilityCompanies
    } else {
      uiState.utilityCompanies.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
          it.category.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  Scaffold(
    containerColor = YapeTrayBackground,
    topBar = {
      Surface(color = YapePurple, modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = {
              if (selectedCompany != null) {
                selectedCompany = null
                queriedReceipt = false
                supplyCodeInput = ""
              } else {
                onBack()
              }
            },
            modifier = Modifier.testTag("servicios_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "Volver",
              tint = YapeWhite,
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column {
            Text(
              text = "Yapear servicios",
              color = YapeWhite,
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              text = "Paga luz, agua y telefonía sin comisiones (CP-008)",
              color = YapeWhite.copy(alpha = 0.8f),
              style = MaterialTheme.typography.labelSmall,
            )
          }
        }
      }
    },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding()
        .imePadding()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      if (selectedCompany == null) {
        item {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = {
              Icon(Icons.Rounded.Search, contentDescription = null, tint = YapePurple)
            },
            placeholder = { Text("Buscar empresa (Electrocentro, Sedapal...)") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = YapeWhite,
              unfocusedContainerColor = YapeWhite,
              focusedBorderColor = YapePurple,
              unfocusedBorderColor = YapeDivider,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("servicios_search_input"),
          )
        }

        item {
          Text(
            text = "EMPRESAS DISPONIBLES",
            style = MaterialTheme.typography.labelSmall,
            color = YapeTextSecondary,
            fontWeight = FontWeight.Bold,
          )
        }

        items(filteredCompanies, key = { it.id }) { company ->
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = YapeWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                selectedCompany = company
                supplyCodeInput = ""
                queriedReceipt = false
              }
              .testTag("service_company_${company.id}"),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                shape = CircleShape,
                color = YapePurpleLight,
                modifier = Modifier.size(48.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = serviceIconFor(company.id),
                    contentDescription = null,
                    tint = YapePurple,
                  )
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = company.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = YapeTextPrimary,
                )
                Text(
                  text = company.category,
                  style = MaterialTheme.typography.bodyMedium,
                  color = YapeTextSecondary,
                )
              }
              Text(
                text = "Elegir",
                color = YapeTurquoiseDark,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }
      } else {
        val company = selectedCompany!!
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = YapeWhite),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = CircleShape,
                    color = YapePurpleLight,
                    modifier = Modifier.size(44.dp),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(serviceIconFor(company.id), contentDescription = null, tint = YapePurple)
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(company.name, style = MaterialTheme.typography.titleMedium, color = YapeTextPrimary)
                    Text(company.category, style = MaterialTheme.typography.bodyMedium, color = YapeTextSecondary)
                  }
                }
                TextButton(
                  onClick = {
                    selectedCompany = null
                    queriedReceipt = false
                  },
                ) {
                  Text("Cambiar", color = YapePurple, fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = supplyCodeInput,
                onValueChange = { raw ->
                  supplyCodeInput = raw.filter { it.isDigit() }.take(10)
                  queriedReceipt = false
                },
                label = { Text("N° de suministro o código de cliente") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = YapePurple,
                  focusedLabelColor = YapePurple,
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("supply_code_input"),
              )

              Spacer(modifier = Modifier.height(8.dp))

              // 1-Click Preload Chip
              AssistChip(
                onClick = {
                  supplyCodeInput = company.sampleSupplyCode
                  queriedReceipt = true
                },
                label = {
                  Text(
                    text = "Usar suministro de prueba: ${company.sampleSupplyCode} (1 clic)",
                    fontWeight = FontWeight.SemiBold,
                  )
                },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = YapePurple,
                    modifier = Modifier.size(16.dp),
                  )
                },
                colors = AssistChipDefaults.assistChipColors(
                  containerColor = YapePurpleLight,
                  labelColor = YapePurpleDark,
                ),
                modifier = Modifier.testTag("preload_supply_chip"),
              )

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { queriedReceipt = true },
                enabled = supplyCodeInput.length >= 6,
                colors = ButtonDefaults.buttonColors(containerColor = YapePurple),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("query_receipt_button"),
              ) {
                Text("Consultar recibo", color = YapeWhite, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        item {
          AnimatedVisibility(visible = queriedReceipt) {
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = YapeWhite),
              border = BorderStroke(1.5.dp, YapeTurquoise),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("pending_receipt_card"),
            ) {
              Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                  color = YapeTurquoiseLight,
                  shape = RoundedCornerShape(50),
                ) {
                  Text(
                    text = "RECIBO PENDIENTE ENCONTRADO",
                    color = YapeTurquoiseDark,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = formatSoles(company.pendingAmount),
                  color = YapePurple,
                  fontSize = 34.sp,
                  fontWeight = FontWeight.ExtraBold,
                  modifier = Modifier.testTag("receipt_pending_amount"),
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = YapeDivider)
                Spacer(modifier = Modifier.height(10.dp))
                ReceiptLine("Titular", "Jimmy Pereira")
                ReceiptLine("Empresa", company.name)
                ReceiptLine("N° Suministro", supplyCodeInput, mono = true)
                ReceiptLine("Periodo", company.periodLabel)
                ReceiptLine("Vencimiento", company.dueDateLabel)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                  onClick = {
                    val paid = onPayService(company, supplyCodeInput)
                    if (paid) {
                      onNavigateToVoucher()
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("pay_service_button"),
                ) {
                  Text(
                    text = "Pagar ${formatSoles(company.pendingAmount)}",
                    color = YapeWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  uiState.validationAlert?.let { alert ->
    RedValidationModal(
      alert = alert,
      onDismiss = onDismissValidationAlert,
    )
  }
}

@Composable
private fun ReceiptLine(label: String, value: String, mono: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(label, color = YapeTextSecondary, style = MaterialTheme.typography.bodyMedium)
    Text(
      text = value,
      color = YapeTextPrimary,
      fontFamily = if (mono) JetBrainsMonoFontFamily else MaterialTheme.typography.bodyMedium.fontFamily,
      fontWeight = FontWeight.SemiBold,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

private fun serviceIconFor(companyId: String): ImageVector = when (companyId) {
  "electrocentro", "luz_del_sur" -> Icons.Rounded.Bolt
  "sedapal" -> Icons.Rounded.WaterDrop
  else -> Icons.Rounded.PhoneIphone
}
