package com.mable.bank.exception

class InsufficientFundsException : IllegalStateException {
    constructor(message: String) : super(message)
}
