package com.mable.bank.controller

import com.mable.bank.entity.Account
import com.mable.bank.repository.AccountRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(private val accountRepository: AccountRepository) {
    @GetMapping
    fun index(): ResponseEntity<List<Account>> {
        val accounts = accountRepository.findAll()

        return ResponseEntity.ok(accounts)
    }
}
