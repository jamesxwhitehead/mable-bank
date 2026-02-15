package com.mable.bank.controller

import com.mable.bank.entity.Transaction
import com.mable.bank.repository.TransactionRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionController(private val transactionRepository: TransactionRepository) {
    @GetMapping
    fun index(): ResponseEntity<List<Transaction>> {
        val transactions = transactionRepository.findAll()

        return ResponseEntity.ok(transactions)
    }
}
