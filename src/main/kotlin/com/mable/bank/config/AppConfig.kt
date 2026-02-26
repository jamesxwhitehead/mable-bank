package com.mable.bank.config

import com.mable.bank.config.factory.FakerFactory
import net.datafaker.Faker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableAsync
@EnableScheduling
class AppConfig {
    @Bean
    fun faker(): Faker = FakerFactory.create()
}
