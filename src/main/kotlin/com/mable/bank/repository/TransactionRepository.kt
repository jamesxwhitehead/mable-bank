package com.mable.bank.repository

import com.mable.bank.entity.Transaction
import com.mable.bank.entity.TransactionState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByStateOrderByCreatedAtAsc(state: TransactionState): List<Transaction>
}
