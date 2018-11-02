package ua.com.info.prize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_card_checked.*
import ua.com.info.data.Result

interface OnUserInfoListener {
    fun getUserInfo(result: Result)
}

interface OnCreateTreasuryListener {
    fun treasuryCreated(result: Result)
}

interface OnAddToTreasuryListener {
    fun added(result: Result)
}

class CardCheckedActivity : AppCompatActivity(), View.OnClickListener {

    private var cardNumber: Long = 0
    private var _stage: Stages? = Stages.CARD_CHECKED
    private var stage: Stages? = null
        set(value) {
            when (value) {
                Stages.CARD_CHECKED -> {
                    msg.visibility = View.INVISIBLE
                    number_fields.visibility = View.INVISIBLE
                    btn_add.text = getString(R.string.btn_add)
                    btn_register_new.text = getString(R.string.btn_register_new)
                    btn_register_new.isEnabled = true
                }
                Stages.LOGIN -> {
                    if (loginMode == LoginMode.NONE) {
                        msg.visibility = View.VISIBLE
                        number_fields.visibility = View.VISIBLE
                        btn_add.text = getString(R.string.btn_back)
                        btn_register_new.text = getString(R.string.btn_done)
                        btn_register_new.isEnabled = false
                    } else {
                        addToTreasuryBox()
                    }
                }
            }
            _stage = value
        }

    private enum class Stages {
        CARD_CHECKED,
        LOGIN
    }

    private object CreateTreasuryResult {
        const val SUCCESS = 0
        const val ALREADY_REGISTERED = 1
        const val UNSUCCESSFUL_ATTEMPT = 10
    }

    private object AddTreasuryResult {
        const val SUCCESS = 0
        const val DO_NOT_EXIST = 1
    }

    private object GetInfoByNumberResult {
        const val SUCCESS = 0
        const val INCORRECT_INPUT = -1
        const val BLOCKED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_checked)

        val cardState = intent.getIntExtra(CARD_STATE, -1)
        cardNumber = intent.getLongExtra(CARD_NUMBER, 0L)

        handleCardState(cardState)
        btn_register_new.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        number_fields.button = btn_register_new
    }

    private fun handleCardState(cardState: Int) {
        when (cardState) {
            SUCCESS_REGISTER -> {
                val par = intent.getIntExtra(PAR, -1)
                val ending = ua.com.info.common.Utils.numEnding(par, "зірочка", "зірочки", "зірочок")
                stars.text = par.toString()
                tv_ending.text = ending.toString()
                buttons.visibility = View.VISIBLE
            }
            DO_NOT_FOUND -> {
                message.text = getString(R.string.error_do_not_found)
            }
            ALREADY_REGISTERED -> {
                message.text = getString(R.string.msg_already_registered)
            }
            NOT_SPECIFIED_REGISTERED -> { // todo: обробити коректно: "Швидше зареєструйте карку, після перевірки термін дії обмежений"
                val par = intent.getIntExtra(PAR, -1)
                val ending = ua.com.info.common.Utils.numEnding(par, "зірочка", "зірочки", "зірочок")
                stars.text = par.toString()
                tv_ending.text = ending.toString()
                msg.visibility = View.VISIBLE
                msg.text = "Швидше зареєструйте карку, після перевірки термін дії обмежений"
                buttons.visibility = View.VISIBLE
            }
        }
    }

    private fun prepareForAdd(v: View) {
        when (_stage) {
            Stages.CARD_CHECKED -> {
                stage = Stages.LOGIN
            }
            Stages.LOGIN -> {
                stage = Stages.CARD_CHECKED
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    private fun returnToPrizes() {
        val intent = Intent(this, PrizeMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun error(err: String) {
        error.visibility = View.VISIBLE
        error.text = err
    }

    private fun registerNewCard(context: Context) {
        when (_stage) {
            Stages.CARD_CHECKED -> {
                createTreasuryBox()
            }
            Stages.LOGIN -> {
                    val login = number_fields.getNumbers()
                    checkLoginCard(context, login)
            }
        }
    }

    private fun checkLoginCard(context: Context, login : Long) {
        DbHelper.getUserInfoByCard(login, object : OnUserInfoListener {
            override fun getUserInfo(result: Result) {
                if (result.isOk) {
                    val variables = result.variables
                    val returnValue = variables?.getInt(RETURN_VALUE)
                    when (returnValue) {
                        GetInfoByNumberResult.SUCCESS -> {
                            error.visibility = View.GONE
                            UserInfo.login = login
                            UserInfo.save(context, variables)
                            addToTreasuryBox()
                        }
                        GetInfoByNumberResult.INCORRECT_INPUT -> {
                            error(getString(R.string.error_do_not_found))
                        }
                        GetInfoByNumberResult.BLOCKED -> {
                            error(getString(R.string.error_blocked))
                        }
                    }
                } else {
                    // todo: обробити помилку під'єднання до БД
                    Log.d(LOG_TAG, result.error())
                }
            }
        })
    }

    private fun addToTreasuryBox() {
        DbHelper.addToTreasury(UserInfo.login!!, cardNumber, object : OnAddToTreasuryListener {
            override fun added(result: Result) {
                if (result.isOk) {
                    val addTreasuryResult = result.variables!!.getInt("RETURN_VALUE")
                    when (addTreasuryResult) {
                        AddTreasuryResult.DO_NOT_EXIST -> { // картки не існує
                            error(getString(R.string.error_do_not_found))
                        }
                        AddTreasuryResult.SUCCESS -> {
                            loginMode = LoginMode.CARD
                            returnToPrizes()
                        }
                    }
                } else {
                    // todo: обробити помилку під'єднання до БД
                }
            }
        })
    }

    private fun createTreasuryBox() {
        DbHelper.createTreasuryBox(cardNumber, object : OnCreateTreasuryListener {
            override fun treasuryCreated(result: Result) {
                if (result.isOk) {
                    val variables = result.variables
                    val createTreasuryResult = variables!!.getInt(RETURN_VALUE)
                    when (createTreasuryResult) {
                        CreateTreasuryResult.SUCCESS -> {
                            returnToPrizes()//todo: виконати певні дії, для інформування користувача
                        }
                        CreateTreasuryResult.ALREADY_REGISTERED -> {
                            error(getString(R.string.error_card_already_logged))
                        }
                        CreateTreasuryResult.UNSUCCESSFUL_ATTEMPT -> {
                            error(getString(R.string.error_unsuccessful_attempt))
                        }
                    }
                } else {
                    // todo: обробити помилку під'єднання до БД
                    Log.d(LOG_TAG, result.error())
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_add -> {
                prepareForAdd(v!!)
            }
            btn_register_new -> {
                registerNewCard(this)
            }
        }
    }
}



