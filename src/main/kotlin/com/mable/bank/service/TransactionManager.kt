package com.mable.bank.service

import org.springframework.core.io.Resource
import java.nio.file.Path

interface TransactionManager {
    fun createPendingTransactionQueue(resource: Resource)

    fun createPendingTransactionQueue(path: Path)
}
