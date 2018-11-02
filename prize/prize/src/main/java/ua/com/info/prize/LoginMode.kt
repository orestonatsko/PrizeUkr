package ua.com.info.prize

import java.lang.Exception

enum class LoginMode {
    NONE,
    CARD,
    TEL
}
fun toLoginMode(value :String) : LoginMode{
    return try{
        LoginMode.valueOf(value)
    }catch (e : Exception){
        LoginMode.NONE
    }
}