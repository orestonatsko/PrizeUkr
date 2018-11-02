package ua.com.info.prize

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.number_fields.view.*

class NumberFields(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var fields: ArrayList<EditText> = ArrayList()
    private val fieldLength = 4
    var button: Button? = null

    init {
        initView()
    }

    private fun initView() {
        val view = inflate(context, R.layout.number_fields, null)

        val focusChangedListener = View.OnFocusChangeListener { v, hasFocus ->

            when (v) {
                view.field_1 -> {
                    if (hasFocus) {
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(view.field_1, InputMethodManager.SHOW_IMPLICIT)
                    }
                    changeHint(view.field_1, hasFocus)
                }
                view.field_2 -> {
                    changeHint(view.field_2, hasFocus)
                }
                view.field_3 -> {
                    changeHint(view.field_3, hasFocus)
                }
                view.field_4 -> {
                    changeHint(view.field_4, hasFocus)
                }
            }
        }
        val textChangeListener = object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == fieldLength) {
                    validate()
                } else {
                    button?.isEnabled = false
                }
            }
        }

        val onKeyListener = OnKeyListener { v, keyCode, event ->
            val field = v as EditText
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && field.text.isEmpty()) {
                for (idx in 1 until fields.size) {
                    if (field == fields[idx]) {
                        val prevField = fields[idx - 1]
                        if (!prevField.text.isEmpty() && prevField.text.length == fieldLength)
                            prevField.setText(prevField.text.substring(0, prevField.length() - 1))
                        prevField.setSelection(prevField.length())
                        prevField.requestFocus()
                        return@OnKeyListener true
                    }
                }
            }
            false
        }

        fields.add(view.field_1)
        fields.add(view.field_2)
        fields.add(view.field_3)
        fields.add(view.field_4)


        fields.forEachIndexed { _, field ->
            field.addTextChangedListener(textChangeListener)
            field.onFocusChangeListener = focusChangedListener
            field.setOnKeyListener(onKeyListener)
        }
        addView(view)
    }

    fun getNumbers(): Long {
        var cardNumber = ""
        fields.forEach { f ->
            cardNumber += f.text.toString()
        }
        return cardNumber.toLong()
    }

    private fun validate() {
        for (f in fields) {
            if (!valid(f)) {
                f.requestFocus()
                return
            }
        }
        button?.isEnabled = true
    }

    fun setBackground(resourceId: Int) {
        fields.forEach { field ->
            field.background = ContextCompat.getDrawable(context, resourceId)
        }
    }

    private fun valid(field: EditText): Boolean {
        if (field.length() == fieldLength && isNumbers(field.text.toString())) {
            return true
        }
        field.text.clear()
        return false
    }

    private fun changeHint(field: EditText, hasFocus: Boolean) {
        val emptyHint = ""
        if (hasFocus) {
            field.hint = emptyHint
        } else {
            field.hint = context.getString(R.string.x)
        }
    }

    private fun isNumbers(text: String): Boolean {
        return text.matches(Regex("\\d+"))
    }

    fun fill(value: Long?) {
        if (value != null) {
            val str = value.toString()
            if (str.length == 16) {
                var start = 0
                var end = 4
                for (f in fields) {
                    f.setText(str.subSequence(start, end).toString())
                    f.clearFocus()
                    start = end
                    end += 4
                }
            }
        }
    }

    fun focusableInTouchMode(focusable: Boolean) {
        fields.forEach { f ->
            f.isFocusableInTouchMode = focusable
        }
    }
}