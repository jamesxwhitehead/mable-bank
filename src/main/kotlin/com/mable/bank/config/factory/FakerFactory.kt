package com.mable.bank.config.factory

import net.datafaker.Faker
import java.util.Locale
import java.util.Random

class FakerFactory {
    companion object {
        private const val LOCALE_LANGUAGE = "en"
        private const val LOCALE_COUNTRY = "AU"
        private const val SEED = 1L

        @JvmStatic
        fun create(): Faker = Faker(
            Locale.of(LOCALE_LANGUAGE, LOCALE_COUNTRY),
            Random(SEED)
        )
    }
}
