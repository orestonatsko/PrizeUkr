//package ua.com.info.prize
//
//import android.content.Context
//import android.util.Log
//import java.io.File
//import java.io.FileOutputStream
//
//
//private val charArr = ArrayList<Char>()
////    private val byteArr = ArrayList<Byte>()
//private var byteArr: ByteArray? = null
//private val sb = StringBuilder("")
//private val fileName = "userInfo"
//
//
//private fun showDataInput() {
//    val s1 = String(charArr.toCharArray())
//    val s2 = String(byteArr!!, Charsets.UTF_8)
//    val s3 = String(sb)
//    Log.d(LOG_TAG, s1)
//    Log.d(LOG_TAG, s2)
//    Log.d(LOG_TAG, s3)
//}
//
//private fun saveToInternalStorage() {
//    val dir = filesDir
//    val f = File(dir, fileName)
//    val deleted = f.delete()
//    val fOut: FileOutputStream?
//    try {
//        fOut = openFileOutput(fileName, Context.MODE_PRIVATE)
//        fOut.write(UserInfo.fName?.toByteArray(Charsets.UTF_8))
//        fOut.write(UserInfo.fName?.toByteArray())
//        fOut.write(UserInfo.sName?.toByteArray())
//        fOut.write(UserInfo.mName?.toByteArray())
//        fOut.write(UserInfo.email?.toByteArray())
//        fOut.write(UserInfo.tel?.toByteArray())
//        fOut.close()
//    } catch (e: Exception) {
//        Log.d(LOG_TAG, "${e.printStackTrace()}")
//    }
//}
//
//private fun getUserInfoFromInternalStorage() {
//    try {
//        val fIn = openFileInput(fileName)
//        var data: Int
//        var i = 0
//        data = fIn.read()
//        var character: Char
//        while (data != -1) {
//            character = data.toChar()
////                byteArr.add(data.toByte())
//            charArr.add(character)
//            i++
//            sb.append("int= $data; char= $character;")
//            sb.append("byte= ${data.toByte()}; char= $character;\n")
//            data = fIn.read()
//        }
//    } catch (e: Exception) {
//        Log.d(LOG_TAG, e.toString())
//    }
//}
//
