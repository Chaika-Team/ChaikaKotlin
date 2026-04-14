package com.chaikasoft.app.domain.sealed

sealed class SaveOperationResult {
    /** Успешно, возвращаем ID созданной операции (полезно для навигации/диалога). */
    data class Success(val operationId: Int) : SaveOperationResult()

    /** Корзина пуста — операция не записана. */
    data object EmptyCart : SaveOperationResult()

    /** Что-то пошло не так на уровне БД/IO. */
    data class Failure(val reason: String? = null) : SaveOperationResult()
}
