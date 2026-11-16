package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoucherData
import com.example.model.formatSoles
import com.example.ui.components.TicketDashedDivider
import com.example.ui.components.YapeBrandLogo
import com.example.ui.components.yapeTicketShape
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.YapePurple
import com.example.ui.theme.YapePurpleDark
import com.example.ui.theme.YapeTextPrimary
import com.example.ui.theme.YapeTextSecondary
import com.example.ui.theme.YapeTrayBackground
import com.example.ui.theme.YapeTurquoise
import com.example.ui.theme.YapeTurquoiseLight
import com.example.ui.theme.YapeWhite

@Composable
fun VoucherScreen(
  voucher: VoucherData?,
  onGoHome: () -> Unit,
) {
  val context = LocalContext.current
  var appear by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    appear = true
  }

  val checkScale by animateFloatAsState(
    targetValue = if (appear) 1f else 0.2f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow,
    ),
    label = "check_scale",
  )

  val safeVoucher = voucher ?: VoucherData(
    title = "¡Yapeaste!",
    amount = 120.00,
    recipientName = "Lucía Paredes Quispe",
    recipientDetail = "987 654 321",
    dateTimeLabel = "13 nov. 2026 • 11:23 p. m.",
    destinationLabel = "Yape",
    message = "",
    securityCode = "482",
    operationNumber = "84920144",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(listOf(YapePurple, YapePurpleDark))
      )
      .statusBarsPadding()
      .navigationBarsPadding(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      YapeBrandLogo(compact = true)

      Spacer(modifier = Modifier.height(14.dp))

      // Official White Ticket Card with Serrated Top/Bottom & Side Notches
      Surface(
        shape = yapeTicketShape(notchYFraction = 0.42f),
        color = YapeWhite,
        shadowElevation = 10.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("official_voucher_ticket"),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Animated Turquoise Checkmark
          Box(
            modifier = Modifier
              .size(72.dp)
              .scale(checkScale)
              .clip(CircleShape)
              .background(YapeTurquoiseLight),
            contentAlignment = Alignment.Center,
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(YapeTurquoise),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Operación exitosa",
                tint = YapeWhite,
                modifier = Modifier.size(34.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = safeVoucher.title,
            color = YapePurple,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("voucher_title"),
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = formatSoles(safeVoucher.amount),
            color = YapePurple,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.testTag("voucher_amount"),
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = safeVoucher.recipientName,
            color = YapeTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
          )
          Text(
            text = safeVoucher.recipientDetail,
            color = YapeTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = safeVoucher.dateTimeLabel,
            color = YapeTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
          )

          Spacer(modifier = Modifier.height(20.dp))
          TicketDashedDivider()
          Spacer(modifier = Modifier.height(18.dp))

          VoucherDetailRow(
            label = "Destino",
            value = safeVoucher.destinationLabel,
          )
          if (safeVoucher.message.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            VoucherDetailRow(
              label = "Mensaje",
              value = safeVoucher.message,
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Security Code: 3 random digits inside 3 individual grey boxes
          Text(
            text = "CÓDIGO DE SEGURIDAD",
            color = YapeTextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.testTag("voucher_security_code_boxes"),
          ) {
            safeVoucher.securityCode.take(3).padEnd(3, '0').forEach { digitChar ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = YapeTrayBackground,
                border = BorderStroke(1.dp, YapePurple.copy(alpha = 0.16f)),
                modifier = Modifier.size(width = 46.dp, height = 50.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = digitChar.toString(),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = YapePurpleDark,
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "N° de operación",
              color = YapeTextSecondary,
              style = MaterialTheme.typography.bodyMedium,
            )
            Text(
              text = safeVoucher.operationNumber,
              fontFamily = JetBrainsMonoFontFamily,
              color = YapeTextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              modifier = Modifier.testTag("voucher_operation_number"),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons: "Compartir" & "Ir al inicio"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = {
            val shareText = buildString {
              appendLine("${safeVoucher.title} ${formatSoles(safeVoucher.amount)}")
              appendLine("Destinatario: ${safeVoucher.recipientName}")
              appendLine("Destino: ${safeVoucher.destinationLabel}")
              appendLine("Fecha: ${safeVoucher.dateTimeLabel}")
              appendLine("Código de seguridad: ${safeVoucher.securityCode}")
              append("N° de operación: ${safeVoucher.operationNumber}")
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir comprobante Yape"))
          },
          border = BorderStroke(1.5.dp, YapeWhite),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("voucher_share_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Share,
            contentDescription = null,
            tint = YapeWhite,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Compartir", color = YapeWhite, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onGoHome,
          colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("voucher_home_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Home,
            contentDescription = null,
            tint = YapeWhite,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Ir al inicio", color = YapeWhite, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun VoucherDetailRow(
  label: String,
  value: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      color = YapeTextSecondary,
      style = MaterialTheme.typography.bodyMedium,
    )
    Text(
      text = value,
      color = YapeTextPrimary,
      style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
      fontWeight = FontWeight.SemiBold,
      textAlign = TextAlign.End,
      modifier = Modifier.weight(1f, fill = false),
    )
  }
}
