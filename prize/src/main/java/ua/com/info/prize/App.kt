package ua.com.info.prize

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import ua.com.info.data.DataBase
import ua.com.info.sqldb.SQLService

const val LOG_TAG = "PRIZE_LOGS"
const val LOGIN_MODE = "loginMode"

var ANDROID_ID: String = ""
var loginMode: LoginMode? = null

class App : Application() {
    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        DataBase.db = SQLService(this, getString(R.string.url))
        ANDROID_ID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        getLoginMode(this)

    }
}

fun hasNetworkConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun getLoginMode(context: Context) {
    val sPref = context.getSharedPreferences(LOGIN_MODE, Context.MODE_PRIVATE)
    val strLoginMode = sPref.getString(LOGIN_MODE, "")
    loginMode = toLoginMode(strLoginMode)
}

fun putLoginMode(context: Context, lm: LoginMode) {
    val editor = context.getSharedPreferences(LOGIN_MODE, Context.MODE_PRIVATE).edit()
    editor.putString(LOGIN_MODE, lm.toString())
    editor.apply()
}

fun exit(context: Context) {
    UserInfo.clearInfo(context)
    putLoginMode(context, LoginMode.NONE)
}
