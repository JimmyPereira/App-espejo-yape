package com.example.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class UserRole(val title: String, val shortLabel: String) {
  TITULAR("Perfil Titular (Yape con BCP)", "Titular BCP"),
  AYUDANTE("Perfil Ayudante (Yape Empresa - RBAC)", "Ayudante RBAC"),
}

enum class InteropDestination(
  val displayName: String,
  val shortTag: String,
  val subtitle: String,
) {
  YAPE("Yape", "Yape", "Billetera BCP • Inmediato"),
  PLIN("Plin (BBVA / Interbank)", "Plin", "Interoperabilidad BCRP • Sin comisión"),
  CMAC("Cajas Municipales (CMAC Huancayo)", "CMAC", "Red Interoperable BCRP • Cajas"),
}

enum class TransactionType {
  YAPEO_ENVIADO,
  COBRO_RECIBIDO,
  PAGO_SERVICIO,
  DESEMBOLSO_CREDITO,
}

data class YapeContact(
  val id: String,
  val name: String,
  val phone: String,
  val defaultDestination: InteropDestination,
  val initials: String,
)

data class YapeTransaction(
  val id: String,
  val title: String,
  val subtitle: String,
  val amount: Double,
  val isIncome: Boolean,
  val timestampLabel: String,
  val destinationLabel: String,
  val type: TransactionType,
  val securityCode: String,
  val operationNumber: String,
  val message: String = "",
)

data class UtilityCompany(
  val id: String,
  val name: String,
  val category: String,
  val sampleSupplyCode: String,
  val pendingAmount: Double,
  val periodLabel: String,
  val dueDateLabel: String,
)

data class VoucherData(
  val title: String,
  val amount: Double,
  val recipientName: String,
  val recipientDetail: String,
  val dateTimeLabel: String,
  val destinationLabel: String,
  val message: String,
  val securityCode: String,
  val operationNumber: String,
  val isCreditDisbursement: Boolean = false,
)

data class ValidationAlert(
  val code: String,
  val title: String,
  val message: String,
  val canExpandLimitBiometric: Boolean = false,
)

fun formatSoles(amount: Double): String {
  val symbols = DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = ','
    decimalSeparator = '.'
  }
  val formatter = DecimalFormat("#,##0.00", symbols)
  return "S/ ${formatter.format(amount)}"
}

fun formatPhoneSpaced(phone: String): String {
  val clean = phone.filter { it.isDigit() }
  return if (clean.length == 9) {
    "${clean.substring(0, 3)} ${clean.substring(3, 6)} ${clean.substring(6, 9)}"
  } else {
    phone
  }
}
