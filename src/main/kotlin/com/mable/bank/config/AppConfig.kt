package com.mable.bank.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableAsync
@EnableScheduling
class AppConfig {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
}
