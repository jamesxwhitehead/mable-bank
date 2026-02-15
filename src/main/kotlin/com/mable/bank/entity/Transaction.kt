package com.mable.bank.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

@Entity
class Transaction(
    @ManyToOne(optional = false)
    val sender: Account,

    @ManyToOne(optional = false)
    val receiver: Account,

    @Column(nullable = false)
    val amount: BigDecimal
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var state: TransactionState = TransactionState.PENDING
}
