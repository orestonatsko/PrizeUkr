package ua.com.info.prize

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_my_office.*
import ua.com.info.data.Result


interface OnWritePersonalDataListener {
    fun onWrite(result: Result)
}

class MyOfficeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var fName: String
    private lateinit var sName: String
    private lateinit var mName: String
    private lateinit var mail: String
    private lateinit var tel : String

    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_office)

        number_fields.setBackground(R.drawable.border_round)
        number_fields.focusableInTouchMode(false)
        number_fields.fill(UserInfo.login)

        when (loginMode) {
            LoginMode.CARD -> {
                cardAccess()
            }
            LoginMode.TEL -> {
                telAccess()
            }
            LoginMode.NONE->{
                //empty body
            }
        }
//        if(loginMode == Lo)

        btn_apply_data.setOnClickListener(this)
        btn_apply_password.setOnClickListener(this)
        rb_card.setOnClickListener(this)
        rb_tel.setOnClickListener(this)
    }

    private fun cardAccess() {
        rb_card.isChecked = true
    }

    private fun telAccess() {
        rb_tel.isChecked = true
        rb_tel.isEnabled = true
        et_new_pass.isEnabled = true
        et_rep_pass.isEnabled = true
        btn_apply_password.isEnabled = true
        insertUserInfo()
    }

    private fun insertUserInfo() {
        et_fName.setText(UserInfo.fName)
        et_sName.setText(UserInfo.sName)
        et_mName.setText(UserInfo.mName)
        et_mail.setText(UserInfo.email)
        et_tel.setText(UserInfo.tel)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_apply_data -> {
//                applyData()
            }
            btn_apply_password -> {

            }
            rb_card -> {
                rb_tel.isChecked = false
            }
            rb_tel -> {
                rb_card.isChecked = false
            }
        }
    }

    private fun applyData() {
        if (requestedFieldsIsValid()) {
            writeData()
        }
    }

    private fun requestedFieldsIsValid(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun error() {
        highlightField(et_tel)
    }

    private fun highlightField(et: EditText) {
        et.setBackgroundResource(R.drawable.border_required_field)
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)
        et.startAnimation(shakeAnim)
    }


    private fun writeData() {
        fName = et_fName.text.toString()
        sName = et_sName.text.toString()
        mName = et_mName.text.toString()
        mail = et_mail.text.toString()
        tel = et_tel.toString()
        writePersonalData(fName, sName, mName, mail, tel)
    }

    private fun writePersonalData(fName: String, sName: String, mName: String, mail: String, tel: String) {
        DbHelper.writePersonalData(fName, sName, mName, mail, tel, object : OnWritePersonalDataListener {
            override fun onWrite(result: Result) {
                if (result.isOk) {
                    val confirmCode = result.variables!!.getString(CONFIRMATION_CODE)
                    showConfirmDialog()
                }
            }
        })
    }

    private fun showConfirmDialog() {
        val confirmDialog = ConfirmCodeDialog()
        confirmDialog.show(supportFragmentManager, CONFIRMATION_CODE)
    }

    private fun valid(text: Editable?): Boolean {
        return !text.isNullOrEmpty()
    }
}
