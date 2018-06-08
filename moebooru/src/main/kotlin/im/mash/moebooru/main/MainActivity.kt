package im.mash.moebooru.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.application.BaseActivity
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.viewmodel.PostViewModel
import im.mash.moebooru.main.viewmodel.PostViewModelFactory
import im.mash.moebooru.util.getViewModel
import okhttp3.HttpUrl
import java.io.IOException
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val component by lazy { MoeDH.mainComponent() }

    @Inject
    lateinit var viewModelFactory: PostViewModelFactory
    private val viewModel: PostViewModel by lazy { this.getViewModel<PostViewModel>(viewModelFactory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "This is MainActivity")
        component.inject(this)
        val httpUrlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host("konachan.com")
                .addPathSegments("post.json")
        val buttonLoad = findViewById<Button>(R.id.btn_load)
        val buttonRefresh = findViewById<Button>(R.id.btn_refresh)
        val buttonLoadMore = findViewById<Button>(R.id.btn_load_more)
        val textView = findViewById<TextView>(R.id.tv_info)
        buttonLoad.setOnClickListener {
            viewModel.loadPosts(httpUrlBuilder.addQueryParameter("page", "1").build())
        }
        buttonRefresh.setOnClickListener {
            viewModel.refreshPosts(httpUrlBuilder.addQueryParameter("page", "1").build())
        }
        var page = 1
        buttonLoadMore.setOnClickListener {
            page += 1
            viewModel.loadMorePosts(httpUrlBuilder.addQueryParameter("page", "$page").build())
        }
        viewModel.postsOutcome.observe(this, Observer<Outcome<MutableList<Post>>> { outcome  ->
            when (outcome) {
                is Outcome.Progress -> {
                    Log.i(TAG, "Outcome.Progress")
                }
                is Outcome.Success -> {
                    Log.i(TAG, "Outcome.Success")
                    textView.text = outcome.data.size.toString()
                }
                is Outcome.Failure -> {
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                        textView.text = outcome.e.message
                    } else {
                        Log.i(TAG, "Outcome.Failure")
                    }
                }
            }
        })
    }

}