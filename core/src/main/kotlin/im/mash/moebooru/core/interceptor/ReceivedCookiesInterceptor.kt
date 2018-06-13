package im.mash.moebooru.core.interceptor

import im.mash.moebooru.core.constants.Cookies
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/* credit: https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b */
class ReceivedCookiesInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()

            for (header in originalResponse.headers("Set-Cookie")) {
                println("Set-Cookie: $header")
                cookies.add(header)
            }
            Cookies.values = cookies
        }
        return originalResponse
    }
}