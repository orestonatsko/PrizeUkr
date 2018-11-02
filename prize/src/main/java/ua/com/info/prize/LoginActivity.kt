package ua.com.info.prize

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import ua.com.info.common.Utils
import ua.com.info.data.Result

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var _loginMode = LoginMode.CARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        card_entrance_fields.setBackground(R.drawable.background_field_entrance)
        card_entrance_fields.button = btn_open

        tv_change_entrance.setOnClickListener(this)
        btn_open.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            tv_change_entrance -> {
                changeEntrance()
            }
            btn_open -> {
                loginMode = _loginMode
                login()
            }
        }
    }


    private fun login() {
        if (_loginMode == LoginMode.CARD) {
            val login = card_entrance_fields.getNumbers()
            DbHelper.getUserInfoByCard(login, object : OnUserInfoListener {
                override fun getUserInfo(result: Result) {
                    if (result.isOk) {
                        UserInfo.login = login
                        saveAndOut(result)
                    }
                }
            })
        } else {
            val tel = et_tel.text.toString()
            val telNumb = Utils.telToNumber(tel).toString()
            val pass = et_pass.text.toString()

            DbHelper.getUserInfoByTel(telNumb, pass, object : OnUserInfoListener {
                override fun getUserInfo(result: Result) {
                    if (result.isOk) {
                        saveAndOut(result)
                    }
                }
            })
        }
    }

    private fun toMainActivity() {
        val intent = Intent(this, PrizeMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun saveAndOut(result: Result) {
        UserInfo.save(this, result.variables!!)
        loginMode = _loginMode
        putLoginMode(this, _loginMode)
        toMainActivity()
    }

    private fun changeEntrance() {
        if (_loginMode == LoginMode.CARD) {
            card_entrance_fields.visibility = View.GONE
            tel_entrance_fields.visibility = View.VISIBLE
            tv_change_entrance.setTextColor(ContextCompat.getColor(this, R.color.green_light))
            tv_change_entrance.text = resources.getString(R.string.card_entrance)
            _loginMode = LoginMode.TEL
        } else {
            tel_entrance_fields.visibility = View.GONE
            card_entrance_fields.visibility = View.VISIBLE
            tv_change_entrance.setTextColor(ContextCompat.getColor(this, R.color.light_blue))
            tv_change_entrance.text = resources.getString(R.string.tel_entrance)
            _loginMode = LoginMode.CARD
        }
    }

}
