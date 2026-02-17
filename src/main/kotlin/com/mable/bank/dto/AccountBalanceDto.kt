package com.mable.bank.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class AccountBalanceDto(
    @Pattern(regexp = "^[1-9]\\d{15}$", message = "Account ID must be a 16-digit number not starting with 0.")
    val accountId: String,

    @PositiveOrZero
    val balance: BigDecimal = BigDecimal.ZERO
)
