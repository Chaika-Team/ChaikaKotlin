package com.chaikasoft.app.util

import android.util.Base64
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.mockito.MockedStatic
import org.mockito.Mockito
import java.security.MessageDigest

class PKCEUtilTest : FunSpec({

    lateinit var base64Mock: MockedStatic<Base64>

    beforeSpec {
        // Стабилизируем Android Base64 для JVM unit: проксируем в java.util.Base64.
        base64Mock = Mockito.mockStatic(Base64::class.java)
        base64Mock.`when`<String> {
            Base64.encodeToString(Mockito.any(), Mockito.anyInt())
        }.thenAnswer { invocation ->
            java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(invocation.getArgument(0))
        }
    }

    afterSpec {
        base64Mock.close()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Проверяем длину и формат code verifier при разных длинах входного буфера.
     */
    test("generateCodeVerifier returns URL-safe value with expected length") {
        val verifier64 = PKCEUtil.generateCodeVerifier()
        val verifier32 = PKCEUtil.generateCodeVerifier(32)

        verifier64.length shouldBe 86
        verifier32.length shouldBe 43
        verifier64.shouldMatch(Regex("^[A-Za-z0-9\\-_.~]*$"))
        verifier32.shouldMatch(Regex("^[A-Za-z0-9\\-_.~]*$"))
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Для известного verifier хеш-челлендж должен совпадать с вручную рассчитанным значением.
     */
    test("generateCodeChallenge returns known SHA-256 based value") {
        val verifier = "test_code_verifier"
        val expected = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
            MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)),
        )

        PKCEUtil.generateCodeChallenge(verifier) shouldBe expected
    }
})

