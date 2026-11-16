package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.InteropDestination
import com.example.model.TransactionType
import com.example.model.UserRole
import com.example.model.UtilityCompany
import com.example.model.ValidationAlert
import com.example.model.VoucherData
import com.example.model.YapeContact
import com.example.model.YapeTransaction
import com.example.model.formatPhoneSpaced
import com.example.model.formatSoles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

data class YapeUiState(
  val isAuthenticated: Boolean = false,
  val pinInput: String = "",
  val failedPinAttempts: Int = 0,
  val isAccountLocked: Boolean = false,
  val pinErrorMessage: String? = null,
  val reniecUnlockedNotice: Boolean = false,
  val keypadDigits: List<Int> = (0..9).shuffled(),
  val userRole: UserRole = UserRole.TITULAR,
  val isBalanceVisible: Boolean = false,
  val balance: Double = 2450.00,
  val dailySentAmount: Double = 0.00,
  val dailyLimit: Double = 2000.00,
  val biometricLimitExpanded: Boolean = false,
  val transactions: List<YapeTransaction> = defaultTransactions(),
  val contacts: List<YapeContact> = defaultContacts(),
  val utilityCompanies: List<UtilityCompany> = defaultUtilityCompanies(),
  val activeVoucher: VoucherData? = null,
  val validationAlert: ValidationAlert? = null,
  val prefillPhone: String? = null,
  val prefillName: String? = null,
  val prefillDestination: InteropDestination? = null,
  val prefillAmount: String? = null,
  val unreadNotifications: Int = 2,
) {
  val effectiveDailyLimit: Double
    get() = if (biometricLimitExpanded) 3500.00 else dailyLimit
}

class YapeViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(YapeUiState())
  val uiState: StateFlow<YapeUiState> = _uiState.asStateFlow()

  companion object {
    const val CORRECT_PIN = "123456"
    const val MIN_YAPEO_AMOUNT = 0.10
    const val MAX_YAPEO_PER_OP = 500.00
    const val MAX_DAILY_STANDARD = 2000.00
    const val MAX_CREDIT_PREAPPROVED = 500.00
  }

  fun onPinDigitPressed(digit: Int) {
    val current = _uiState.value
    if (current.isAccountLocked || current.pinInput.length >= 6) return

    val updatedPin = current.pinInput + digit.toString()
    _uiState.update {
      it.copy(
        pinInput = updatedPin,
        pinErrorMessage = null,
        reniecUnlockedNotice = false,
      )
    }

    if (updatedPin.length == 6) {
      validatePin(updatedPin)
    }
  }

  fun onPinBackspace() {
    val current = _uiState.value
    if (current.isAccountLocked || current.pinInput.isEmpty()) return
    _uiState.update {
      it.copy(
        pinInput = it.pinInput.dropLast(1),
        pinErrorMessage = null,
      )
    }
  }

  fun onPinClear() {
    if (_uiState.value.isAccountLocked) return
    _uiState.update {
      it.copy(pinInput = "", pinErrorMessage = null)
    }
  }

  fun reshuffleKeypad() {
    _uiState.update { it.copy(keypadDigits = (0..9).shuffled()) }
  }

  private fun validatePin(pin: String) {
    if (pin == CORRECT_PIN) {
      _uiState.update {
        it.copy(
          isAuthenticated = true,
          pinInput = "",
          failedPinAttempts = 0,
          isAccountLocked = false,
          pinErrorMessage = null,
          reniecUnlockedNotice = false,
        )
      }
    } else {
      val attempts = _uiState.value.failedPinAttempts + 1
      val locked = attempts >= 3
      _uiState.update {
        it.copy(
          pinInput = "",
          failedPinAttempts = attempts,
          isAccountLocked = locked,
          keypadDigits = (0..9).shuffled(),
          pinErrorMessage = if (locked) {
            "Cuenta bloqueada temporalmente por 3 intentos fallidos. Valida tu identidad biométrica RENIEC"
          } else {
            "Clave incorrecta. Intento $attempts de 3"
          },
        )
      }
    }
  }

  fun unlockWithReniecBiometric() {
    _uiState.update {
      it.copy(
        isAccountLocked = false,
        failedPinAttempts = 0,
        pinInput = "",
        pinErrorMessage = null,
        reniecUnlockedNotice = true,
        keypadDigits = (0..9).shuffled(),
      )
    }
  }

  fun logout() {
    _uiState.update {
      it.copy(
        isAuthenticated = false,
        pinInput = "",
        pinErrorMessage = null,
        reniecUnlockedNotice = false,
        keypadDigits = (0..9).shuffled(),
      )
    }
  }

  fun setUserRole(role: UserRole) {
    _uiState.update {
      it.copy(
        userRole = role,
        isBalanceVisible = if (role == UserRole.AYUDANTE) false else it.isBalanceVisible,
      )
    }
  }

  fun toggleBalanceVisibility() {
    if (_uiState.value.userRole == UserRole.AYUDANTE) return
    _uiState.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
  }

  fun markNotificationsRead() {
    _uiState.update { it.copy(unreadNotifications = 0) }
  }

  fun setQrPrefill(phone: String, name: String, destination: InteropDestination, amount: String) {
    _uiState.update {
      it.copy(
        prefillPhone = phone,
        prefillName = name,
        prefillDestination = destination,
        prefillAmount = amount,
      )
    }
  }

  fun clearQrPrefill() {
    _uiState.update {
      it.copy(
        prefillPhone = null,
        prefillName = null,
        prefillDestination = null,
        prefillAmount = null,
      )
    }
  }

  fun resolveTitularNameForPhone(phone: String, destination: InteropDestination): String? {
    val clean = phone.filter { it.isDigit() }
    if (clean.length != 9 || !clean.startsWith("9")) return null
    val contactMatch = _uiState.value.contacts.firstOrNull { it.phone == clean }
    if (contactMatch != null) return contactMatch.name

    return when (clean) {
      "987654321" -> "Lucía Paredes Quispe"
      "991234567" -> "Mateo Rojas Fernández"
      "945678901" -> "Rosa Chávez Huamán"
      "912345678" -> "Diego Salazar Torres"
      "999888777" -> "Bodega La Esquina E.I.R.L."
      "988776655" -> "Café Lima Central S.A.C."
      else -> {
        val sampleNames = listOf(
          "Andrea Morales Castillo",
          "Jorge Luis Quispe Vargas",
          "Valeria Mendoza Ríos",
          "Sebastián Flores Aguilar",
          "Camila Gutiérrez León",
        )
        val idx = (clean.takeLast(2).toIntOrNull() ?: 0) % sampleNames.size
        val suffix = when (destination) {
          InteropDestination.YAPE -> ""
          InteropDestination.PLIN -> " (Verificado Plin)"
          InteropDestination.CMAC -> " (Verificado CMAC)"
        }
        sampleNames[idx] + suffix
      }
    }
  }

  fun attemptYapeo(
    recipientName: String,
    phone: String,
    destination: InteropDestination,
    amount: Double,
    message: String,
  ): Boolean {
    val state = _uiState.value
    val roundedAmount = (amount * 100.0).roundToInt() / 100.0

    if (roundedAmount < MIN_YAPEO_AMOUNT) {
      _uiState.update {
        it.copy(
          validationAlert = ValidationAlert(
            code = "RF-03",
            title = "Monto mínimo no permitido",
            message = "El monto mínimo permitido por operación es S/ 0.10 (RF-03).",
          )
        )
      }
      return false
    }

    if (roundedAmount > MAX_YAPEO_PER_OP) {
      _uiState.update {
        it.copy(
          validationAlert = ValidationAlert(
            code = "RF-06",
            title = "Límite por operación excedido",
            message = "Supera el límite máximo estándar de S/ 500.00 por yapeo (RF-06).",
          )
        )
      }
      return false
    }

    val projectedDaily = ((state.dailySentAmount + roundedAmount) * 100.0).roundToInt() / 100.0
    if (projectedDaily > state.effectiveDailyLimit) {
      _uiState.update {
        it.copy(
          validationAlert = ValidationAlert(
            code = "RF-06",
            title = "Tope diario superado",
            message = "Superaste tu tope acumulado diario de S/ 2,000.00. Amplía tu límite con validación biométrica (RF-06).",
            canExpandLimitBiometric = !state.biometricLimitExpanded,
          )
        )
      }
      return false
    }

    if (roundedAmount > state.balance) {
      _uiState.update {
        it.copy(
          validationAlert = ValidationAlert(
            code = "SALDO",
            title = "Saldo insuficiente",
            message = "Tu saldo disponible (${formatSoles(state.balance)}) no cubre este yapeo.",
          )
        )
      }
      return false
    }

    val secCode = generateSecurityCode()
    val opNumber = generateOperationNumber()
    val nowLabel = currentFormattedDateTime()
    val shortTime = currentShortTime()

    val newTx = YapeTransaction(
      id = "tx_${System.currentTimeMillis()}",
      title = recipientName,
      subtitle = "Yapeo enviado • ${destination.shortTag}",
      amount = roundedAmount,
      isIncome = false,
      timestampLabel = "Hoy, $shortTime",
      destinationLabel = destination.displayName,
      type = TransactionType.YAPEO_ENVIADO,
      securityCode = secCode,
      operationNumber = opNumber,
      message = message.trim(),
    )

    val voucher = VoucherData(
      title = "¡Yapeaste!",
      amount = roundedAmount,
      recipientName = recipientName,
      recipientDetail = formatPhoneSpaced(phone),
      dateTimeLabel = nowLabel,
      destinationLabel = destination.displayName,
      message = message.trim(),
      securityCode = secCode,
      operationNumber = opNumber,
    )

    _uiState.update {
      it.copy(
        balance = ((it.balance - roundedAmount) * 100.0).roundToInt() / 100.0,
        dailySentAmount = projectedDaily,
        transactions = listOf(newTx) + it.transactions,
        activeVoucher = voucher,
        prefillPhone = null,
        prefillName = null,
        prefillDestination = null,
        prefillAmount = null,
      )
    }
    return true
  }

  fun payUtilityService(company: UtilityCompany, supplyCode: String): Boolean {
    val state = _uiState.value
    val amount = company.pendingAmount
    if (amount > state.balance) {
      _uiState.update {
        it.copy(
          validationAlert = ValidationAlert(
            code = "SALDO",
            title = "Saldo insuficiente",
            message = "No tienes saldo suficiente para pagar ${formatSoles(amount)} a ${company.name}.",
          )
        )
      }
      return false
    }

    val secCode = generateSecurityCode()
    val opNumber = generateOperationNumber()
    val nowLabel = currentFormattedDateTime()
    val shortTime = currentShortTime()

    val newTx = YapeTransaction(
      id = "srv_${System.currentTimeMillis()}",
      title = company.name,
      subtitle = "Pago de servicio • Suministro $supplyCode",
      amount = amount,
      isIncome = false,
      timestampLabel = "Hoy, $shortTime",
      destinationLabel = "Yapear Servicios",
      type = TransactionType.PAGO_SERVICIO,
      securityCode = secCode,
      operationNumber = opNumber,
      message = "Recibo ${company.periodLabel}",
    )

    val voucher = VoucherData(
      title = "¡Pagaste tu servicio!",
      amount = amount,
      recipientName = company.name,
      recipientDetail = "Suministro N° $supplyCode",
      dateTimeLabel = nowLabel,
      destinationLabel = "Yapear Servicios",
      message = "Periodo ${company.periodLabel}",
      securityCode = secCode,
      operationNumber = opNumber,
    )

    _uiState.update {
      it.copy(
        balance = ((it.balance - amount) * 100.0).roundToInt() / 100.0,
        transactions = listOf(newTx) + it.transactions,
        activeVoucher = voucher,
      )
    }
    return true
  }

  fun disburseCredit(amount: Double): VoucherData {
    val roundedAmount = (amount * 100.0).roundToInt() / 100.0
    val secCode = generateSecurityCode()
    val opNumber = generateOperationNumber()
    val nowLabel = currentFormattedDateTime()
    val shortTime = currentShortTime()

    val newTx = YapeTransaction(
      id = "cred_${System.currentTimeMillis()}",
      title = "Crédito Yape BCP",
      subtitle = "Desembolso instantáneo • 1 cuota (30 días)",
      amount = roundedAmount,
      isIncome = true,
      timestampLabel = "Hoy, $shortTime",
      destinationLabel = "Créditos Yape",
      type = TransactionType.DESEMBOLSO_CREDITO,
      securityCode = secCode,
      operationNumber = opNumber,
      message = "Abono directo a tu cuenta Yape",
    )

    val voucher = VoucherData(
      title = "¡Crédito desembolsado!",
      amount = roundedAmount,
      recipientName = "Jimmy Pereira",
      recipientDetail = "Abono directo a Yape BCP",
      dateTimeLabel = nowLabel,
      destinationLabel = "Créditos Yape",
      message = "1 cuota a 30 días • TCEA 108.45%",
      securityCode = secCode,
      operationNumber = opNumber,
      isCreditDisbursement = true,
    )

    _uiState.update {
      it.copy(
        balance = ((it.balance + roundedAmount) * 100.0).roundToInt() / 100.0,
        transactions = listOf(newTx) + it.transactions,
        activeVoucher = voucher,
      )
    }
    return voucher
  }

  fun expandDailyLimitWithBiometric() {
    _uiState.update {
      it.copy(
        biometricLimitExpanded = true,
        validationAlert = null,
      )
    }
  }

  fun dismissValidationAlert() {
    _uiState.update { it.copy(validationAlert = null) }
  }

  fun generateApprovalCode(): String = Random.nextInt(100000, 999999).toString()

  private fun generateSecurityCode(): String = Random.nextInt(100, 1000).toString()

  private fun generateOperationNumber(): String = Random.nextInt(10000000, 99999999).toString()

  private fun currentFormattedDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale("es", "PE"))
    return sdf.format(Date())
  }

  private fun currentShortTime(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale("es", "PE"))
    return sdf.format(Date())
  }
}

private fun defaultContacts(): List<YapeContact> = listOf(
  YapeContact(
    id = "c1",
    name = "Lucía Paredes Quispe",
    phone = "987654321",
    defaultDestination = InteropDestination.YAPE,
    initials = "LP",
  ),
  YapeContact(
    id = "c2",
    name = "Mateo Rojas Fernández",
    phone = "991234567",
    defaultDestination = InteropDestination.PLIN,
    initials = "MR",
  ),
  YapeContact(
    id = "c3",
    name = "Rosa Chávez Huamán",
    phone = "945678901",
    defaultDestination = InteropDestination.CMAC,
    initials = "RC",
  ),
  YapeContact(
    id = "c4",
    name = "Diego Salazar Torres",
    phone = "912345678",
    defaultDestination = InteropDestination.YAPE,
    initials = "DS",
  ),
)

private fun defaultTransactions(): List<YapeTransaction> = listOf(
  YapeTransaction(
    id = "tx_init_1",
    title = "María Fernanda Quispe",
    subtitle = "Te yapeó • Venta del día",
    amount = 145.00,
    isIncome = true,
    timestampLabel = "Hoy, 10:18 a. m.",
    destinationLabel = "Yape",
    type = TransactionType.COBRO_RECIBIDO,
    securityCode = "482",
    operationNumber = "84920144",
    message = "Pedido tienda #14",
  ),
  YapeTransaction(
    id = "tx_init_2",
    title = "Bodega Don Pepe",
    subtitle = "Yapeo enviado • Yape",
    amount = 35.50,
    isIncome = false,
    timestampLabel = "Ayer, 07:42 p. m.",
    destinationLabel = "Yape",
    type = TransactionType.YAPEO_ENVIADO,
    securityCode = "719",
    operationNumber = "73104982",
    message = "Compras",
  ),
  YapeTransaction(
    id = "tx_init_3",
    title = "Carlos Mendoza Ríos",
    subtitle = "Cobro recibido • Plin",
    amount = 82.00,
    isIncome = true,
    timestampLabel = "Ayer, 03:15 p. m.",
    destinationLabel = "Plin (BBVA / Interbank)",
    type = TransactionType.COBRO_RECIBIDO,
    securityCode = "905",
    operationNumber = "62019485",
    message = "Almuerzo",
  ),
)

private fun defaultUtilityCompanies(): List<UtilityCompany> = listOf(
  UtilityCompany(
    id = "electrocentro",
    name = "Electrocentro",
    category = "Energía eléctrica",
    sampleSupplyCode = "7482910",
    pendingAmount = 84.20,
    periodLabel = "Noviembre 2026",
    dueDateLabel = "25 nov. 2026",
  ),
  UtilityCompany(
    id = "luz_del_sur",
    name = "Luz del Sur",
    category = "Energía eléctrica",
    sampleSupplyCode = "7482910",
    pendingAmount = 84.20,
    periodLabel = "Noviembre 2026",
    dueDateLabel = "22 nov. 2026",
  ),
  UtilityCompany(
    id = "sedapal",
    name = "Sedapal",
    category = "Agua potable",
    sampleSupplyCode = "7482910",
    pendingAmount = 84.20,
    periodLabel = "Noviembre 2026",
    dueDateLabel = "28 nov. 2026",
  ),
  UtilityCompany(
    id = "movistar",
    name = "Movistar",
    category = "Telefonía e Internet",
    sampleSupplyCode = "7482910",
    pendingAmount = 84.20,
    periodLabel = "Noviembre 2026",
    dueDateLabel = "20 nov. 2026",
  ),
)
