package com.chaikasoft.app.domain.sealed

sealed interface AddItemToCartWithLimitResult {
    data object Added : AddItemToCartWithLimitResult
    data object AlreadyInCart : AddItemToCartWithLimitResult
    data object OutOfStock : AddItemToCartWithLimitResult
}
