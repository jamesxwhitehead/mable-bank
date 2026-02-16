package com.mable.bank.service

import com.mable.bank.entity.Transaction
import com.mable.bank.repository.AccountRepository
import com.mable.bank.repository.TransactionRepository
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.file.Path

@Service
class TransactionManagerImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : TransactionManager {
    @Transactional
    override fun createPendingTransactionQueue(resource: Resource) {
        createPendingTransactionQueueFromFile(resource.file)
    }

    @Transactional
    override fun createPendingTransactionQueue(path: Path) {
        createPendingTransactionQueueFromFile(path.toFile())
    }

    private fun createPendingTransactionQueueFromFile(file: File) {
        file.forEachLine {
            val (senderAccountId, receiverAccountId, amount) = it.split(CSV_DELIMITER, limit = 3)

            val sender = accountRepository.findByAccountId(senderAccountId.toLong())!!
            val receiver = accountRepository.findByAccountId(receiverAccountId.toLong())!!

            val transaction = Transaction(sender, receiver, amount.toBigDecimal())

            transactionRepository.save(transaction)
        }

        transactionRepository.flush()
    }

    private companion object {
        const val CSV_DELIMITER = ","
    }
}
