package im.mash.moebooru.util

import android.content.Context
import im.mash.moebooru.R
import java.util.*

object ColorUtil {

    fun getCustomizedColor(context: Context): Int {
        val customizedColors = context.resources.getIntArray(R.array.customizedColors)
        return customizedColors[Random().nextInt(customizedColors.size)]
    }

    fun getCustomizedColor(context: Context, text: String): Int {
        val customizedColors = context.resources.getIntArray(R.array.customizedColors)
        val str0 = "0azAZ"
        val str1 = "1byBY"
        val str2 = "2cxCX"
        val str3 = "3dwDW"
        val str4 = "4evEV"
        val str5 = "5fuFU"
        val str6 = "6gtGT"
        val str7 = "7hsHS"
        val str8 = "8irIR"
        val str9 = "9jqJQ"
        return when {
            str0.contains(text) -> customizedColors[0]
            str1.contains(text) -> customizedColors[1]
            str2.contains(text) -> customizedColors[2]
            str3.contains(text) -> customizedColors[3]
            str4.contains(text) -> customizedColors[4]
            str5.contains(text) -> customizedColors[5]
            str6.contains(text) -> customizedColors[6]
            str7.contains(text) -> customizedColors[7]
            str8.contains(text) -> customizedColors[8]
            str9.contains(text) -> customizedColors[9]
            else -> customizedColors[9]
        }
    }
}