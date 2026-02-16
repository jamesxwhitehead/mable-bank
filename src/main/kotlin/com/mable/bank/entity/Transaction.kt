package com.mable.bank.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant

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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var state: TransactionState = TransactionState.PENDING
}
