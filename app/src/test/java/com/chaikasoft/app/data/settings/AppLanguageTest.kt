package com.chaikasoft.app.data.settings

import com.chaikasoft.app.domain.models.settings.AppLanguage
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AppLanguageTest {

    @Test
    fun `fromLanguageTag maps supported tags and falls back to system`() {
        AppLanguage.fromLanguageTag(null) shouldBe AppLanguage.SYSTEM
        AppLanguage.fromLanguageTag("") shouldBe AppLanguage.SYSTEM
        AppLanguage.fromLanguageTag("ru") shouldBe AppLanguage.RU
        AppLanguage.fromLanguageTag("ru-RU") shouldBe AppLanguage.RU
        AppLanguage.fromLanguageTag("en") shouldBe AppLanguage.EN
        AppLanguage.fromLanguageTag("en-US") shouldBe AppLanguage.EN
        AppLanguage.fromLanguageTag("fr") shouldBe AppLanguage.SYSTEM
    }

    @Test
    fun `toLocaleListCompat maps languages to expected tags`() {
        AppLanguage.SYSTEM.toLocaleListCompat().toLanguageTags() shouldBe ""
        AppLanguage.RU.toLocaleListCompat().toLanguageTags() shouldBe "ru"
        AppLanguage.EN.toLocaleListCompat().toLanguageTags() shouldBe "en"
    }
}
