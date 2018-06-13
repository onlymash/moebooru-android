package im.mash.moebooru.core.interceptor

import im.mash.moebooru.core.constants.Cookies
import okhttp3.Interceptor
import okhttp3.Response

/* credit: https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b */
class AddCookiesInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookies = Cookies.values

        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
        }
        return chain.proceed(builder.build())
    }
}