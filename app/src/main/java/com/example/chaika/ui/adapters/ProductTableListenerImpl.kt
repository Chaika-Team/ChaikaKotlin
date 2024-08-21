// ProductTableListenerImpl.kt
package com.example.chaika.ui.adapters

import android.content.Context
import com.example.chaika.ui.view_models.ProductTableViewModel
import com.example.chaika.models.ProductInTrip
import com.example.chaika.ui.dialogs.ReplenishAddProductDialog
import com.example.chaika.ui.dialogs.SellProductDialog

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
