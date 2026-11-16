package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.GppBad
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SimulatedFaceVerificationModal
import com.example.ui.components.YapeBrandLogo
import com.example.ui.theme.YapeErrorRed
import com.example.ui.theme.YapeErrorRedLight
import com.example.ui.theme.YapeMagenta
import com.example.ui.theme.YapePurple
import com.example.ui.theme.YapePurpleDark
import com.example.ui.theme.YapeTurquoise
import com.example.ui.theme.YapeTurquoiseLight
import com.example.ui.theme.YapeWhite
import com.example.viewmodel.YapeUiState
import kotlin.math.roundToInt

@Composable
fun LoginScreen(
  uiState: YapeUiState,
  onDigitPress: (Int) -> Unit,
  onBackspace: () -> Unit,
  onClearPin: () -> Unit,
  onUnlockWithReniec: () -> Unit,
) {
  var showFaceModal by remember { mutableStateOf(false) }
  val shakeOffset = remember { Animatable(0f) }

  LaunchedEffect(uiState.failedPinAttempts) {
    if (uiState.failedPinAttempts > 0) {
      val sequence = listOf(-18f, 18f, -14f, 14f, -8f, 8f, 0f)
      sequence.forEach { target ->
        shakeOffset.animateTo(target, animationSpec = tween(42))
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(YapePurple, YapePurpleDark)
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 16.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      // Top Logo + Security Pill
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 14.dp),
      ) {
        YapeBrandLogo()
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = YapeWhite.copy(alpha = 0.12f),
          shape = RoundedCornerShape(50),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Shuffle,
              contentDescription = null,
              tint = YapeTurquoise,
              modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Teclado dinámico anti-espía",
              color = YapeWhite.copy(alpha = 0.9f),
              style = MaterialTheme.typography.labelSmall,
            )
          }
        }
      }

      // Middle: Prompt + 6 PIN Dots + Error / Lock Alert
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = "Ingresa tu clave Yape de 6 dígitos",
          color = YapeWhite,
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 6 PIN Indicators
        Row(
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .testTag("pin_dots_row"),
        ) {
          repeat(6) { index ->
            val filled = index < uiState.pinInput.length
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(
                  when {
                    uiState.isAccountLocked -> YapeErrorRed
                    filled -> YapeTurquoise
                    else -> Color.Transparent
                  }
                )
                .border(
                  width = 2.dp,
                  color = when {
                    uiState.isAccountLocked -> YapeErrorRed
                    filled -> YapeTurquoise
                    else -> YapeWhite.copy(alpha = 0.65f)
                  },
                  shape = CircleShape,
                )
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = uiState.reniecUnlockedNotice) {
          Surface(
            color = YapeTurquoiseLight,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = YapeTurquoise,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Identidad RENIEC verificada. Ingresa tu clave Yape.",
                color = YapePurpleDark,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
              )
            }
          }
        }

        AnimatedVisibility(visible = uiState.isAccountLocked) {
          Card(
            colors = CardDefaults.cardColors(containerColor = YapeErrorRedLight),
            border = BorderStroke(1.5.dp, YapeErrorRed),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("account_locked_alert"),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.GppBad,
                  contentDescription = "Alerta de seguridad",
                  tint = YapeErrorRed,
                  modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Alerta de seguridad • RF-02",
                  color = YapeErrorRed,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Cuenta bloqueada temporalmente por 3 intentos fallidos. Valida tu identidad biométrica RENIEC",
                color = Color(0xFF7F1D1D),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { showFaceModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = YapeTurquoise),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("reniec_face_unlock_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Face,
                  contentDescription = null,
                  tint = YapeWhite,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Desbloquear con verificación facial RENIEC",
                  color = YapeWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                )
              }
            }
          }
        }

        if (!uiState.isAccountLocked && uiState.pinErrorMessage != null) {
          Surface(
            color = YapeMagenta.copy(alpha = 0.22f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, YapeMagenta.copy(alpha = 0.65f)),
          ) {
            Text(
              text = uiState.pinErrorMessage,
              color = YapeWhite,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              textAlign = TextAlign.Center,
            )
          }
        }
      }

      // Bottom: Randomized 0-9 Numeric Keypad + Discreet Test Note
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
      ) {
        val digits = uiState.keypadDigits
        val rows = listOf(
          digits.subList(0, 3),
          digits.subList(3, 6),
          digits.subList(6, 9),
        )

        Column(
          verticalArrangement = Arrangement.spacedBy(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          rows.forEach { rowDigits ->
            Row(
              horizontalArrangement = Arrangement.spacedBy(20.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              rowDigits.forEach { digit ->
                PinKeypadButton(
                  label = digit.toString(),
                  enabled = !uiState.isAccountLocked,
                  onClick = { onDigitPress(digit) },
                  testTag = "pin_key_$digit",
                )
              }
            }
          }

          // Bottom row: Clear | last digit | Backspace
          Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .clickable(enabled = !uiState.isAccountLocked, onClick = onClearPin)
                .testTag("pin_clear_button"),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "Limpiar",
                color = YapeWhite.copy(alpha = if (uiState.isAccountLocked) 0.3f else 0.85f),
                style = MaterialTheme.typography.labelLarge,
              )
            }

            val lastDigit = digits[9]
            PinKeypadButton(
              label = lastDigit.toString(),
              enabled = !uiState.isAccountLocked,
              onClick = { onDigitPress(lastDigit) },
              testTag = "pin_key_$lastDigit",
            )

            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .clickable(enabled = !uiState.isAccountLocked, onClick = onBackspace)
                .testTag("pin_backspace_button"),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Backspace,
                contentDescription = "Borrar dígito",
                tint = YapeWhite.copy(alpha = if (uiState.isAccountLocked) 0.3f else 0.9f),
                modifier = Modifier.size(26.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Discreet QA test note at bottom
        Surface(
          color = YapeWhite.copy(alpha = 0.09f),
          shape = RoundedCornerShape(12.dp),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Lock,
              contentDescription = null,
              tint = YapeTurquoise,
              modifier = Modifier.size(13.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Nota QA (CP-002): Clave correcta 123456 • 3 errores bloquean",
              color = YapeWhite.copy(alpha = 0.78f),
              fontSize = 11.sp,
            )
          }
        }
      }
    }
  }

  if (showFaceModal) {
    SimulatedFaceVerificationModal(
      title = "Validación Biométrica RENIEC",
      subtitle = "Mira al frente para desbloquear tu cuenta Yape de forma segura",
      onSuccess = {
        showFaceModal = false
        onUnlockWithReniec()
      },
      onDismiss = { showFaceModal = false },
    )
  }
}

@Composable
private fun PinKeypadButton(
  label: String,
  enabled: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    shape = CircleShape,
    color = if (enabled) YapeWhite.copy(alpha = 0.16f) else YapeWhite.copy(alpha = 0.06f),
    border = BorderStroke(
      width = 1.dp,
      color = if (enabled) YapeWhite.copy(alpha = 0.24f) else YapeWhite.copy(alpha = 0.08f),
    ),
    modifier = Modifier
      .size(68.dp)
      .clip(CircleShape)
      .clickable(enabled = enabled, onClick = onClick)
      .testTag(testTag),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        color = if (enabled) YapeWhite else YapeWhite.copy(alpha = 0.35f),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}
