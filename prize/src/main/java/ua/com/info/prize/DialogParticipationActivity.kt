package ua.com.info.prize

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_dialog_participation.*
import ua.com.info.common.Utils
import ua.com.info.data.Result
import ua.com.info.data.Variables
import ua.com.info.prize.R.id.action_rules


interface JoinToActionListener {
    fun onJoin(result: Result)
}

class DialogParticipationActivity : AppCompatActivity(), View.OnClickListener {

    var stars: Int? = null
    private var familiarized: Any? = null
    private var endDate: String? = null
    private var seats: Int? = null
    private var prizeCode: Int? = null

    object Participate {
        const val SUCCESS = 0
        const val TAKEN = 1
        const val MISSING_STARS = 2
    }

    private val onJoinToActionListener = object : JoinToActionListener {
        override fun onJoin(result: Result) {
            if (result.isOk) {
                val variables = result.variables
                val res = variables!!.getInt(RETURN_VALUE)
                when (res) {
                    Participate.SUCCESS -> {
                        DbHelper.getUserInfoByCard(UserInfo.login!!, object : OnUserInfoListener {
                            override fun getUserInfo(result: Result) {
                                if (result.isOk) {
                                    refreshUserData(variables)
                                }
                            }
                        })
                        finish()
                    }
                    Participate.TAKEN -> {
                        participateError("Приз розіграно. Ви зпізнились.")
                    }
                    Participate.MISSING_STARS -> {
                        action_rules.visibility = View.INVISIBLE
                        btnJoin(false)
                        participateError("Бракує зірок.")
                    }
                }
            }
        }
    }

    private fun refreshUserData(variables: Variables) {
        UserInfo.save(this, variables)
    }

    private fun participateError(msg: String) {
        action_rules.visibility = View.INVISIBLE
        tv_action_date.visibility = View.INVISIBLE
        btnJoin(false)
        tv_participants.setTextColor(ContextCompat.getColor(this, R.color.error))
        tv_participants.text = msg
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_participation)
        this.setFinishOnTouchOutside(false)

        val extras = intent.extras
        stars = extras.getInt(STARS)
        seats = extras.getInt(PLACES_NUMBER)
        prizeCode = extras.getInt(PRIZE)
        familiarized = extras.get(FAMILIAR_WITH_CONDITIONS)
        endDate = extras.getString(END_DATE)

        tv_agreement.text = Html.fromHtml(resources.getString(R.string.msg_agreement), Html.FROM_HTML_MODE_LEGACY)
        tv_agreement.setOnClickListener {
            //todo: Показати умови акції
        }

        viewInfo()
        checkbox_agree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnJoin(true)
            } else {
                btnJoin(false)
            }
        }
        btn_join.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    private fun viewInfo() {
        val textStars = "$stars ${Utils.numEnding(stars!!, "зірочка", "зірочки", "зірочок")}"
        val textDate = resources.getString(R.string.msg_action_date, endDate)
        tv_stars.text = textStars
        tv_action_date.text = Html.fromHtml(textDate, Html.FROM_HTML_MODE_LEGACY)
        tv_participants.text = resources.getString(R.string.msg_participants, seats, Utils.numEnding(seats!!, "учасник", "учасники", "учасників"))
    }

    private fun btnJoin(enabled: Boolean) {
        if (enabled) {
            btn_join.isEnabled = true
            btn_join.setTextColor(getColor(R.color.colorPrimary))
        } else {
            btn_join.isEnabled = false
            btn_join.setTextColor(getColor(R.color.gray))
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_join -> {
                DbHelper.participateInAction(UserInfo.login!!, prizeCode!!, onJoinToActionListener)
            }
            btn_cancel -> {
                finish()
            }
        }
    }
}
