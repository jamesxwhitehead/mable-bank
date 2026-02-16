package com.mable.bank.controller

import com.mable.bank.dto.AccountBalanceDto
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    @Test
    fun index() {
        mockMvc.get("/accounts")
            .andExpectAll {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(5) }
            }
    }

    @Test
    fun store() {
        val dto = AccountBalanceDto("1000000000000001")

        mockMvc.post("/accounts") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpectAll {
            status { isCreated() }
            header { string("Location", containsString("/accounts/")) }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.accountId") { value(dto.accountId) }
            jsonPath("$.balance") { value(dto.balance) }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "0", "1", "-1", "accountId", "1000000accountId", "0000000000000001", "100000000000001", "10000000000000001"])
    fun storeShouldReturnBadRequestWhenAccountIdIsInvalid(accountId: String) {
        val dto = AccountBalanceDto(accountId)

        mockMvc.post("/accounts") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpectAll {
            status { isBadRequest() }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1111234522226789", "1111234522221234", "2222123433331212", "1212343433335665", "3212343433335755"])
    fun storeShouldReturnConflictWhenAccountExists(accountId: String) {
        val dto = AccountBalanceDto(accountId)

        mockMvc.post("/accounts") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpectAll {
            status { isConflict() }
        }
    }
}
