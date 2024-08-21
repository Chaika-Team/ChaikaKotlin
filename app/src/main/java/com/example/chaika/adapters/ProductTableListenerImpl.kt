// ProductTableListenerImpl.kt
package com.example.chaika.adapters

import android.content.Context
import com.example.chaika.services.ProductTableViewModel
import com.example.chaika.utils.ProductInTrip
import com.example.chaika.utils.dialogs.ReplenishAddProductDialog
import com.example.chaika.utils.dialogs.SellProductDialog

class ProductTableListenerImpl(
    private val context: Context,
    private val productTableViewModel: ProductTableViewModel,
    private val tripId: Int
) : ProductTableListener {

    override fun onSellProduct(product: ProductInTrip) {
        SellProductDialog(context) { operationId, quantity ->
            productTableViewModel.addAction(tripId, product.id, operationId, quantity)
        }.show()
    }

    override fun onReplenishProduct(product: ProductInTrip) {
        ReplenishAddProductDialog(context, product) { quantity ->
            productTableViewModel.addAction(tripId, product.id, 4, quantity)
        }.show()
    }

    override fun onDeleteProduct(product: ProductInTrip) {
        productTableViewModel.deleteActionsForProductInTrip(product.id, tripId)
    }
}
