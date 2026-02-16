package com.mable.bank.controller

import com.mable.bank.entity.Transaction
import com.mable.bank.event.TransactionFileUploadedEvent
import com.mable.bank.repository.TransactionRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionRepository: TransactionRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    @GetMapping
    fun index(): ResponseEntity<List<Transaction>> {
        val transactions = transactionRepository.findAll()

        return ResponseEntity.ok(transactions)
    }

    @PostMapping("/upload", consumes = ["multipart/form-data"])
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<Unit> {
        val uploadDir = Paths.get("src/main/resources/static/upload")
        val path = uploadDir.resolve(file.originalFilename!!)

        file.inputStream.use {
            Files.copy(it, path)
        }

        val event = TransactionFileUploadedEvent(path)
        eventPublisher.publishEvent(event)

        return ResponseEntity.accepted().build()
    }
}
