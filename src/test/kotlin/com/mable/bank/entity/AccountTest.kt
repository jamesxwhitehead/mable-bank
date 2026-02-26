package com.mable.bank.entity

import com.mable.bank.exception.InsufficientFundsException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AccountTest {
    @Test
    fun withdraw() {
        val account = Account(1000000000000001, 5000.toBigDecimal())

        account.withdraw(1000.toBigDecimal())

        assertEquals(4000.toBigDecimal(), account.balance)
    }

    @Test
    fun withdrawShouldThrowInsufficientFundsExceptionWhenBalanceIsInsufficient() {
        val account = Account(1000000000000001, 5000.toBigDecimal())

        assertThrows<InsufficientFundsException> { account.withdraw(6000.toBigDecimal()) }
    }

    @Test
    fun deposit() {
        val account = Account(1000000000000001, 5000.toBigDecimal())

        account.deposit(1000.toBigDecimal())

        assertEquals(6000.toBigDecimal(), account.balance)
    }
}
