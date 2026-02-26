package com.example.demo.entity

import com.mable.bank.entity.Account
import java.math.BigDecimal

object AccountFixture {
    private val accountIdSequence = generateSequence(1000000000000000) { it + 1 }.iterator()

    fun make(
        accountId: Long = accountIdSequence.next(),
        balance: BigDecimal = BigDecimal.ZERO
    ) = Account(accountId = accountId, balance = balance)
}
