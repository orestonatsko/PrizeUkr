package ua.com.info.prize

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_my_office.view.*
import ua.com.info.data.Result


class MyOfficeFragment : Fragment(), View.OnClickListener{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_office, container, false)

        view.number_fields.setBackground(R.drawable.border_round)
        view.number_fields.fill(UserInfo.login)

        when (loginMode) {
            LoginMode.CARD -> {
                cardAccess(view)
            }
            LoginMode.TEL -> {
                telAccess(view)
            }
            LoginMode.NONE -> {
                //empty body
            }
        }

        view?.btn_apply_data?.setOnClickListener(this)
        view?.btn_apply_password?.setOnClickListener(this)
        view?.rb_card?.setOnClickListener(this)
        view?.rb_tel?.setOnClickListener(this)
        return view
    }

    private fun cardAccess(view: View) {
        view.rb_card.isChecked = true
    }

    private fun telAccess(view: View) {
        view.rb_tel.isChecked = true
        view.rb_tel.isEnabled = true
        view.et_new_pass.isEnabled = true
        view.et_rep_pass.isEnabled = true
        view.btn_apply_password.isEnabled = true
        view.number_fields.focusableInTouchMode(false)
        insertUserInfo(view)
    }

    private fun insertUserInfo(view : View) {
        view.et_fName?.setText(UserInfo.fName ?: "")
        view.et_sName?.setText(UserInfo.sName ?: "")
        view.et_mName?.setText(UserInfo.mName ?: "")
        view.et_mail?.setText(UserInfo.email ?: "")
        view.et_tel?.setText(UserInfo.tel ?: "")
    }

    private fun applyData() {
        if (fieldIsValid()) {
//            writeData()
            showDialog("5")
        }
    }

    private fun fieldIsValid(): Boolean {
        return true
    }

    override fun onClick(v: View?) {
        when (v) {
            view?.btn_apply_data -> {
                applyData()

            }
            view?.btn_apply_password -> {

            }
            view?.rb_card -> {
                view?.rb_tel?.isChecked = false
            }
            view?.rb_tel -> {
                view?.rb_card?.isChecked = false
            }
        }
    }

    private fun error() {
        highlightField(view?.et_tel)
    }

    private fun valid(text: Editable?): Boolean {
        return !text.isNullOrEmpty()
    }


    private fun writeData() {
        val fName = view?.et_fName?.text.toString()
        val sName = view?.et_sName?.text.toString()
        val mName = view?.et_mName?.text.toString()
        val mail = view?.et_mail?.text.toString()
        val tel = view?.et_tel.toString()
        writePersonalData(fName, sName, mName, mail, tel)
    }

    private fun writePersonalData(fName: String, sName: String, mName: String, mail: String, tel: String) {
        DbHelper.writePersonalData(fName, sName, mName, mail, tel, object : OnWritePersonalDataListener {
            override fun onWrite(result: Result) {
                if (result.isOk) {
                   val confirmationCode = result.variables?.getString(CONFIRMATION_CODE)
                    showDialog(confirmationCode)
                }
            }
        })
    }

    private fun showDialog(confirmCode : String?) {
        if(confirmCode != null) {
            val dialog = ConfirmCodeDialog()
            val bundle = Bundle()
            bundle.putString(CONFIRMATION_CODE, confirmCode)
            dialog.arguments = bundle
            dialog.setTargetFragment(this@MyOfficeFragment, 0)
            dialog.show(activity?.supportFragmentManager, "ConfirmCodeDialog")
        }
    }

    private fun highlightField(et: TextInputEditText?) {
        if (et != null) {
            et.setBackgroundResource(R.drawable.border_required_field)
            val shakeAnim = AnimationUtils.loadAnimation(context, R.anim.shake)
            et.startAnimation(shakeAnim)
        }
    }

}