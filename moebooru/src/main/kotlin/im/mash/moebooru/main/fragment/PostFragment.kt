package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.App.Companion.coreComponent
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.PostAdapter
import im.mash.moebooru.main.adapter.TagsDrawerAdapter
import im.mash.moebooru.util.screenWidth
import im.mash.moebooru.util.toolbarHeight
import okhttp3.HttpUrl
import java.io.IOException

@SuppressLint("RtlHardcoded")
class PostFragment : ToolbarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "PostFragment"
    }

    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var drawerListener: DrawerLayout.DrawerListener

    private lateinit var postView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private lateinit var refreshLayout: SwipeRefreshLayout

    private var spanCount = 3
    private var page = 1
    private var posts = mutableListOf<Post>()

    private var isLoad = true
    private var isLoadMore = false
    private var isNotMore = false

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_drawer, null) as Toolbar
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        view.setBackgroundColor(ContextCompat.getColor(activity, R.color.window_background))
        initToolbar()
        initRightDrawer(view)
        initDrawerListener()
        spanCount = activity.screenWidth/activity.resources.getDimension(R.dimen.item_width).toInt()
        initRefresh(view)
        initPostList(view)
        coreComponent.sharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }

    private fun initRefresh(view: View) {
        refreshLayout = view.findViewById(R.id.refresh)
        refreshLayout.setProgressViewOffset(true, toolbar.minimumHeight, toolbar.minimumHeight + 150)
        refreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange,
                R.color.red
        )
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            isLoad = true
            isNotMore = false
            page = 1
            (activity as MainActivity).postViewModel.refreshPosts(getHttpUrl())
        }
    }

    private fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileScheme)
                .host(app.settings.activeProfileHost)
                .addPathSegment("post.json")
                .addQueryParameter("limit", app.settings.postLimitInt.toString())
                .addQueryParameter("page", page.toString())
                .build()
    }

    private fun initPostList(view: View) {
        postView = view.findViewById(R.id.posts_list)
        postView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        postView.setItemViewCacheSize(20)
        postView.isDrawingCacheEnabled = true
        postView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> {
                postView.layoutManager = GridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                postView.setHasFixedSize(true)
            }
            else -> {
                postView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                postView.setHasFixedSize(false)
            }
        }
        postAdapter = PostAdapter(this.requireContext())
        postView.adapter = postAdapter

        val activity = activity as MainActivity

        activity.postViewModel.postsOutcome.observe(this, Observer<Outcome<MutableList<Post>>> { outcome: Outcome<MutableList<Post>>? ->
            when (outcome) {
                is Outcome.Progress -> {
                    Log.i(TAG, "postViewModel Outcome.Progress")
                }
                is Outcome.Success -> {
                    Log.i(TAG, "postViewModel Outcome.Success")
                    refreshLayout.isRefreshing = false
                    val data = outcome.data
                    if (isLoad) {
                        isLoad = false
                        if ((posts.size > 0 && data[0].id > posts[0].id ) || posts.size != data.size) {
                            posts = data
                            postAdapter.updateData(posts)
                        }
                        isLoad = false
                    } else if (isLoadMore) {
                        if (data.size > 0) {
                            posts.addAll(data)
                            postAdapter.addData(posts)
                        } else {
                            isNotMore = true
                        }
                    }
                }
                is Outcome.Failure -> {
                    refreshLayout.isRefreshing = false
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    } else {
                        Log.i(TAG, "postViewModel Outcome.Failure")
                    }
                }
            }
        })

        refreshLayout.isRefreshing = true
        activity.postViewModel.loadPosts(getHttpUrl())
//
//        activity.tagViewModel.tagOutcome.observe(this, Observer<Outcome<MutableList<Tag>>> { outcome: Outcome<MutableList<Tag>>? ->
//            when (outcome) {
//                is Outcome.Progress -> {
//                    Log.i(TAG, "tagViewModel Outcome.Progress")
//                }
//                is Outcome.Success -> {
//                    Log.i(TAG, "tagViewModel Outcome.Success")
//                }
//                is Outcome.Failure -> {
//                    if (outcome.e is IOException) {
//                        outcome.e.printStackTrace()
//                    } else {
//                        Log.i(TAG, "tagViewModel Outcome.Failure")
//                    }
//                }
//            }
//        })
//
//        activity.tagViewModel.loadTags(httpUrl.host())
    }

    private fun initDrawerListener() {
        drawerListener = object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                closeRightDrawer()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }
        }
        (activity as MainActivity).drawer.drawerLayout.addDrawerListener(drawerListener)
    }

    private fun initToolbar() {
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.transparent))
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_grid -> app.settings.gridModeString = Settings.GRID_MODE_GRID
                R.id.action_staggered_grid -> app.settings.gridModeString = Settings.GRID_MODE_STAGGERED_GRID
                R.id.action_search_open -> openRightDrawer()
            }
            return@setOnMenuItemClickListener true
        }
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Settings.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
    }

    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_posts)
        drawer = view.findViewById(R.id.right_drawer_view)
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_white_24dp)
        drawerToolbar.inflateMenu(R.menu.menu_search)
        drawerToolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_add -> {}
                R.id.action_search -> {}
            }
            return@setOnMenuItemClickListener true
        }
        val drawerRecyclerView = view.findViewById<RecyclerView>(R.id.drawer_rv_list)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this.requireContext(),
                LinearLayoutManager.VERTICAL, false)
        val drawerAdapter = TagsDrawerAdapter(this.requireContext())
        drawerRecyclerView.adapter = drawerAdapter
        val drawerViewLayout: LinearLayout = view.findViewById(R.id.drawer_list_layout)
        val drawerAppBarLayout: AppBarLayout = view.findViewById(R.id.appbar_layout_drawer)
        drawerAppBarLayout.addView(drawerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) {_, insets ->
            val paddingTop = insets.systemWindowInsetTop
            val paddingBottom = insets.systemWindowInsetBottom
            val toolbarHeight = this.requireContext().toolbarHeight
            drawerViewLayout.setPadding(0, 0, 0, paddingBottom)
            drawerToolbar.setPadding(0, paddingTop, 0, 0)
            drawerAppBarLayout.minimumHeight = paddingTop + toolbarHeight
            drawerAppBarLayout.removeView(drawerToolbar)
            drawerAppBarLayout.addView(drawerToolbar)
            insets
        }

    }

    private fun closeRightDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT)
        }
    }

    private fun openRightDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.openDrawer(Gravity.RIGHT)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.GRID_MODE -> {
                when (app.settings.gridModeString) {
                    Settings.GRID_MODE_GRID -> {
                        toolbar.menu.findItem(R.id.action_grid).isChecked = true
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).drawer.drawerLayout.removeDrawerListener(drawerListener)
        coreComponent.sharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }
}