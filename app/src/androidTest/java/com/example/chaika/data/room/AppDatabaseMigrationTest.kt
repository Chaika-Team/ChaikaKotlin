package com.example.chaika.data.room

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.migrations.AppMigrations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun validatesCurrentBaselineSchema() {
        helper.createDatabase(TEST_DB, 1).close()

        val db = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        )
            .addMigrations(*AppMigrations.ALL)
            .build()

        db.openHelper.writableDatabase.close()
        db.close()
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}
