/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.view.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.JsonParseException

import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.models.ParamGet
import im.mash.moebooru.models.RawPost
import im.mash.moebooru.network.MoeHttpClient
import im.mash.moebooru.network.MoeResponse
import im.mash.moebooru.ui.adapter.PostsAdapter
import im.mash.moebooru.ui.adapter.TagsDrawerAdapter
import im.mash.moebooru.ui.listener.LastItemListener
import im.mash.moebooru.ui.listener.RecyclerViewClickListener
import im.mash.moebooru.utils.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

@SuppressLint("RtlHardcoded")
class PostsFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener, View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, SwipeRefreshLayout.OnRefreshListener,
        DrawerLayout.DrawerListener {

    private val TAG = this.javaClass.simpleName

    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var tagsDrawerAdapter: TagsDrawerAdapter
    private lateinit var tagsDrawerView: RecyclerView
    private lateinit var postsView: RecyclerView
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var lastItemListener: LastItemListener

    private var currentGridMode: String = Key.GRID_MODE_STAGGERED_GRID

    private var width: Int = 0
    private var spanCount: Int = 1
    private var itemPadding: Int = 0
    //工具栏高度
    private var toolbarHeight = 0
    //当前页数
    private var page = 1
    //items data
    private var items: MutableList<RawPost>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))

        //init toolbar
        toolbarLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.transparent))
        toolbar.setOnMenuItemClickListener(this)
        setToolbarGridOption()

        //计算窗口宽度
        val activity = activity as MainActivity
        val metric: DisplayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels

        //计算列数
        spanCount = width/activity.resources.getDimension(R.dimen.item_width).toInt()
        app.settings.spanCountInt = spanCount

        //item 边距
        itemPadding = activity.resources.getDimension(R.dimen.item_padding).toInt()

        //第一行 item 要加上 toolbar 的高度
        val tv = TypedValue()
        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }

        //监听 MainActivity 的左侧抽屉
        activity.drawer.drawerLayout.addDrawerListener(this)

        //SwipeRefreshLayout
        refresh = view.findViewById(R.id.refresh)
        refresh.setOnRefreshListener(this)

        //右侧搜索抽屉
        initRightDrawer(view)

        //init Adapter
        postsAdapter = PostsAdapter(this.context!!, itemPadding, toolbarHeight + app.settings.statusBarHeightInt,null)

        //init RecyclerView listener
        app.settings.isNotMoreData = false
        lastItemListener = object : LastItemListener() {
            override fun onLastItemVisible() {
                if (!refresh.isRefreshing && !app.settings.isNotMoreData) {
                    loadMoreData()
                }
            }
        }

        //init RecyclerView
        initPostsView(view)

        //监听设置变化
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        sp.registerOnSharedPreferenceChangeListener(this)

        if (savedInstanceState == null) {
            loadData()
            Log.i(TAG, "savedInstanceState == null, loadCacheData()")
        }
    }

    private fun loadData() {
        doAsync {
            items = app.postsManager.loadPosts(app.settings.activeProfile)
            uiThread {
                if (items != null && items!!.size > 0) {
                    postsAdapter.updateData(items)
                } else {
                    refreshData()
                }
            }
        }
    }

    //初始右侧化抽屉
    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_posts)
        drawer = view.findViewById(R.id.tags_drawer_view)
        drawerToolbar = view.findViewById(R.id.toolbar_tags)
        drawerToolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar))
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_24dp)
        drawerToolbar.inflateMenu(R.menu.menu_search)
        drawerToolbar.setOnMenuItemClickListener(this)
        drawerToolbar.setOnClickListener(this)
        ViewCompat.setOnApplyWindowInsetsListener(drawer) { _, insets ->
            drawer.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }

        val itemsTag = mutableListOf(
                "item1",
                "item2",
                "item3",
                "item4",
                "item5",
                "item6",
                "item7",
                "item8",
                "item1",
                "item2",
                "item3",
                "item4",
                "item5",
                "item6",
                "item7",
                "item8",
                "item1",
                "item2",
                "item3",
                "item4",
                "item5",
                "item6",
                "item7",
                "item8"
        )
        tagsDrawerAdapter = TagsDrawerAdapter(this.requireContext(), itemsTag)
        tagsDrawerView = view.findViewById(R.id.search_tags_list)
        tagsDrawerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        tagsDrawerView.adapter = tagsDrawerAdapter
        val tagsLayout: LinearLayout = view.findViewById(R.id.drawer_search_tags_layout)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { _, insets ->
            tagsLayout.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
    }

    //初始化 PostsView
    private fun initPostsView(view: View) {
        postsView = view.findViewById(R.id.posts_list)
        setupGridMode()
        //动画
        postsView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        postsView.itemAnimator = DefaultItemAnimator()

        postsView.setItemViewCacheSize(20)
        postsView.isDrawingCacheEnabled = true
        postsView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        postsView.adapter = postsAdapter

        //监听是否滑动到最后一个 item
        postsView.addOnScrollListener(lastItemListener)

        //item 点击监听
        val postsItemClickListener = object : RecyclerViewClickListener.OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                Log.i(TAG, "onItemClick: $position")
                val intent = Intent()
                intent.action = "im.mash.moebooru.details"
                val bundle = Bundle()
                bundle.putInt(Key.ITEM_POS, position)
                bundle.putInt(Key.ITEM_ID, items!![position].id!!.toInt())
                intent.putExtra(Key.BUNDLE, bundle)
                startActivity(intent)
            }

            override fun onItemLongClick(itemView: View?, position: Int) {
                Log.i(TAG, "onItemLongClick: $position")
            }
        }
        val postsViewItemTouchListener = RecyclerViewClickListener(this.requireContext(), postsItemClickListener)
        postsView.addOnItemTouchListener(postsViewItemTouchListener)

        var isScrolling = false
        val postsViewScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (isScrolling) {
                            GlideApp.with(this@PostsFragment.context!!).resumeRequests()
                        }
                        isScrolling = false
                    }
                    RecyclerView.SCROLL_STATE_SETTLING xor  RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isScrolling = true
                        GlideApp.with(this@PostsFragment.context!!).pauseAllRequests()
                    }
                }
            }
        }
        postsView.addOnScrollListener(postsViewScrollListener)

    }

    override fun onDrawerOpened(drawerView: View) {

    }

    override fun onDrawerClosed(drawerView: View) {

    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

    }

    override fun onDrawerStateChanged(newState: Int) {
        //左侧抽屉状态变化时关闭右侧抽屉
        closeRightDrawer()
    }

    // 设置 PostsView 网格模式
    private fun setupGridMode() {
        currentGridMode = app.settings.gridModeString
        when (currentGridMode) {
            Key.GRID_MODE_GRID -> {
                postsView.layoutManager = GridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                postsView.setHasFixedSize(true)
            }
            else -> {
                currentGridMode = Key.GRID_MODE_STAGGERED_GRID
                postsView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                postsView.setHasFixedSize(false)
            }
        }
    }

    // 设置变化 重新设置 PostsView 网格模式
    private fun reSetupGridMode() {
        if (app.settings.gridModeString != currentGridMode) {
            setupGridMode()
            postsAdapter.updateData(items)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_grid -> app.settings.gridModeString = Key.GRID_MODE_GRID
            R.id.action_staggered_grid -> app.settings.gridModeString = Key.GRID_MODE_STAGGERED_GRID
            R.id.action_search_open -> openRightDrawer()
            R.id.action_search -> closeRightDrawer()
        }
        return true
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
            Key.GRID_MODE -> {
                setToolbarGridOption()
                reSetupGridMode()
            }
            Key.ACTIVE_PROFILE -> {
                closeRightDrawer()
                app.settings.isNotMoreData = false
                loadData()
            }
        }
    }

    //设置 Toolbar 网格选项
    private fun setToolbarGridOption() {
        when (app.settings.gridModeString) {
            Key.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Key.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            drawerToolbar -> closeRightDrawer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        closeRightDrawer()
    }

    override fun onPause() {
        super.onPause()
//        closeRightDrawer()
    }

    override fun onResume() {
        super.onResume()
//        reSetupGridMode()
        loadData()
    }

    override fun onBackPressed(): Boolean {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT)
            return true
        }
        return super.onBackPressed()
    }

    private fun loadMoreData() {
        refresh.isRefreshing = true
        page = postsAdapter.itemCount/app.settings.postLimitInt + 1
        Log.i(TAG, "Loading more data...")
        doAsync {
            val response: MoeResponse? = getResponseData()
            var result: MutableList<RawPost>? = null
            try {
                result = Gson().fromJson<MutableList<RawPost>>(response?.getResponseAsString().toString())
            } catch (e: JsonParseException) {
                Log.i(TAG, "Gson exception!")
            }
            if (result != null && result.size > 0) {
                if (result.size < app.settings.postLimitInt) {
                    app.settings.isNotMoreData = true
                }
                app.postsManager.savePosts(result, app.settings.activeProfile)
                if (items == null) {
                    items = result
                } else {
                    result.forEach {
                        items!!.add(it)
                    }
                }
            } else {
                app.settings.isNotMoreData = true
            }
            uiThread {
                refresh.isRefreshing = false
                Log.i(TAG, "Load more data finished!")
                if (items != null) {
                    postsAdapter.addData(items)
                } else {
                    Log.i(TAG, "Not data")
                }
            }
        }
    }

    private fun getResponseData(): MoeResponse? {
        val siteUrl = app.boorusManager.getBooru(app.settings.activeProfile).url
        Log.i(TAG, "siteUrl: $siteUrl")
        val limit = app.settings.postLimitInt
        val url = ParamGet(siteUrl, page.toString(), limit.toString(), null,
                "rating:safe", null, null, null).makeGetUrl()
        Log.i(TAG, "url: $url")
        return MoeHttpClient.instance.get(url, null, okHttpHeader)
    }

    private fun refreshData() {
        refresh.isRefreshing = true
        app.settings.isNotMoreData = false
        page = 1
        doAsync {
            val response: MoeResponse? = getResponseData()
            var result: MutableList<RawPost>? = null
            try {
                result = Gson().fromJson<MutableList<RawPost>>(response?.getResponseAsString().toString())
            } catch (e: JsonParseException) {
                Log.i(TAG, "Gson exception!")
            }
            if (result != null) {
                app.postsManager.deletePosts(app.settings.activeProfile)
                app.postsManager.savePosts(result, app.settings.activeProfile)
            }
            uiThread {
                refresh.isRefreshing = false
                Log.i(TAG, "Refresh data finished!")
                if (result != null) {
                    items = result
                    postsAdapter.updateData(items)
                } else {
                    Log.i(TAG, "Not data")
                }
            }
        }
    }

    override fun onRefresh() {
        Log.i(TAG, "Refreshing!!")
        refreshData()
    }
}