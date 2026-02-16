package com.mable.bank.controller

import com.mable.bank.dto.AccountBalanceDto
import com.mable.bank.entity.Account
import com.mable.bank.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountRepository: AccountRepository
) : AbstractController() {
    @GetMapping
    fun index(): ResponseEntity<List<Account>> {
        val accounts = accountRepository.findAll()

        return ResponseEntity.ok(accounts)
    }

    @PostMapping
    fun store(@Validated @RequestBody request: AccountBalanceDto): ResponseEntity<Account> {
        if (accountRepository.existsByAccountId(request.accountId.toLong())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        val account = Account.fromDto(request)
        accountRepository.save(account)

        val location = buildLocationHeader(account.id!!)

        return ResponseEntity.created(location).body(account)
    }
}
