package im.mash.moebooru.search

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.base.LastItemListener
import im.mash.moebooru.common.base.RecyclerViewClickListener
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.application.SlidingActivity
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.search.adapter.PostSearchAdapter
import im.mash.moebooru.search.viewmodel.PostSearchViewModel
import im.mash.moebooru.search.viewmodel.PostSearchViewModelFactory
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.screenWidth
import okhttp3.HttpUrl
import java.io.IOException
import javax.inject.Inject

class SearchActivity : SlidingActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "SearchActivity"
    }
    private var paddingBottom = 0
    private var paddingTop = 0

    private var spanCount = 3
    private var page = 1
    private var posts = mutableListOf<PostSearch>()

    private var refreshing = false
    private var loadingMore = false
    private var notiNotMore = true
    private var limit = 50
    private var keyword = ""
    private var safeMode = true
    private var newStart = true

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

    private val postSearchViewModel: PostSearchViewModel by lazy {
        this.getViewModel<PostSearchViewModel>(searchViewModelFactory)
    }

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
        initRefresh()
        observePosts()
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
        loadPosts()
    }

    private fun observePosts() {
        postSearchViewModel.postsSearchOutcome.observe( this,
                Observer<Outcome<MutableList<PostSearch>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {
                    logi(TAG, "Outcome.Progress")
                }
                is Outcome.Success -> {
                    posts = outcome.data
                    if (loadingMore) {
                        if (safeMode) {
                            postSearchAdapter.addData(getSafePosts())
                        } else {
                            postSearchAdapter.addData(posts)
                        }
                        loadingMore = false
                    } else {
                        if (safeMode) {
                            postSearchAdapter.updateData(getSafePosts())
                        } else {
                            postSearchAdapter.updateData(posts)
                        }
                        refreshing = false
                        if (newStart && posts.size == 0) {
                            newStart = false
                            refresh()
                        }
                    }
                    refreshLayout.isRefreshing = false
                    logi(TAG, "Outcome.Success. data.size: ${posts.size}")
                }
                is Outcome.Failure -> {
                    refreshLayout.isRefreshing = false
                    loadingMore = false
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    logi(TAG, "Outcome.Failure")
                }
            }})
    }

    private fun loadPosts() {
        refreshLayout.isRefreshing = true
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
        postSearchView.itemAnimator = DefaultItemAnimator()
        postSearchView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
        postSearchView.setItemViewCacheSize(20)
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> {
                val layoutManager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
                postSearchView.layoutManager = layoutManager
                postSearchView.setHasFixedSize(true)
            }
            else -> {
                val layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                postSearchView.layoutManager = layoutManager
                postSearchView.setHasFixedSize(false)
            }
        }
        postSearchAdapter = PostSearchAdapter(this, app.settings.gridModeString)
        postSearchView.adapter = postSearchAdapter
        postSearchView.addOnScrollListener(object : LastItemListener() {
            override fun onLastItemVisible() {
                loadMoreData()
            }
        })
        postSearchView.addOnItemTouchListener(RecyclerViewClickListener(this,
                object : RecyclerViewClickListener.OnItemClickListener {
                    override fun onItemClick(itemView: View?, position: Int) {
                        val intent = Intent(this@SearchActivity, DetailActivity::class.java)
                        intent.putExtra("tags", keyword)
                        intent.putExtra("position", position)
                        startActivity(intent)
                    }

                    override fun onItemLongClick(itemView: View?, position: Int) {
                        logi(TAG, "Long click item: $position")
                    }
                }))
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
        refreshLayout.isRefreshing = true
        refreshLayout.setOnRefreshListener {
            if (!loadingMore && !refreshing) {
                refresh()
            }
        }
    }

    private fun refresh() {
        refreshLayout.isRefreshing = true
        notiNotMore = true
        page = 1
        refreshing = true
        postSearchViewModel.refreshPosts(getHttpUrl())
    }

    private fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileScheme)
                .host(app.settings.activeProfileHost)
                .addPathSegment("post.json")
                .addQueryParameter("limit", limit.toString())
                .addQueryParameter("page", page.toString())
                .addQueryParameter("tags", keyword)
                .build()
    }

    private fun loadMoreData() {
        val isNotMore = postSearchViewModel.isNotMore()
        if (!refreshLayout.isRefreshing && !loadingMore && !isNotMore) {
            loadingMore = true
            page = posts.size/(limit-1) + 1
            logi(TAG, "loadMoreData. page: $page")
            postSearchViewModel.loadMorePosts(getHttpUrl())
        }
        if (isNotMore && notiNotMore) {
            notiNotMore = false
            val snackbar: Snackbar = Snackbar.make(refreshLayout, "Not more data.", Snackbar.LENGTH_SHORT)
            snackbar.view.setPadding(0, 0, 0, paddingBottom)
            snackbar.show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.GRID_MODE -> {
                when (app.settings.gridModeString) {
                    Settings.GRID_MODE_GRID -> {
                        postSearchView.layoutManager = GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
                        postSearchView.setHasFixedSize(true)
                        postSearchAdapter.setGridMode(Settings.GRID_MODE_GRID)
                        postSearchAdapter.updateData(mutableListOf())
                        if (safeMode) postSearchAdapter.updateData(getSafePosts()) else postSearchAdapter.updateData(posts)
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        postSearchView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                        postSearchView.setHasFixedSize(false)
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