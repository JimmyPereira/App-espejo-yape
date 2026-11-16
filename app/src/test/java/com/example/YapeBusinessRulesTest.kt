package com.example

import com.example.model.InteropDestination
import com.example.model.UserRole
import com.example.viewmodel.YapeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YapeBusinessRulesTest {

  private fun enterPin(vm: YapeViewModel, pin: String) {
    pin.forEach { ch -> vm.onPinDigitPressed(ch.digitToInt()) }
  }

  @Test
  fun cp002_threeWrongPinsLockAccountAndReniecUnlocks() {
    val vm = YapeViewModel()
    enterPin(vm, "000000")
    assertFalse(vm.uiState.value.isAuthenticated)
    assertEquals(1, vm.uiState.value.failedPinAttempts)
    assertFalse(vm.uiState.value.isAccountLocked)

    enterPin(vm, "111111")
    assertEquals(2, vm.uiState.value.failedPinAttempts)
    assertFalse(vm.uiState.value.isAccountLocked)

    enterPin(vm, "999999")
    assertEquals(3, vm.uiState.value.failedPinAttempts)
    assertTrue(vm.uiState.value.isAccountLocked)

    vm.unlockWithReniecBiometric()
    assertFalse(vm.uiState.value.isAccountLocked)
    assertEquals(0, vm.uiState.value.failedPinAttempts)

    enterPin(vm, "123456")
    assertTrue(vm.uiState.value.isAuthenticated)
  }

  @Test
  fun cp003_to_cp006_yapeoValidationsAndDailyLimit() {
    val vm = YapeViewModel()
    enterPin(vm, "123456")

    // RF-03: < 0.10 rejected
    val belowMin = vm.attemptYapeo("Lucía", "987654321", InteropDestination.YAPE, 0.09, "")
    assertFalse(belowMin)
    assertEquals("RF-03", vm.uiState.value.validationAlert?.code)
    vm.dismissValidationAlert()

    // RF-06: > 500.00 rejected
    val aboveMax = vm.attemptYapeo("Lucía", "987654321", InteropDestination.PLIN, 500.01, "")
    assertFalse(aboveMax)
    assertEquals("RF-06", vm.uiState.value.validationAlert?.code)
    vm.dismissValidationAlert()

    // 4 x 500.00 = 2000.00 daily accumulated
    repeat(4) {
      assertTrue(vm.attemptYapeo("Lucía", "987654321", InteropDestination.YAPE, 500.00, "Prueba"))
    }
    assertEquals(2000.00, vm.uiState.value.dailySentAmount, 0.001)
    assertEquals(450.00, vm.uiState.value.balance, 0.001)

    // 5th yapeo exceeds daily limit 2,000.00 -> RF-06 daily alert
    val exceedsDaily = vm.attemptYapeo("Mateo", "991234567", InteropDestination.PLIN, 0.10, "")
    assertFalse(exceedsDaily)
    assertNotNull(vm.uiState.value.validationAlert)
    assertTrue(vm.uiState.value.validationAlert!!.message.contains("2,000.00"))
  }

  @Test
  fun cp008_cp009_cp010_servicesCreditAndRbac() {
    val vm = YapeViewModel()
    enterPin(vm, "123456")

    val company = vm.uiState.value.utilityCompanies.first { it.name == "Electrocentro" }
    assertTrue(vm.payUtilityService(company, "7482910"))
    assertEquals(2450.00 - 84.20, vm.uiState.value.balance, 0.001)
    assertEquals("¡Pagaste tu servicio!", vm.uiState.value.activeVoucher?.title)

    vm.disburseCredit(300.00)
    assertEquals(2450.00 - 84.20 + 300.00, vm.uiState.value.balance, 0.001)
    assertEquals("¡Crédito desembolsado!", vm.uiState.value.activeVoucher?.title)

    vm.setUserRole(UserRole.AYUDANTE)
    assertFalse(vm.uiState.value.isBalanceVisible)
    vm.toggleBalanceVisibility()
    assertFalse(vm.uiState.value.isBalanceVisible)

    assertNull(vm.resolveTitularNameForPhone("812345678", InteropDestination.YAPE))
    assertNotNull(vm.resolveTitularNameForPhone("912345678", InteropDestination.CMAC))
  }
}
