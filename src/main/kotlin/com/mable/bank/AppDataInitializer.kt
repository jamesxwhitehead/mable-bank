package com.mable.bank

import com.mable.bank.dto.AccountBalanceDto
import com.mable.bank.entity.Account
import com.mable.bank.repository.AccountRepository
import com.mable.bank.service.TransactionManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

@Component
class AppDataInitializer(
    private val accountRepository: AccountRepository,
    private val transactionManager: TransactionManager,
    private val validator: Validator
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        loadAccountBalances()
        loadTransactions()
    }

    private fun loadAccountBalances() {
        val file = ClassPathResource("mable_account_balances.csv").file

        file.forEachLine {
            val (accountId, balance) = it.split(CSV_DELIMITER)
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
        transactionManager.createPendingTransactionQueue(ClassPathResource("mable_transactions.csv"))
    }

    private companion object {
        const val CSV_DELIMITER = ","
    }
}
