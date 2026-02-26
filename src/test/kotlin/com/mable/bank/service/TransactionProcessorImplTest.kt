package com.mable.bank.service

import com.example.demo.entity.AccountFixture
import com.mable.bank.entity.Reason
import com.mable.bank.entity.Transaction
import com.mable.bank.entity.TransactionState
import com.mable.bank.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class TransactionProcessorImplTest {
    private val transactionRepository = mock<TransactionRepository>()
    private val transactionProcessor = TransactionProcessorImpl(transactionRepository)

    @Test
    fun processPendingTransactionQueue() {
        val transaction1 = Transaction(
            sender = AccountFixture.make(balance = BigDecimal(1000)),
            receiver = AccountFixture.make(balance = BigDecimal(1000)),
            amount = BigDecimal(500)
        )
        val transaction2 = Transaction(
            sender = AccountFixture.make(balance = BigDecimal(1000)),
            receiver = AccountFixture.make(balance = BigDecimal(1000)),
            amount = BigDecimal(1500)
        )
        val transactions = listOf(transaction1, transaction2)
        given(transactionRepository.findAllByStateOrderByCreatedAtAsc(TransactionState.PENDING)).willReturn(transactions)

        transactionProcessor.onApplicationReady()
        transactionProcessor.processPendingTransactionQueue()

        then(transactionRepository).should().save(transaction1)
        then(transactionRepository).should().save(transaction2)
        then(transactionRepository).should().flush()

        assertThat(transaction1.state).isEqualTo(TransactionState.SETTLED)
        assertThat(transaction1.reason).isEqualTo(Reason.APPROVED)
        assertThat(transaction2.state).isEqualTo(TransactionState.DISHONOURED)
        assertThat(transaction2.reason).isEqualTo(Reason.INSUFFICIENT_FUNDS)
    }

    @Test
    fun processPendingTransactionQueueShouldDoNothingWhenApplicationNotReady() {
        transactionProcessor.processPendingTransactionQueue()

        then(transactionRepository).shouldHaveNoInteractions()
    }

    @Test
    fun processPendingTransactionQueueShouldDoNothingWhenQueueIsEmpty() {
        given(transactionRepository.findAllByStateOrderByCreatedAtAsc(TransactionState.PENDING)).willReturn(emptyList())

        transactionProcessor.onApplicationReady()
        transactionProcessor.processPendingTransactionQueue()

        then(transactionRepository).should(never()).flush()
    }
}
