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
        setContentView(R.layout.layout_posts_content)
        component.inject(this)
        val key = intent.getStringExtra("keyword")
        if ( key == null) {
            finish()
        }
        keyword = key
        if (keyword == "") finish()
        spanCount = this.screenWidth/resources.getDimension(R.dimen.item_width).toInt()
        limit = app.settings.postLimitInt
        logi(TAG, keyword)
        initView()
        initRefresh()
        observePosts()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun observePosts() {
        postSearchViewModel.postsSearchOutcome.observe( this,
                Observer<Outcome<MutableList<PostSearch>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {
                    if (!refreshLayout.isRefreshing) refreshLayout.isRefreshing = true
                    logi(TAG, "postViewModel Outcome.Progress")
                }
                is Outcome.Success -> {
                    refreshLayout.isRefreshing = false
                    val data = outcome.data
                    logi(TAG, "postViewModel Outcome.Success. data.size: ${data.size}")
                    if (loadingMore) {
                        posts = data
                        postSearchAdapter.addData(posts)
                        loadingMore = false
                    } else {
                        posts = data
                        postSearchAdapter.updateData(posts)
                        refreshing = false
                    }
                }
                is Outcome.Failure -> {
                    refreshLayout.isRefreshing = false
                    loadingMore = false
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    logi(TAG, "postViewModel Outcome.Failure")
                }
            }
        })
        postSearchViewModel.loadPosts(getHttpUrl())
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        appBarLayout = findViewById(R.id.appbar_layout)
        toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        toolbar.inflateMenu(R.menu.menu_search)
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
            }
            return@setOnMenuItemClickListener true
        }
        toolbar.setTitle(R.string.posts)
        toolbar.subtitle = keyword
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
        postSearchView.isDrawingCacheEnabled = true
        postSearchView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
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
        refreshLayout.setOnRefreshListener {
            if (!loadingMore && !refreshing) {
                notiNotMore = true
                page = 1
                refreshing = true
                postSearchViewModel.refreshPosts(getHttpUrl())
            }
        }
        refreshLayout.isRefreshing = true
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
            refreshLayout.isRefreshing = true
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
                        postSearchAdapter.updateData(posts)
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        postSearchView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                        postSearchView.setHasFixedSize(false)
                        postSearchAdapter.setGridMode(Settings.GRID_MODE_STAGGERED_GRID)
                        postSearchAdapter.updateData(mutableListOf())
                        postSearchAdapter.updateData(posts)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        MoeDH.destroySearchComponent()
    }
}