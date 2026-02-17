package com.mable.bank.service

import com.mable.bank.entity.Transaction
import com.mable.bank.repository.AccountRepository
import com.mable.bank.repository.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.math.BigDecimal
import java.nio.file.Path

@Service
class TransactionManagerImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : TransactionManager {
    @Transactional
    override fun createPendingTransactionQueue(resource: Resource) {
        createPendingTransactionQueue(resource.file)
    }

    @Transactional
    override fun createPendingTransactionQueue(path: Path) {
        createPendingTransactionQueue(path.toFile())
    }

    private fun createPendingTransactionQueue(file: File) {
        file.forEachLine { line ->
            if (line.isBlank()) return@forEachLine

            val transaction = parseTransactionOrNull(line) ?: return@forEachLine

            transactionRepository.save(transaction)
        }

        transactionRepository.flush()
    }

    private fun parseTransactionOrNull(line: String): Transaction? {
        return runCatching {
            val parts = line.split(CSV_DELIMITER, limit = 3)

            if (parts.size != 3) return null

            val senderAccountId = parts[0].trim().toLong()
            val receiverAccountId = parts[1].trim().toLong()
            val amount = parts[2].trim().toBigDecimal()

            require(senderAccountId != receiverAccountId) { "Sender and receiver accounts cannot be the same." }
            require(amount > BigDecimal.ZERO) { "Amount must be positive." }

            val sender = accountRepository.findByAccountId(senderAccountId) ?: return null
            val receiver = accountRepository.findByAccountId(receiverAccountId) ?: return null

            Transaction(sender, receiver, amount)
        }.getOrElse { exception ->
            logger.warn("Skipping malformed line: {}", line, exception)
            null
        }
    }

    companion object {
        private const val CSV_DELIMITER = ","
        private val logger = LoggerFactory.getLogger(TransactionManagerImpl::class.java)
    }
}
