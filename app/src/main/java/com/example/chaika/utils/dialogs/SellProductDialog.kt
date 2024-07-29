package com.example.chaika.utils.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chaika.R
import com.example.chaika.databinding.DialogBuyProductBinding

class SellProductDialog(
    private val context: Context,
    private val onActionConfirmed: (Int, Int) -> Unit
) {
    fun show() {
        val dialogView = DialogBuyProductBinding.inflate(LayoutInflater.from(context))

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
            val operationId = when (dialogView.radioGroupPayment.checkedRadioButtonId) {
                R.id.RbBuyByCash -> 2
                R.id.RbBuyByCard -> 3
                else -> 0
            }

            if (quantity in 1..99) {
                if (operationId != 0) {
                    onActionConfirmed(operationId, quantity)
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(context, "Выберите способ оплаты", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Введите корректное количество", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
