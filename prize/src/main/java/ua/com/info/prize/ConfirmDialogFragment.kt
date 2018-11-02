package ua.com.info.prize

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater

/**
 *  Створено oor 10.10.2018.
 */
class ConfirmDialogFragment : DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setView(LayoutInflater.from(context).inflate(R.layout.fragment_confirm_dialog, null))

        return super.onCreateDialog(savedInstanceState)
    }
}