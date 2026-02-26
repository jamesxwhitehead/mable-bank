package com.mable.bank.entity

import com.mable.bank.dto.AccountBalanceDto
import com.mable.bank.exception.InsufficientFundsException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity
class Account(
    @Column(unique = true, nullable = false)
    val accountId: Long,

    @Column(nullable = false)
    var balance: BigDecimal = BigDecimal.ZERO.setScale(2)
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null

    @Throws(InsufficientFundsException::class)
    fun withdraw(amount: BigDecimal) {
        if (balance < amount) {
            throw InsufficientFundsException("Insufficient funds: account $accountId has balance $balance, attempted withdrawal of $amount.")
        }

        balance -= amount
    }

    fun deposit(amount: BigDecimal) {
        balance += amount
    }

    companion object {
        fun fromDto(dto: AccountBalanceDto) = Account(dto.accountId.toLong(), dto.balance)
    }
}
