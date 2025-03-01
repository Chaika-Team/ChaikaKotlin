package com.example.chaika.testUtils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Утилиты для тестирования.
 *
 * Функция getOrAwaitValue ожидает, пока LiveData не опубликует значение.
 * Подходит для синхронного тестирования LiveData.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    afterObserve.invoke()
    if (!latch.await(time, timeUnit)) {
        throw Exception("LiveData значение не было установлено в течение ожидания.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
