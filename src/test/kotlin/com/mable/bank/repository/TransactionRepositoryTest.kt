package com.mable.bank.repository

import com.mable.bank.entity.TransactionState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class TransactionRepositoryTest(@Autowired private val transactionRepository: TransactionRepository) {
    @Test
    fun findAllByStateOrderByCreatedAtAsc() {
        val transactions = transactionRepository.findAllByStateOrderByCreatedAtAsc(TransactionState.PROCESSED)

        assertThat(transactions)
            .hasSize(4)
            .extracting<Long?> { it.id }
            .containsExactlyElementsOf(listOf(1L, 2L, 3L, 4L))
    }
}
