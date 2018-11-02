package ua.com.info.prize

import ua.com.info.data.*

/**
 *  Створено oor 10.09.2018.
 */
const val USER_INFO = "user_info"

const val ANDROID = "Android"
const val NUMBER = "Номер"
const val CARD_NUMBER = "НомерКартки"
const val TREASURY_BOX_NUMBER = "НомерСкарбнички"
const  val RETURN_VALUE = "RETURN_VALUE"
const val PAR = "Номінал"
const val POLL = "Опитування"
const val NAME = "Назва"
const val PRIZE = "Приз"
const val IMAGE_COUNT = "КількістьМалюнків"
const val STARS = "Зірочок"
const val DESCRIPTION = "Опис"
const val INFORMATION = "Інформація"
const val ACTION_DATE = "ДатаРозіграшу"
const val PLACES_LEFT = "ЗалишилосьМісць"
const val PLACES_ALL = "ВсьогоМісць"
const val PLACES_NUMBER = "КількістьМісць"
const val END_DATE = "КінцеваДата"
const val FAMILIAR_WITH_CONDITIONS = "ОзнайомленийЗУмовами"

const val LOGIN = "Логін"
const val F_NAME = "Прізвище"
const val S_NAME = "Імя"
const val M_NAME = "Побатькові"
const val EMAIL = "EMail"
const val TEL = "Телефон"
const val PASS = "пароль"
const val CONFIRMATION_CODE = "КодПідтвердження"

const val CODE = "Код"
const val SECURITY_STAMP = "SecurityStamp"
//const val REMAINDER = "Залишок"
const val HAS_PRIZES = "єПризи"
const val HAS_NEW_PRIZES = "єНовіПризи"
const val HAS_MESSAGES = "єПовідомлення"
const val HAS_NEW_MESSAGES = "єНовіПовідомлення"

interface OnGetDataListener{
    fun getData();
}

object DbHelper {
    fun getUserInfoByCard(login: Long, listener: OnUserInfoListener) {
        val cmd = "API.Інформація_ЗчитатиЗаНомеромСкарбнички"
        val parameters = Parameters("Android", ANDROID_ID)
        parameters.add(Parameter(NUMBER, login))
        parameters.add(Parameter(CODE, Direction.output, Types.Int))
        parameters.add(Parameter(NAME, Direction.output, Types.VarChar))
        parameters.add(Parameter(SECURITY_STAMP, Direction.output, Types.VarChar))
        parameters.add(Parameter(STARS, Direction.output, Types.Int))
        parameters.add(Parameter(HAS_PRIZES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_NEW_PRIZES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_MESSAGES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_NEW_MESSAGES, Direction.output, Types.Bit))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.getUserInfo(result)
        }
    }

    fun getUserInfoByTel(tel : String, pass : String, listener: OnUserInfoListener) {
        val cmd = "API.Інформація_ЗчитатиЗаТелефоном"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(TEL, tel))
        parameters.add(Parameter(PASS, pass, Direction.output, Types.VarChar))
        parameters.add(Parameter(TREASURY_BOX_NUMBER, Direction.output, Types.BigInt))
        parameters.add(Parameter(CODE, Direction.output, Types.Int))
        parameters.add(Parameter(NAME, Direction.output, Types.VarChar))
        parameters.add(Parameter(SECURITY_STAMP, Direction.output, Types.VarChar))
        parameters.add(Parameter(STARS, Direction.output, Types.Int))
        parameters.add(Parameter(HAS_PRIZES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_NEW_PRIZES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_MESSAGES, Direction.output, Types.Bit))
        parameters.add(Parameter(HAS_NEW_MESSAGES, Direction.output, Types.Bit))
        DataBase.db.Execute(cmd, parameters){ result ->
            listener.getUserInfo(result)
        }
    }

    fun checkCardState(number: Long, listener: OnCardStateListener) {
        val cmd = "API.Перевірити"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(NUMBER, number))
        parameters.add(Parameter(PAR, Direction.output, Types.Int))
        parameters.add(Parameter(POLL, Direction.output, Types.Int))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.getCardState(result)
        }
    }

    fun createTreasuryBox(cardNumber: Long, listener: OnCreateTreasuryListener) {
        val cmd = "API.СтворитиСкарбничку"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(CARD_NUMBER, cardNumber))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.treasuryCreated(result)
        }
    }

    fun addToTreasury(login: Long, cardNumber: Long, listener: OnAddToTreasuryListener) {
        val cmd = "API.ДодатиДоСкарбнички"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(TREASURY_BOX_NUMBER, login))
        parameters.add(Parameter(CARD_NUMBER, cardNumber))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.added(result)
        }
    }

    fun getPrizeDetail(prizeCode: Int, listener: OnReadDetailsListener) {
        val cmd = "API.Призи_Зчитати_Детально"
        val parameters = Parameters(TREASURY_BOX_NUMBER, UserInfo.login)
        parameters.add(Parameter(PRIZE, prizeCode))
        parameters.add(Parameter(NAME, Direction.output, Types.VarChar))
        parameters.add(Parameter(IMAGE_COUNT, Direction.output, Types.Int))
        parameters.add(Parameter(SECURITY_STAMP, Direction.output, Types.Int))
        parameters.add(Parameter(DESCRIPTION, Direction.output, Types.VarChar))
        parameters.add(Parameter(STARS, Direction.output, Types.Int))
        parameters.add(Parameter(INFORMATION, Direction.output, Types.Int))
        parameters.add(Parameter(ACTION_DATE, Direction.output, Types.SmallDateTime))
        parameters.add(Parameter(PLACES_LEFT, Direction.output, Types.Int))
        parameters.add(Parameter(PLACES_ALL, Direction.output, Types.Int))
        parameters.add(Parameter(END_DATE, Direction.output, Types.Date))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.onRead(result)
        }
    }

    fun numberOfSeats(prizeCode: Int, listener: OnParticipateListener) {
        val cmd = "API.КількістьМісць"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(TREASURY_BOX_NUMBER, UserInfo.login))
        parameters.add(Parameter(PRIZE, prizeCode))
        parameters.add(Parameter(STARS, Direction.output, Types.Int))
        parameters.add(Parameter(PLACES_NUMBER, Direction.output, Types.Int))
        parameters.add(Parameter(FAMILIAR_WITH_CONDITIONS, Direction.output, Types.Bit))
        parameters.add(Parameter(END_DATE, Direction.output, Types.DateTime))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.onAssume(result)
        }

    }

    fun participateInAction(login: Long, prizeCode: Int, listener: JoinToActionListener) {
        val cmd = "API.ВзятиУчасть"
        val parameters = Parameters(TREASURY_BOX_NUMBER, login)
        parameters.add(Parameter(PRIZE, prizeCode))
        DataBase.db.Execute(cmd, parameters) { result ->
            listener.onJoin(result)
        }
    }

    fun writePersonalData(fName : String, sName :String, mName : String, mail : String, tel : String, listener : OnWritePersonalDataListener) {
        val cmd = "API.ПерсональніДані_Записати"
        val parameters = Parameters(ANDROID, ANDROID_ID)
        parameters.add(Parameter(TREASURY_BOX_NUMBER, UserInfo.login))
        parameters.add(Parameter(F_NAME, fName))
        parameters.add(Parameter(S_NAME, sName))
        parameters.add(Parameter(M_NAME, mName))
        parameters.add(Parameter(EMAIL, mail))
        parameters.add(Parameter(TEL, tel))
        parameters.add(Parameter(CONFIRMATION_CODE, Direction.output, Types.VarChar))
        DataBase.db.Execute(cmd, parameters){ result ->
            listener.onWrite(result)
        }
    }
}
