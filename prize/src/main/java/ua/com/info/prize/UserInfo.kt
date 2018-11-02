package ua.com.info.prize

import android.content.Context
import ua.com.info.data.Result
import ua.com.info.data.Variables

object UserInfo {
    private var securityStamp: String? = null
    var login: Long? = null
    var stars: Int? = null
    private var code: Int? = null
    private var hasPrizes: Boolean? = null
    private var hasNewPrizes: Boolean? = null
    private var hasMessages: Boolean? = null
    private var hasNewMessages: Boolean? = null

    var fName: String? = null
    var sName: String? = null
    var mName: String? = null
    var email: String? = null
    var tel: String? = null

    fun get(context: Context, listener : OnLoadedListener) {
        val sPref = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE)
        val userInfo = sPref.all
        UserInfo.login = userInfo[LOGIN]?.toString()?.toLong()
        if (hasNetworkConnection(context)) {    // якщо є інтернет, тоді інформацію зчитуємо з сервера
            DbHelper.getUserInfoByCard(UserInfo.login!!, object : OnUserInfoListener {
                override fun getUserInfo(result: Result) {
                    if (result.isOk) {
                        save(context, result.variables!!)
                        listener.onLoaded()
                    }
                }
            })
        } else {
            getFromPref(userInfo)   // якщо немає зв'язку з інтернетом, зчитати з SharedPreferences
        }
    }

    private fun setPersonalData(fName: String, sName: String, mName: String, email: String, tel: String) {
        UserInfo.fName = fName
        UserInfo.sName = sName
        UserInfo.mName = mName
        UserInfo.email = email
        UserInfo.tel = tel
    }

    fun save(context: Context, variables: Variables) {
        UserInfo.code = variables.getInt(CODE)!!
        UserInfo.securityStamp = variables.getString(SECURITY_STAMP)!!
        UserInfo.stars = variables.getInt(STARS)!!
        UserInfo.hasPrizes = variables.getValue(HAS_PRIZES) as Boolean
        UserInfo.hasNewPrizes = variables.getValue(HAS_NEW_PRIZES) as Boolean
        UserInfo.hasMessages = variables.getValue(HAS_MESSAGES) as Boolean
        UserInfo.hasNewMessages = variables.getValue(HAS_NEW_MESSAGES) as Boolean

        putToPref(context)
    }

    private fun putToPref(context: Context) {
        val editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit()
        editor.putLong(LOGIN, UserInfo.login!!)
        editor.putInt(CODE, UserInfo.code!!)
        editor.putString(SECURITY_STAMP, UserInfo.securityStamp!!)
        editor.putLong(STARS, UserInfo.login!!)
        editor.putBoolean(HAS_PRIZES, UserInfo.hasPrizes!!)
        editor.putBoolean(HAS_NEW_PRIZES, UserInfo.hasNewPrizes!!)
        editor.putBoolean(HAS_MESSAGES, UserInfo.hasMessages!!)
        editor.putBoolean(HAS_NEW_MESSAGES, UserInfo.hasNewMessages!!)
        editor.apply()
    }

    private fun getFromPref(userInfo: Map<String, *>) {
        UserInfo.code = userInfo[CODE]?.toString()?.toInt()
        UserInfo.securityStamp = userInfo[SECURITY_STAMP]?.toString()
        UserInfo.stars = userInfo[STARS]?.toString()?.toInt()
        UserInfo.hasPrizes = userInfo[HAS_PRIZES]?.toString()?.toBoolean()
        UserInfo.hasNewPrizes = userInfo[HAS_NEW_PRIZES].toString().toBoolean()
        UserInfo.hasMessages = userInfo[HAS_MESSAGES].toString().toBoolean()
        UserInfo.hasNewMessages = userInfo[HAS_NEW_MESSAGES].toString().toBoolean()
    }

    fun clearInfo(context: Context) {
        val editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
        UserInfo.login = null
        UserInfo.code = null
        UserInfo.securityStamp = null
        UserInfo.stars = null
        UserInfo.hasPrizes = null
        UserInfo.hasNewPrizes = null
        UserInfo.hasMessages = null
        UserInfo.hasNewMessages = null
    }
}