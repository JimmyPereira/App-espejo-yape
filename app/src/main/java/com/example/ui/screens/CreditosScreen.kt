package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.formatSoles
import com.example.ui.components.SimulatedFaceVerificationModal
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
import com.example.ui.theme.YapeWhite
import kotlin.math.roundToInt

@Composable
fun CreditosScreen(
  currentBalance: Double,
  onDisburseCredit: (Double) -> Unit,
  onNavigateToVoucher: () -> Unit,
  onBack: () -> Unit,
) {
  var sliderAmount by remember { mutableFloatStateOf(300f) }
  var acceptedSummarySheet by remember { mutableStateOf(false) }
  var showFaceBiometric by remember { mutableStateOf(false) }

  val requestedAmount = ((sliderAmount / 50f).roundToInt() * 50).coerceIn(100, 500).toDouble()
  val interestAndInsurance = (requestedAmount * 0.062 * 100.0).roundToInt() / 100.0
  val totalToPay = ((requestedAmount + interestAndInsurance) * 100.0).roundToInt() / 100.0
  val tceaPercent = "108.45%"

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
            onClick = onBack,
            modifier = Modifier.testTag("creditos_back_button"),
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
              text = "Créditos Yape",
              color = YapeWhite,
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              text = "Desembolso inmediato sin papeleos (CP-009)",
              color = YapeWhite.copy(alpha = 0.8f),
              style = MaterialTheme.typography.labelSmall,
            )
          }
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Pre-approved Highlight Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = YapePurpleDark),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Surface(
            color = YapeMagenta,
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = YapeWhite,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "CRÉDITO PRE-APROBADO",
                color = YapeWhite,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Tienes disponible hasta S/ 500.00",
            color = YapeWhite,
            style = MaterialTheme.typography.titleLarge,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tu saldo actual: ${formatSoles(currentBalance)} • Abono en segundos",
            color = YapeWhite.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      // Credit Simulator Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = YapeWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "¿Cuánto necesitas hoy?",
            style = MaterialTheme.typography.titleMedium,
            color = YapeTextPrimary,
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = formatSoles(requestedAmount),
            color = YapePurple,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.testTag("credit_selected_amount"),
          )

          Slider(
            value = sliderAmount,
            onValueChange = { sliderAmount = it },
            valueRange = 100f..500f,
            steps = 7,
            colors = SliderDefaults.colors(
              thumbColor = YapeTurquoise,
              activeTrackColor = YapeTurquoise,
              inactiveTrackColor = YapePurpleLight,
            ),
            modifier = Modifier.testTag("credit_amount_slider"),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            listOf(100, 300, 500).forEach { preset ->
              FilterChip(
                selected = requestedAmount.toInt() == preset,
                onClick = { sliderAmount = preset.toFloat() },
                label = { Text("S/ $preset.00", fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = YapePurpleLight,
                  selectedLabelColor = YapePurpleDark,
                ),
                modifier = Modifier.testTag("credit_preset_$preset"),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            color = YapeTrayBackground,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = YapePurple)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Plazo: 1 cuota de 30 días",
                  style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                  color = YapeTextPrimary,
                )
                Text(
                  text = "Fecha única de pago: 13 dic. 2026",
                  style = MaterialTheme.typography.labelSmall,
                  color = YapeTextSecondary,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = YapeDivider)
          Spacer(modifier = Modifier.height(12.dp))

          CreditBreakdownRow("Monto a desembolsar", formatSoles(requestedAmount))
          CreditBreakdownRow("Interés + Seguro desgravamen", formatSoles(interestAndInsurance))
          CreditBreakdownRow("TCEA referencial", tceaPercent, highlightColor = YapeMagenta)

          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            color = YapeTurquoiseLight,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Total a pagar (1 cuota)",
                color = YapeTurquoiseDark,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = formatSoles(totalToPay),
                color = YapeTurquoiseDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.testTag("credit_total_to_pay"),
              )
            }
          }
        }
      }

      // Hoja Resumen Acceptance Checkbox
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = YapeWhite),
        border = BorderStroke(
          width = 1.dp,
          color = if (acceptedSummarySheet) YapeTurquoise else YapeDivider,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { acceptedSummarySheet = !acceptedSummarySheet }
          .testTag("credit_hoja_resumen_card"),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Checkbox(
            checked = acceptedSummarySheet,
            onCheckedChange = { acceptedSummarySheet = it },
            colors = CheckboxDefaults.colors(
              checkedColor = YapeTurquoise,
              uncheckedColor = YapePurple,
            ),
            modifier = Modifier.testTag("credit_hoja_resumen_checkbox"),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "He leído y acepto la Hoja Resumen, TCEA ($tceaPercent) y Condiciones del Crédito Yape.",
            style = MaterialTheme.typography.bodyMedium,
            color = YapeTextPrimary,
          )
        }
      }

      Button(
        onClick = { showFaceBiometric = true },
        enabled = acceptedSummarySheet,
        colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("credit_disburse_button"),
      ) {
        Icon(Icons.Rounded.Face, contentDescription = null, tint = YapeWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Validar rostro y desembolsar ${formatSoles(requestedAmount)}",
          color = YapeWhite,
          fontWeight = FontWeight.ExtraBold,
        )
      }
    }
  }

  if (showFaceBiometric) {
    SimulatedFaceVerificationModal(
      title = "Validación Facial de Crédito",
      subtitle = "Confirmando tu identidad en RENIEC para abonar ${formatSoles(requestedAmount)} al instante",
      onSuccess = {
        showFaceBiometric = false
        onDisburseCredit(requestedAmount)
        onNavigateToVoucher()
      },
      onDismiss = { showFaceBiometric = false },
    )
  }
}

@Composable
private fun CreditBreakdownRow(
  label: String,
  value: String,
  highlightColor: androidx.compose.ui.graphics.Color = YapeTextPrimary,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(label, color = YapeTextSecondary, style = MaterialTheme.typography.bodyMedium)
    Text(
      text = value,
      color = highlightColor,
      fontWeight = FontWeight.Bold,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}
