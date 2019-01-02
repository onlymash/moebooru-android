package im.mash.moebooru.search

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.base.LastItemListener
import im.mash.moebooru.common.base.SafeGridLayoutManager
import im.mash.moebooru.common.base.SafeStaggeredGridLayoutManager
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.common.viewmodel.UserViewModel
import im.mash.moebooru.common.viewmodel.UserViewModelFactory
import im.mash.moebooru.common.viewmodel.VoteViewModel
import im.mash.moebooru.common.viewmodel.VoteViewModelFactory
import im.mash.moebooru.core.application.SlidingActivity
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.search.adapter.PostSearchAdapter
import im.mash.moebooru.search.viewmodel.PostSearchViewModel
import im.mash.moebooru.search.viewmodel.PostSearchViewModelFactory
import im.mash.moebooru.util.isNetworkConnected
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.screenWidth
import im.mash.moebooru.util.takeSnackbarShort
import okhttp3.HttpUrl
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchActivity : SlidingActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "SearchActivity"
        private const val STATUS_LOADING = 0
        private const val STATUS_REFRESH = 1
        private const val STATUS_LOAD_MORE = 2
        private const val STATUS_IDLE = -1
    }
    private var paddingBottom = 0
    private var paddingTop = 0

    private var spanCount = 3
    private var page = 1
    private var posts = mutableListOf<PostSearch>()

    private var notiNotMore = true
    private var limit = 50
    private var keyword = ""
    private var safeMode = true
    private var newStart = true
    private var status = STATUS_LOADING

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var postSearchView: RecyclerView
    private lateinit var postSearchAdapter: PostSearchAdapter

    private val component by lazy { MoeDH.searchComponent() }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var searchViewModelFactory: PostSearchViewModelFactory
    private val postSearchViewModel: PostSearchViewModel by lazy { this.getViewModel<PostSearchViewModel>(searchViewModelFactory) }

    private var user: User? = null
    private var users: MutableList<User> = mutableListOf()

    @Inject
    lateinit var userViewModelFactory: UserViewModelFactory
    private val userViewModel: UserViewModel by lazy { this.getViewModel<UserViewModel>(userViewModelFactory) }

    @Inject
    lateinit var voteViewModelFactory: VoteViewModelFactory
    private val voteViewModel: VoteViewModel by lazy { this.getViewModel<VoteViewModel>(voteViewModelFactory) }

    private var voteChangedId = -1
    private var voteChangedScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate")
        setContentView(R.layout.layout_posts_content)
        component.inject(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val key = intent.getStringExtra("keyword")
        if ( key == null) {
            finish()
        }
        keyword = key
        if (keyword == "") finish()
        spanCount = this.screenWidth/resources.getDimension(R.dimen.item_width).toInt()
        limit = app.settings.postLimitInt
        safeMode = app.settings.safeMode
        initView()
        initVoteViewModel()
        initRefresh()
        initPostSearchViewModel()
        initUserViewModel()
        loadPosts()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logi(TAG, "onNewIntent")
        setIntent(intent)
        val key = intent?.getStringExtra("keyword")
        if ( key == null) {
            finish()
        }
        keyword = key!!
        if (keyword == "") finish()
        toolbar.subtitle = keyword
        newStart = true
        status = STATUS_LOADING
        loadPosts()
    }

    private fun initUserViewModel() {
        userViewModel.userOutcome.observe(this, Observer<Outcome<MutableList<User>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {

                }
                is Outcome.Success -> {
                    users.clear()
                    if (outcome.data.size > 0) {
                        users = outcome.data
                        initUser()
                    }
                }
                is Outcome.Failure -> {
                    outcome.e.printStackTrace()
                }
            }
        })
        userViewModel.loadUsers()
    }

    private fun initUser() {
        val schema = app.settings.activeProfileSchema
        val host = app.settings.activeProfileHost
        val baseUrl = "$schema://$host"
        user = null
        users.forEach { user ->
            if (user.url == baseUrl) {
                this.user = user
                return@forEach
            }
        }
        if (user != null) {
            voteViewModel.getVoteIdsOneTwo(host, user!!.name)
            voteViewModel.getVoteIdsThree(host, user!!.name)
        }
    }

    private fun disableRefreshLayout() {
        if (refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = false
        }
    }

    private fun enableRefreshLayout() {
        if (!refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
        }
    }

    private fun initPostSearchViewModel() {
        postSearchViewModel.postsSearchOutcome.observe( this,
                Observer<Outcome<MutableList<PostSearch>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {
                    logi(TAG, "Outcome.Progress")
                }
                is Outcome.Success -> {
                    logi(TAG, "postViewModel Outcome.Success. status: $status")
                    disableRefreshLayout()
                    if (outcome.data.size > 0 && outcome.data[0].keyword != keyword) return@Observer
                    val posts = outcome.data
                    when (status) {
                        STATUS_LOADING -> {
                            status = STATUS_IDLE
                            this.posts = posts
                            if (safeMode) {
                                postSearchAdapter.updateData(getSafePosts())
                            } else {
                                postSearchAdapter.updateData(posts)
                            }
                            if (newStart && posts.isEmpty()) {
                                newStart = false
                                refresh()
                            }
                        }
                        STATUS_REFRESH -> {
                            status = STATUS_IDLE
                            if (this.posts != posts ) {
                                this.posts = posts
                                if (safeMode) {
                                    postSearchAdapter.updateData(getSafePosts())
                                } else {
                                    postSearchAdapter.updateData(posts)
                                }
                            }
                        }
                        STATUS_LOAD_MORE -> {
                            status = STATUS_IDLE
                            this.posts = posts
                            if (safeMode) {
                                postSearchAdapter.addData(getSafePosts())
                            } else {
                                postSearchAdapter.addData(posts)
                            }
                        }
                    }
                }
                is Outcome.Failure -> {
                    status = STATUS_IDLE
                    disableRefreshLayout()
                    when (outcome.e) {
                        is HttpException -> {
                            val httpException = outcome.e as HttpException
                            val message = httpException.response().message()
                            val code = httpException.response().code()
                            Toast.makeText(this, "code: $code, msg: $message", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            outcome.e.printStackTrace()
                        }
                    }
                    logi(TAG, "Outcome.Failure")
                }
            }})
        postSearchViewModel.isEndOutcome.observe(this, Observer<Outcome<Boolean>> { outcome ->
            when (outcome) {
                is Outcome.Success -> {
                    disableRefreshLayout()
                }
            }
        })
    }

    private fun loadPosts() {
        enableRefreshLayout()
        postSearchViewModel.loadPosts(getHttpUrl())
    }

    private fun getSafePosts(): MutableList<PostSearch> {
        val postsSafe: MutableList<PostSearch> = mutableListOf()
        posts.forEach { post ->
            if (post.rating == "s") {
                postsSafe.add(post)
            }
        }
        return postsSafe
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        appBarLayout = findViewById(R.id.appbar_layout)
        toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        toolbar.setTitle(R.string.posts)
        toolbar.subtitle = keyword
        toolbar.inflateMenu(R.menu.menu_search)
        toolbar.menu.findItem(R.id.action_safe_mode).isChecked = safeMode
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Settings.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
        toolbar.setOnMenuItemClickListener {item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_grid -> {
                    if (!toolbar.menu.findItem(R.id.action_grid).isChecked) {
                        toolbar.menu.findItem(R.id.action_grid).isChecked = true
                        app.settings.gridModeString = Settings.GRID_MODE_GRID
                    }
                }
                R.id.action_staggered_grid -> {
                    if (!toolbar.menu.findItem(R.id.action_staggered_grid).isChecked) {
                        toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
                        app.settings.gridModeString = Settings.GRID_MODE_STAGGERED_GRID
                    }
                }
                R.id.action_safe_mode -> app.settings.safeMode = !app.settings.safeMode
            }
            return@setOnMenuItemClickListener true
        }
        appBarLayout.addView(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            paddingTop = insets.systemWindowInsetTop
            paddingBottom = insets.systemWindowInsetBottom
            appBarLayout.minimumHeight = toolbar.minimumHeight + paddingTop
            toolbar.setPadding(0, paddingTop, 0, 0)
            insets
        }
        toolbar.setNavigationOnClickListener { finish() }
        postSearchView = findViewById(R.id.posts_list)
//        (postSearchView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        postSearchView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
        postSearchView.setItemViewCacheSize(20)
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> {
                val layoutManager = SafeGridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
                postSearchView.setHasFixedSize(true)
                postSearchView.layoutManager = layoutManager
            }
            else -> {
                val layoutManager = SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                postSearchView.setHasFixedSize(false)
                postSearchView.layoutManager = layoutManager
            }
        }
        postSearchAdapter = PostSearchAdapter(this, app.settings.gridModeString)
        postSearchView.adapter = postSearchAdapter
        postSearchView.addOnScrollListener(object : LastItemListener() {
            override fun onLastItemVisible() {
                loadMoreData()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_SETTLING -> GlideApp.with(this@SearchActivity).pauseRequests()
                    RecyclerView.SCROLL_STATE_IDLE -> GlideApp.with(this@SearchActivity).resumeRequests()
                }
            }
        })
        postSearchAdapter.setPostItemClickListener(object : PostSearchAdapter.PostItemClickListener {

            override fun onClickPostItem(position: Int) {
                val intent = Intent(this@SearchActivity, DetailActivity::class.java)
                intent.putExtra("keyword", keyword)
                intent.putExtra("position", position)
                if (user != null) {
                    intent.putExtra("username", user!!.name)
                    intent.putExtra("password_hash", user!!.password_hash)
                } else {
                    intent.putExtra("username", "")
                    intent.putExtra("password_hash", "")
                }
                startActivity(intent)
            }

            override fun onClickRate(position: Int, id: Int, rate: ImageView) {
                voteChangedId = id
                val user = this@SearchActivity.user
                if (user == null) {
                    takeSnackbarShort(this@SearchActivity.postSearchView, "This operation requires a user account.", paddingBottom)
                    return
                }
                val v = layoutInflater.inflate(R.layout.layout_ratingbar, null)
                voteChangedScore = 0
                val scoreTv: TextView = v.findViewById(R.id.score)
                scoreTv.text = "0"
                val ratingBar: RatingBar = v.findViewById(R.id.rating_bar)
                val cancel: Button = v.findViewById(R.id.cancel)
                val vote: Button = v.findViewById(R.id.set_vote)
                ratingBar.setOnRatingBarChangeListener { _, star, _ ->
                    voteChangedScore = star.toInt()
                    scoreTv.text = voteChangedScore.toString()
                }
                val dialog = AlertDialog.Builder(this@SearchActivity)
                        .create()
                dialog.apply {
                    setView(v)
                    setCanceledOnTouchOutside(true)
                    show()
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                vote.setOnClickListener {
                    val url = user.url + "/post/vote.json"
                    voteViewModel.votePost(url, id, voteChangedScore, user.name, user.password_hash)
                    dialog.dismiss()
                }
            }

        })
    }

    private fun initVoteViewModel() {
        voteViewModel.idsOutcomeOneTwo.observe(this, Observer<Outcome<MutableList<Int>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {}
                is Outcome.Success -> {
                    if (voteChangedId > 0 && voteChangedScore == 0) {
                        postSearchView.findViewWithTag<ImageView>(voteChangedId)?.setImageResource(R.drawable.ic_action_star_border_24dp)
                    }
                    val data = outcome.data
                    if (data.size > 0) {
                        logi(TAG, "idsOutcomeOneTwo size: ${data.size}")
                        postSearchAdapter.updateVoteIdsOneTwo(data)
                        if (postSearchAdapter.itemCount > 0) {
                            data.forEach { tag ->
                                postSearchView.findViewWithTag<ImageView>(tag)?.setImageResource(R.drawable.ic_action_star_half_24dp)
                            }
                        }
                    }
                }
                is Outcome.Failure -> {
                    val error = outcome.e
                    when (error) {
                        is HttpException -> {
                            Toast.makeText(this, "code: ${error.code()}, msg: ${error.message()}", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            error.printStackTrace()
                        }
                    }
                }
            }
        })
        voteViewModel.idsOutcomeThree.observe(this, Observer<Outcome<MutableList<Int>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {}
                is Outcome.Success -> {
                    val data = outcome.data
                    if (data.size > 0) {
                        logi(TAG, "idsOutcomeThree size: ${data.size}")
                        postSearchAdapter.updateVoteIdsThree(data)
                        if (postSearchAdapter.itemCount > 0) {
                            data.forEach { tag ->
                                postSearchView.findViewWithTag<ImageView>(tag)?.setImageResource(R.drawable.ic_action_star_24dp)
                            }
                        }
                    }
                }
                is Outcome.Failure -> {
                    val error = outcome.e
                    when (error) {
                        is HttpException -> {
                            Toast.makeText(this, "code: ${error.code()}, msg: ${error.message()}", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            error.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    private fun initRefresh() {
        refreshLayout = findViewById(R.id.refresh)
        refreshLayout.setProgressViewOffset(true, toolbar.minimumHeight, toolbar.minimumHeight + 170)
        refreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange,
                R.color.red
        )
        enableRefreshLayout()
        refreshLayout.setOnRefreshListener {
            if (!this.isNetworkConnected) {
                disableRefreshLayout()
                takeSnackbarShort(refreshLayout, "Network without connection", paddingBottom)
                return@setOnRefreshListener
            }
            if (status == STATUS_IDLE) {
                refresh()
            } else {
                disableRefreshLayout()
            }
        }
    }

    private fun refresh() {
        page = 1
        notiNotMore = true
        status = STATUS_REFRESH
        enableRefreshLayout()
        postSearchViewModel.refreshPosts(getHttpUrl())
    }

    private fun getHttpUrl(): HttpUrl {
        val user = this.user
        var username = ""
        var passwordHash = ""
        if (user != null) {
            username = user.name
            passwordHash = user.password_hash
        }
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileSchema)
                .host(app.settings.activeProfileHost)
                .addPathSegment("post.json")
                .addQueryParameter("limit", limit.toString())
                .addQueryParameter("page", page.toString())
                .addQueryParameter("tags", keyword)
                .addQueryParameter("login", username)
                .addQueryParameter("password_hash", passwordHash)
                .build()
    }

    private fun loadMoreData() {
        if (!this.isNetworkConnected) {
            takeSnackbarShort(refreshLayout, "Network without connection", paddingBottom)
            return
        }
        if (posts.isEmpty() || posts.size < limit) return
        val isNotMore = postSearchViewModel.isNotMore()
        if (!refreshLayout.isRefreshing && status == STATUS_IDLE && !isNotMore) {
            status = STATUS_LOAD_MORE
            enableRefreshLayout()
            page = posts.size/(limit-10) + 1
            postSearchViewModel.loadMorePosts(getHttpUrl())
            logi(TAG, "loadMoreData. page: $page")
        }
        if (isNotMore && notiNotMore) {
            notiNotMore = false
            takeSnackbarShort(refreshLayout, "Not more posts", paddingBottom)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.GRID_MODE -> {
                when (app.settings.gridModeString) {
                    Settings.GRID_MODE_GRID -> {
                        val layoutManager = SafeGridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
                        postSearchView.setHasFixedSize(true)
                        postSearchView.layoutManager = layoutManager
                        postSearchAdapter.setGridMode(Settings.GRID_MODE_GRID)
                        postSearchAdapter.updateData(mutableListOf())
                        if (safeMode) postSearchAdapter.updateData(getSafePosts()) else postSearchAdapter.updateData(posts)
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        val layoutManager = SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                        postSearchView.setHasFixedSize(false)
                        postSearchView.layoutManager = layoutManager
                        postSearchAdapter.setGridMode(Settings.GRID_MODE_STAGGERED_GRID)
                        postSearchAdapter.updateData(mutableListOf())
                        if (safeMode) postSearchAdapter.updateData(getSafePosts()) else postSearchAdapter.updateData(posts)
                    }
                }
            }
            Settings.SAFE_MODE -> {
                safeMode = app.settings.safeMode
                toolbar.menu.findItem(R.id.action_safe_mode).isChecked = safeMode
                if (safeMode) {
                    postSearchAdapter.updateData(getSafePosts())
                } else {
                    postSearchAdapter.updateData(posts)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logi(TAG, "onPause")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        logi(TAG, "onResume")
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        logi(TAG, "onDestroy")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        MoeDH.destroySearchComponent()
    }

    fun getKeyword(): String {
        return keyword
    }
}