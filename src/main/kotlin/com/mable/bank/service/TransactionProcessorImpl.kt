package com.mable.bank.service

import com.mable.bank.entity.TransactionState
import com.mable.bank.repository.TransactionRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@Service
@OptIn(ExperimentalAtomicApi::class)
class TransactionProcessorImpl(private val transactionRepository: TransactionRepository) : TransactionProcessor {
    private val appReady = AtomicBoolean(false)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        appReady.store(true)
    }

    @Scheduled(
        fixedDelay = 60,
        initialDelay = 60,
        timeUnit = TimeUnit.SECONDS
    )
    @Transactional
    override fun processPendingTransactions() {
        if (!appReady.load()) return

        val transactions = transactionRepository.findAllByStateOrderByCreatedAtAsc(TransactionState.PENDING)

        for (transaction in transactions) {
            val sender = transaction.sender
            val receiver = transaction.receiver

            try {
                sender.withdraw(transaction.amount)
                receiver.deposit(transaction.amount)

                transaction.state = TransactionState.PROCESSED
            } catch (_: IllegalStateException) {
                transaction.state = TransactionState.DISHONOURED
            }

            transactionRepository.save(transaction)
        }

        transactionRepository.flush()
    }
}
