package ua.com.info.prize

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater

class ConfirmCodeDialog : DialogFragment() {

    var confirmCode : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         confirmCode = arguments?.getString(CONFIRMATION_CODE)

    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setView(LayoutInflater.from(context).inflate(R.layout.fragment_confirm_dialog, null))
                .setTitle(getString(R.string.confirm_code))
                .setMessage("Введіть код підтвердження")
                .setPositiveButton(getString(R.string.btn_confirm)) { _, _ ->
                 //todo: обробити ввід юзера
                }
                .setNegativeButton(getString(R.string.btn_cancel)){ dialog, which ->
                    dismiss()
                }
        return builder.create()
    }

    private fun incorrectInputError() {

    }
}