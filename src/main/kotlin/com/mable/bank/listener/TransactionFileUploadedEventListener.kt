package com.mable.bank.listener

import com.mable.bank.event.TransactionFileUploadedEvent
import com.mable.bank.service.TransactionManager
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class TransactionFileUploadedEventListener(private val transactionManager: TransactionManager) {
    @Async
    @EventListener
    fun onTransactionFileUploadedEvent(event: TransactionFileUploadedEvent) {
        transactionManager.createPendingTransactionQueue(event.path)
    }
}
