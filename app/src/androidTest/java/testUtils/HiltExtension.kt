package testUtils

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class HiltExtension(private val testInstance: Any) : BeforeEachCallback {
    // Создаем HiltAndroidRule, передавая в него экземпляр тестового класса.
    private val hiltRule = HiltAndroidRule(testInstance)

    override fun beforeEach(context: ExtensionContext?) {
        hiltRule.inject()
    }
}
