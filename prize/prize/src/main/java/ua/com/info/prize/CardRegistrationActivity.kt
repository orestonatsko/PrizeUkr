package ua.com.info.prize

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_card_registration.*
import ua.com.info.data.Result

const val SUCCESS_REGISTER = 0
const val DO_NOT_FOUND = 1
const val ALREADY_REGISTERED = 2
const val NOT_SPECIFIED_REGISTERED = 100
const val CARD_STATE = "СтанКартки"

interface OnCardStateListener {
    fun getCardState(result: Result)
}

class CardRegistrationActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_registration)

        btn_register.setOnClickListener(this)
        number_fields.button = btn_register
    }

    private fun registerCard() {
        val intent = Intent(this, CardCheckedActivity::class.java)
        val cardNumber = number_fields.getNumbers()

        var cardState: Int?
        var par: Int?
        var poll: Int?
        DbHelper.checkCardState(cardNumber, object : OnCardStateListener {
            override fun getCardState(result: Result) {
                if (result.isOk) {
                    val variables = result.variables
                    cardState = variables?.getInt(RETURN_VALUE)
                    if (!(cardState == DO_NOT_FOUND || cardState == ALREADY_REGISTERED)) {
                        par = variables?.getInt(PAR)
                        intent.putExtra(PAR, par)
                        poll = variables?.getInt(POLL)
                        if (poll != null)
                            intent.putExtra(POLL, poll!!)
                    }
                    intent.putExtra(CARD_STATE, cardState)
                    intent.putExtra(CARD_NUMBER, cardNumber)
                    startActivity(intent)
                } else{
                    error(result.error()!!)
                }
            }
        })
    }
    private fun error(er : String){
        error.visibility = View.VISIBLE
        error.text = er
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_register -> {
                registerCard()
                btn_register.isEnabled = false
            }
        }
    }
}