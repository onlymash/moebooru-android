package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.Button
import android.widget.TextView
import com.liulishuo.okdownload.DownloadTask
import im.mash.moebooru.App.Companion.moePath
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.core.constants.Constants
import im.mash.moebooru.core.constants.Cookies
import im.mash.moebooru.core.interceptor.ReceivedCookiesInterceptor
import im.mash.moebooru.download.DownloadListener
import im.mash.moebooru.download.DownloadService
import im.mash.moebooru.util.HashUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

class DownloadFragment : ToolbarFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_test, container, false)
    }

    private lateinit var tv: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn: Button = view.findViewById(R.id.btn_action)
        tv = view.findViewById(R.id.tv_info)

        val listener = DownloadListener()
        btn.setOnClickListener {
            val url = "https://konachan.com/image/176ca4155fcc1645ecc16092388531c9/Konachan.com%20-%20266581%20anus%20ass%20blue_eyes%20blush%20food%20gloves%20gray_hair%20joosi%20kaban%20long_hair%20penis%20pussy%20serval%20sex%20skirt%20tail%20thighhighs%20uncensored%20watermark%20wolfgirl.png"
            val task = DownloadTask.Builder(url, moePath, "test3.png")
                    .build()
            task.enqueue(listener)
            task.execute(listener)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun testLogin() {

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                    .removeHeader(Constants.USER_AGENT_KEY)
                    .addHeader(Constants.USER_AGENT_KEY, WebSettings.getDefaultUserAgent(context))
                    .build()
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(ReceivedCookiesInterceptor())
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://konachan.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        val loginService = retrofit.create(LoginService::class.java)

        val url = HttpUrl.Builder()
                .scheme("https")
                .host("konachan.com")
                .addPathSegment("post.json")
                .addQueryParameter("login", "godating")
                .addQueryParameter("password_hash", HashUtil.sha1("So-I-Heard-You-Like-Mupkids-?--pass2018--"))
                .build()

        loginService.login(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tv.text = "$it  Cookies.values: ${Cookies.values}"
                }, {
                    tv.text = "$it  Cookies.values: ${Cookies.values}"
                }, {
                    tv.text = "Cookies.values: ${Cookies.values}"
                })
    }

    interface LoginService {

        @GET
        fun login(@Url url: HttpUrl): Observable<ResponseBody>
    }
}