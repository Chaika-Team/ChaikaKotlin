package com.example.chaika.utils

import android.widget.Filter
import java.util.Locale

class GenericFilter<T>(
    private val originalList: List<T>,
    private val filterCriteria: (T, String) -> Boolean,
    private val onFiltered: (List<T>) -> Unit,
    private val clazz: Class<T> // Добавляем класс для проверки типа
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filteredList = if (constraint.isNullOrBlank()) {
            originalList
        } else {
            val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
            originalList.filter { filterCriteria(it, filterPattern) }
        }

        return FilterResults().apply { values = filteredList }
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        // Получаем отфильтрованный список из результатов фильтрации
        val filteredItems = results?.values

        if (filteredItems is List<*>) {
            // Проверяем, что элементы списка имеют тип T перед приведением
            @Suppress("UNCHECKED_CAST")
            val typedFilteredItems = filteredItems.filter { clazz.isInstance(it) }.map { it as T }

            // Приведение типа, чтобы получить список нужного типа T
            onFiltered(typedFilteredItems)
        } else {
            // Если результат фильтрации не является списком, передаем пустой список
            onFiltered(emptyList())
        }
    }
}
