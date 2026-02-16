package com.mable.bank

import com.mable.bank.dto.AccountBalanceDto
import com.mable.bank.entity.Account
import com.mable.bank.entity.Transaction
import com.mable.bank.entity.TransactionState
import com.mable.bank.repository.AccountRepository
import com.mable.bank.repository.TransactionRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

@Component
class AppDataInitializer(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val validator: Validator
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        loadAccountBalances()
        loadTransactions()
        processTransactions()
    }

    private fun loadAccountBalances() {
        val file = ClassPathResource("mable_account_balances.csv").file

        file.forEachLine {
            val (accountId, balance) = it.split(COMMA_DELIMITER)
            val dto = AccountBalanceDto(accountId, balance.toBigDecimal())

            val violations = validator.validateObject(dto)

            if (violations.hasErrors()) {
                return@forEachLine
            }

            val account = accountRepository.findByAccountId(dto.accountId.toLong())
                ?.apply { this.balance = dto.balance }
                ?: Account.fromDto(dto)

            accountRepository.save(account)
        }

        accountRepository.flush()
    }

    private fun loadTransactions() {
        val file = ClassPathResource("mable_transactions.csv").file

        file.forEachLine {
            val (senderAccountId, receiverAccountId, amount) = it.split(COMMA_DELIMITER)

            val sender = accountRepository.findByAccountId(senderAccountId.toLong())!!
            val receiver = accountRepository.findByAccountId(receiverAccountId.toLong())!!

            val transaction = Transaction(sender, receiver, amount.toBigDecimal())

            transactionRepository.save(transaction)
        }

        transactionRepository.flush()
    }

    private fun processTransactions() {
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

    companion object {
        const val COMMA_DELIMITER = ","
    }
}
