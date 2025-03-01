package com.example.chaika.testUtils

/**
 * Вспомогательная функция для сбора Flow в список.
 *
 * Для мокирования Flow можно использовать функцию collectToList, которая аккумулирует все элементы Flow в список.
 */
suspend fun <T> kotlinx.coroutines.flow.Flow<T>.collectToList(): List<T> {
    val list = mutableListOf<T>()
    this.collect { list.add(it) }
    return list
}
