package com.mable.bank

import com.mable.bank.entity.Account
import com.mable.bank.repository.AccountRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AppDataInitializer(
    private val accountRepository: AccountRepository
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments) {
        val file = ClassPathResource("mable_account_balances.csv").file

        file.forEachLine {
            val (accountId, balance) = it.split(COMMA_DELIMITER)

            val account = Account(accountId.toLong(), balance.toBigDecimal())

            accountRepository.save(account)
        }

        accountRepository.flush()
    }

    companion object {
        const val COMMA_DELIMITER = ","
    }
}
