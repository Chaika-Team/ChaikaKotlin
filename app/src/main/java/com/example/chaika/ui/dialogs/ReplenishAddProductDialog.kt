package com.example.chaika.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chaika.databinding.DialogEnterProductQuantityBinding
import com.example.chaika.models.ProductInTrip

class ReplenishAddProductDialog(
    private val context: Context,
    private val product: ProductInTrip,
    private val onQuantityEntered: (Int) -> Unit
) {
    fun show() {
        val dialogView = DialogEnterProductQuantityBinding.inflate(LayoutInflater.from(context))

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView.root)
            .create()

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.btnAdd.setOnClickListener {
            val quantity = dialogView.editTextQuantity.text.toString().toIntOrNull() ?: 0
            if (quantity in 1..99) {
                onQuantityEntered(quantity)
                alertDialog.dismiss()
            } else {
                Toast.makeText(context, "Введите корректное количество", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
