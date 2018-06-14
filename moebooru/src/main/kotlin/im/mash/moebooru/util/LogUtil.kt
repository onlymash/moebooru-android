package im.mash.moebooru.util

import android.util.Log
import im.mash.moebooru.BuildConfig

fun logi(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg)
    }
}