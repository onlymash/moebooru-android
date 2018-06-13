package im.mash.moebooru.core.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

/* credit: https://stackoverflow.com/a/43366296 */
class BasicAuthenticationInterceptor(private val username: String, private val password: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val basic = Credentials.basic(username, password)
        val builder = chain.request().newBuilder().header("Authorization", basic)
        return chain.proceed(builder.build())
    }
}