package com.mable.bank

import com.mable.bank.entity.Account
import com.mable.bank.entity.Transaction
import com.mable.bank.repository.AccountRepository
import com.mable.bank.repository.TransactionRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AppDataInitializer(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        loadAccountBalances()
        loadTransactions()
    }

    private fun loadAccountBalances() {
        val file = ClassPathResource("mable_account_balances.csv").file

        file.forEachLine {
            val (accountId, balance) = it.split(COMMA_DELIMITER)

            val account = Account(accountId.toLong(), balance.toBigDecimal())

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

    companion object {
        const val COMMA_DELIMITER = ","
    }
}
