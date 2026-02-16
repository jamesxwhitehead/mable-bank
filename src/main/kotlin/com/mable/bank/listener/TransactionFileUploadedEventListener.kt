package com.mable.bank.listener

import com.mable.bank.entity.Transaction
import com.mable.bank.event.TransactionFileUploadedEvent
import com.mable.bank.repository.AccountRepository
import com.mable.bank.repository.TransactionRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionFileUploadedEventListener(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    @Async
    @EventListener
    @Transactional
    fun onTransactionFileUploadedEvent(event: TransactionFileUploadedEvent) {
        val file = event.path.toFile()

        file.forEachLine {
            val (senderAccountId, receiverAccountId, amount) = it.split(COMMA_DELIMITER)

            val sender = accountRepository.findByAccountId(senderAccountId.toLong())!!
            val receiver = accountRepository.findByAccountId(receiverAccountId.toLong())!!

            val transaction = Transaction(sender, receiver, amount.toBigDecimal())

            transactionRepository.save(transaction)
        }

        transactionRepository.flush()
    }

    companion object {
        const val COMMA_DELIMITER = ","
    }
}
