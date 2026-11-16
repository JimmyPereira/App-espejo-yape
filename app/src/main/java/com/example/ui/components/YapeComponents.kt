package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.InteropDestination
import com.example.model.ValidationAlert
import com.example.ui.theme.YapeDivider
import com.example.ui.theme.YapeErrorRed
import com.example.ui.theme.YapeErrorRedLight
import com.example.ui.theme.YapeMagenta
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
import kotlinx.coroutines.delay

@Composable
fun YapeBrandLogo(
  modifier: Modifier = Modifier,
  compact: Boolean = false,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(if (compact) 12.dp else 18.dp),
      color = YapeTurquoise,
      shadowElevation = 4.dp,
      modifier = Modifier.size(if (compact) 36.dp else 54.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = "S/",
          color = YapeWhite,
          fontWeight = FontWeight.ExtraBold,
          fontSize = if (compact) 17.sp else 26.sp,
        )
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(if (compact) 4.dp else 6.dp)
            .size(if (compact) 6.dp else 9.dp)
            .clip(CircleShape)
            .background(YapeMagenta)
        )
      }
    }
    Spacer(modifier = Modifier.width(if (compact) 8.dp else 12.dp))
    Column {
      Text(
        text = "yape",
        color = YapeWhite,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (compact) 22.sp else 32.sp,
        letterSpacing = (-0.8).sp,
      )
      if (!compact) {
        Text(
          text = "PERÚ • BILLETERA DIGITAL",
          color = YapeWhite.copy(alpha = 0.8f),
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.2.sp,
        )
      }
    }
  }
}

@Composable
fun RedValidationModal(
  alert: ValidationAlert,
  onDismiss: () -> Unit,
  onBiometricExpandClick: (() -> Unit)? = null,
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = YapeWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("validation_error_modal"),
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(YapeErrorRedLight),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = "Alerta de validación",
            tint = YapeErrorRed,
            modifier = Modifier.size(36.dp),
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          color = YapeErrorRedLight,
          shape = RoundedCornerShape(50),
        ) {
          Text(
            text = "REGLA ${alert.code}",
            color = YapeErrorRed,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = alert.title,
          style = MaterialTheme.typography.titleLarge,
          color = YapeErrorRed,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = alert.message,
          style = MaterialTheme.typography.bodyLarge,
          color = YapeTextPrimary,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (alert.canExpandLimitBiometric && onBiometricExpandClick != null) {
          Button(
            onClick = onBiometricExpandClick,
            colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("expand_limit_biometric_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Fingerprint,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ampliar límite con biometría", fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = YapeErrorRed),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("validation_modal_dismiss_button"),
        ) {
          Text("Entendido", fontWeight = FontWeight.Bold, color = YapeWhite)
        }
      }
    }
  }
}

@Composable
fun SimulatedFaceVerificationModal(
  title: String,
  subtitle: String,
  onSuccess: () -> Unit,
  onDismiss: () -> Unit,
) {
  var progress by remember { mutableFloatStateOf(0f) }
  var completed by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
  val scanOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scan_offset",
  )

  LaunchedEffect(Unit) {
    val steps = 20
    for (i in 1..steps) {
      delay(85L)
      progress = i / steps.toFloat()
    }
    completed = true
    delay(650L)
    onSuccess()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = !completed, dismissOnClickOutside = false),
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = YapeWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("reniec_biometric_modal"),
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            color = YapePurpleLight,
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.VerifiedUser,
                contentDescription = null,
                tint = YapePurple,
                modifier = Modifier.size(14.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "RENIEC Biométrico",
                style = MaterialTheme.typography.labelSmall,
                color = YapePurple,
                fontWeight = FontWeight.Bold,
              )
            }
          }
          if (!completed) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(40.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cancelar verificación",
                tint = YapeTextSecondary,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          color = YapePurpleDark,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = YapeTextSecondary,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
          modifier = Modifier.size(152.dp),
          contentAlignment = Alignment.Center,
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val ringColor = if (completed) YapeTurquoise else YapePurple
            drawArc(
              color = ringColor.copy(alpha = 0.16f),
              startAngle = 0f,
              sweepAngle = 360f,
              useCenter = false,
              style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
              color = YapeTurquoise,
              startAngle = -90f,
              sweepAngle = 360f * progress,
              useCenter = false,
              style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
            )
            if (!completed) {
              val y = size.height * (0.22f + scanOffset * 0.56f)
              drawLine(
                brush = Brush.horizontalGradient(
                  listOf(Color.Transparent, YapeTurquoise, Color.Transparent)
                ),
                start = Offset(size.width * 0.2f, y),
                end = Offset(size.width * 0.8f, y),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = if (completed) YapeTurquoiseLight else YapePurpleLight,
            modifier = Modifier.size(104.dp),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.Face,
                contentDescription = "Escáner facial",
                tint = if (completed) YapeTurquoiseDark else YapePurple,
                modifier = Modifier.size(56.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
        LinearProgressIndicator(
          progress = { progress },
          color = YapeTurquoise,
          trackColor = YapeTrayBackground,
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50)),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = if (completed) {
            "¡Identidad validada correctamente en RENIEC!"
          } else {
            "Analizando rasgos faciales... ${(progress * 100).toInt()}%"
          },
          style = MaterialTheme.typography.labelLarge,
          color = if (completed) YapeTurquoiseDark else YapePurple,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}

@Composable
fun QrScannerDialog(
  onDismiss: () -> Unit,
  onSelectTestQr: (phone: String, name: String, destination: InteropDestination, amount: String) -> Unit,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "qr_beam")
  val beamY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "beam_y",
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      color = Color(0xFF1A0822),
      modifier = Modifier.fillMaxSize(),
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Escanear QR",
              color = YapeWhite,
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              text = "Compatible con QR Yape, Plin e Izipay",
              color = YapeWhite.copy(alpha = 0.72f),
              style = MaterialTheme.typography.bodyMedium,
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(YapeWhite.copy(alpha = 0.14f)),
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Cerrar escáner QR",
              tint = YapeWhite,
            )
          }
        }

        Box(
          modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.35f)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Rounded.QrCodeScanner,
            contentDescription = "Marco de escaneo QR",
            tint = YapeWhite.copy(alpha = 0.25f),
            modifier = Modifier.size(132.dp),
          )
          Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 4.dp.toPx()
            val len = 34.dp.toPx()
            // 4 turquoise corner brackets
            drawLine(YapeTurquoise, Offset(12f, 12f), Offset(12f + len, 12f), stroke)
            drawLine(YapeTurquoise, Offset(12f, 12f), Offset(12f, 12f + len), stroke)
            drawLine(YapeTurquoise, Offset(size.width - 12f, 12f), Offset(size.width - 12f - len, 12f), stroke)
            drawLine(YapeTurquoise, Offset(size.width - 12f, 12f), Offset(size.width - 12f, 12f + len), stroke)
            drawLine(YapeTurquoise, Offset(12f, size.height - 12f), Offset(12f + len, size.height - 12f), stroke)
            drawLine(YapeTurquoise, Offset(12f, size.height - 12f), Offset(12f, size.height - 12f - len), stroke)
            drawLine(YapeTurquoise, Offset(size.width - 12f, size.height - 12f), Offset(size.width - 12f - len, size.height - 12f), stroke)
            drawLine(YapeTurquoise, Offset(size.width - 12f, size.height - 12f), Offset(size.width - 12f, size.height - 12f - len), stroke)

            val yPos = 24f + beamY * (size.height - 48f)
            drawLine(
              color = YapeTurquoise,
              start = Offset(24f, yPos),
              end = Offset(size.width - 24f, yPos),
              strokeWidth = 3.dp.toPx(),
            )
          }
        }

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "QR DE PRUEBA INTEROPERABLES",
            color = YapeTurquoise,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
          )
          QrPresetCard(
            merchant = "Bodega La Esquina E.I.R.L.",
            detail = "QR Yape • 999 888 777 • S/ 18.50",
            onClick = {
              onSelectTestQr(
                "999888777",
                "Bodega La Esquina E.I.R.L.",
                InteropDestination.YAPE,
                "18.50",
              )
            },
          )
          QrPresetCard(
            merchant = "Café Lima Central S.A.C.",
            detail = "QR Interoperable Plin • 988 776 655 • S/ 120.00",
            onClick = {
              onSelectTestQr(
                "988776655",
                "Café Lima Central S.A.C.",
                InteropDestination.PLIN,
                "120.00",
              )
            },
          )
          OutlinedButton(
            onClick = onDismiss,
            border = BorderStroke(1.dp, YapeWhite.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
          ) {
            Text("Volver al inicio", color = YapeWhite)
          }
        }
      }
    }
  }
}

@Composable
private fun QrPresetCard(
  merchant: String,
  detail: String,
  onClick: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = YapeWhite.copy(alpha = 0.12f)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = YapeTurquoise.copy(alpha = 0.22f),
        modifier = Modifier.size(42.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Rounded.Storefront,
            contentDescription = null,
            tint = YapeTurquoise,
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = merchant,
          color = YapeWhite,
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          text = detail,
          color = YapeWhite.copy(alpha = 0.78f),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
      Text(
        text = "Simular",
        color = YapeTurquoise,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

/**
 * Custom ticket shape with semicircular side cutouts and serrated top/bottom teeth
 * for the official "¡Yapeaste!" voucher card.
 */
fun yapeTicketShape(notchYFraction: Float = 0.38f): GenericShape = GenericShape { size, _ ->
  val toothWidth = 22f
  val toothHeight = 10f
  val notchRadius = 26f
  val notchY = size.height * notchYFraction

  // Start top-left and draw top serrated edge
  moveTo(0f, toothHeight)
  var x = 0f
  var up = true
  while (x < size.width) {
    val nextX = (x + toothWidth / 2f).coerceAtMost(size.width)
    val y = if (up) 0f else toothHeight
    lineTo(nextX, y)
    x = nextX
    up = !up
  }
  lineTo(size.width, toothHeight)

  // Right edge down to notch
  lineTo(size.width, notchY - notchRadius)
  // Right semicircular cutout inward
  cubicTo(
    size.width - notchRadius * 0.65f,
    notchY - notchRadius,
    size.width - notchRadius * 0.65f,
    notchY + notchRadius,
    size.width,
    notchY + notchRadius,
  )
  lineTo(size.width, size.height - toothHeight)

  // Bottom serrated edge right-to-left
  var bx = size.width
  var down = true
  while (bx > 0f) {
    val nextBx = (bx - toothWidth / 2f).coerceAtLeast(0f)
    val y = if (down) size.height else size.height - toothHeight
    lineTo(nextBx, y)
    bx = nextBx
    down = !down
  }
  lineTo(0f, size.height - toothHeight)

  // Left edge up to notch
  lineTo(0f, notchY + notchRadius)
  // Left semicircular cutout inward
  cubicTo(
    notchRadius * 0.65f,
    notchY + notchRadius,
    notchRadius * 0.65f,
    notchY - notchRadius,
    0f,
    notchY - notchRadius,
  )
  close()
}

@Composable
fun TicketDashedDivider(modifier: Modifier = Modifier) {
  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(2.dp)
  ) {
    drawLine(
      color = YapeDivider,
      start = Offset(0f, size.height / 2f),
      end = Offset(size.width, size.height / 2f),
      strokeWidth = 2.dp.toPx(),
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
    )
  }
}
