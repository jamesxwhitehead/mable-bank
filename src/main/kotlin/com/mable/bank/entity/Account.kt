package com.mable.bank.entity

import com.mable.bank.dto.AccountBalanceDto
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
    var balance: BigDecimal = BigDecimal.ZERO
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null

    companion object {
        fun fromDto(dto: AccountBalanceDto) = Account(dto.accountId.toLong(), dto.balance)
    }
}
