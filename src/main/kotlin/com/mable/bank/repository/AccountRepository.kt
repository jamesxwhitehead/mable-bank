package com.mable.bank.repository

import com.mable.bank.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun findByAccountId(accountId: Long): Account?

    fun existsByAccountId(accountId: Long): Boolean
}
