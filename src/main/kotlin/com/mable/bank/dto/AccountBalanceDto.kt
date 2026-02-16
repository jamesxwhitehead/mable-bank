package com.mable.bank.dto

import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class AccountBalanceDto(
    @Digits(integer = 16, fraction = 0)
    @Min(1000000000000000L)
    @Size(min = 16, max = 16)
    val accountId: String,

    @PositiveOrZero
    val balance: BigDecimal = BigDecimal.ZERO
)
