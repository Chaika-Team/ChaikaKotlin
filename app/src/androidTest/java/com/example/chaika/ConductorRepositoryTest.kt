package com.example.chaika

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.repo.ConductorRepository
import com.example.chaika.domain.models.Conductor
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ConductorRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var conductorDao: ConductorDao
    private lateinit var conductorRepository: ConductorRepository

    @Before
    fun setup() {
        // Инициализация базы данных в тестовом окружении
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Используем основной поток для тестов
            .build()

        conductorDao = database.conductorDao()
        conductorRepository = ConductorRepository(conductorDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndFetchConductor() = runBlocking {
        // Проверка, что таблица изначально пустая
        var conductors = conductorRepository.getAllConductors()
        assertEquals(0, conductors.size)

        // Добавление записи "Иванов И. И."
        val conductor = Conductor(
            id = 0,
            name = "Иванов И. И.",
            image = ""
        )
        conductorRepository.insertConductor(conductor)

        // Повторная проверка
        conductors = conductorRepository.getAllConductors()
        assertEquals(1, conductors.size)
        assertEquals("Иванов И. И.", conductors[0].name)
    }
}
