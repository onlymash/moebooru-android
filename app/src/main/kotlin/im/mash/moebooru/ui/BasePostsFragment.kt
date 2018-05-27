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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonParseException
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.model.ParamGet
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.network.MoeHttpClient
import im.mash.moebooru.network.MoeResponse
import im.mash.moebooru.ui.adapter.PostsAdapter
import im.mash.moebooru.ui.listener.LastItemListener
import im.mash.moebooru.ui.listener.RecyclerViewClickListener
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.TableType
import im.mash.moebooru.utils.fromJson
import im.mash.moebooru.utils.okHttpHeader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

open class BasePostsFragment : ToolbarFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val TAG = this.javaClass.simpleName

    internal lateinit var toolbar: Toolbar
    internal lateinit var postsView: RecyclerView
    internal lateinit var postsAdapter: PostsAdapter
    internal lateinit var refreshLayout: SwipeRefreshLayout
    internal lateinit var lastItemListener: LastItemListener
    internal lateinit var context: Context
    internal lateinit var type: String

    internal var currentGridMode: String = Key.GRID_MODE_STAGGERED_GRID

    internal var spanCount: Int = 1
    internal var itemPadding: Int = 0
    //当前页数
    internal var page = 1
    //items data
    internal var items: MutableList<RawPost>? = null

    internal var tags: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))
    }
    //设置 Toolbar 网格选项
    internal fun setToolbarGridOption() {
        when (app.settings.gridModeString) {
            Key.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Key.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
    }
    internal fun setSwipeRefreshLayout(refreshLayout: SwipeRefreshLayout, toolbarHeight: Int) {
        refreshLayout.setProgressViewOffset(true, toolbarHeight, toolbarHeight + 150)
        refreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange,
                R.color.red
        )
    }
    //初始化 PostsView
    internal fun initPostsView(view: View) {
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
        app.settings.isNotMoreData = false
        lastItemListener = object : LastItemListener() {
            override fun onLastItemVisible() {
                if (!refreshLayout.isRefreshing && !app.settings.isNotMoreData) {
                    loadMoreData()
                }
            }
        }
        postsView.addOnScrollListener(lastItemListener)

        //item 点击监听
        val postsItemClickListener = object : RecyclerViewClickListener.OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                val intent =  Intent(context, DetailsActivity().javaClass)
                val bundle = Bundle()
                bundle.putInt(Key.ITEM_POS, position)
                bundle.putInt(Key.ITEM_ID, items!![position].id!!.toInt())
                bundle.putString(Key.TYPE, type)
                if (type == TableType.SEARCH) {
                    bundle.putString(Key.TAGS_SEARCH, tags)
                }
                intent.putExtra(Key.BUNDLE, bundle)
                startActivity(intent)
            }

            override fun onItemLongClick(itemView: View?, position: Int) {

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
                            GlideApp.with(context).resumeRequests()
                        }
                        isScrolling = false
                    }
                    RecyclerView.SCROLL_STATE_SETTLING xor  RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isScrolling = true
                        GlideApp.with(context).pauseAllRequests()
                    }
                }
            }
        }
        postsView.addOnScrollListener(postsViewScrollListener)

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
    internal fun reSetupGridMode() {
        if (app.settings.gridModeString != currentGridMode) {
            setupGridMode()
            postsAdapter.updateData(items)
        }
    }

    private fun getResponseData(): MoeResponse? {
        var response: MoeResponse? = null
        val siteUrl = try {
            app.boorusManager.getBooru(app.settings.activeProfile).url
        } catch (e: Exception) {
            null
        }
        if (siteUrl != null) {
            val limit = app.settings.postLimitInt
            val url = ParamGet(siteUrl, page.toString(), limit.toString(), null,
                    tags, null, null, null).makeGetUrl()
            response = MoeHttpClient.instance.get(url, null, okHttpHeader)
        }
        return response
    }

    internal fun loadMoreData() {
        refreshLayout.isRefreshing = true
        page = postsAdapter.itemCount/ app.settings.postLimitInt + 1
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
                when (type) {
                    TableType.POSTS -> {
                        app.postsManager.savePosts(result, app.settings.activeProfile)
                    }
                    TableType.SEARCH -> {
                        if (tags != null) app.searchManager.savePosts(result, app.settings.activeProfile, tags!!)
                    }
                }
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
                refreshLayout.isRefreshing = false
                Log.i(TAG, "Load more data finished!")
                if (items != null) {
                    postsAdapter.addData(items)
                } else {
                    Log.i(TAG, "Not data")
                }
            }
        }
    }

    internal fun loadData() {
        doAsync {
            try {
                when (type) {
                    TableType.SEARCH -> {
                        if (tags != null) {
                            Log.i(TAG, "Tags: $tags")
                            items = app.searchManager.loadPosts(app.settings.activeProfile, tags!!)
                            if (items != null) {
                                Log.i(TAG, "items.size: ${items!!.size}")
                            }
                        }
                    }
                    else -> {
                        items = app.postsManager.loadPosts(app.settings.activeProfile)
                    }
                }
            } catch (e: Exception) {
                items = null
                Log.i(TAG, "items = null")
            }
            if (items == null) {
                Log.i(TAG, "$type loadData(): items == null")
            }
            uiThread {
                if (items != null && items!!.size > 0) {
                    postsAdapter.updateData(items)
                } else {
                    refreshData()
                }
            }
        }
    }

    override fun onRefresh() {
        Log.i(TAG, "Refreshing!!")
        refreshData()
    }

    internal fun refreshData() {
        refreshLayout.isRefreshing = true
        app.settings.isNotMoreData = false
        page = 1
        doAsync {
            val response: MoeResponse? = getResponseData()
            var result: MutableList<RawPost>? = null
            if (response != null) {
                try {
                    result = Gson().fromJson<MutableList<RawPost>>(response.getResponseAsString().toString())
                } catch (e: JsonParseException) {
                    result = null
                    Log.i(TAG, "Gson exception! response == " + response.getResponseAsString().toString())
                } finally {
                    if (result != null) {
                        when (type) {
                            TableType.POSTS -> {
                                try {
                                    app.postsManager.deletePosts(app.settings.activeProfile)
                                } catch (e: Exception) {
                                    Log.i(TAG, "Delete posts failed!!")
                                }
                                try {
                                    app.postsManager.savePosts(result, app.settings.activeProfile)
                                } catch (e: Exception) {
                                    Log.i(TAG, "Save posts failed!!")
                                }
                            }
                            TableType.SEARCH -> {
                                if (tags != null) {
                                    try {
                                        app.searchManager.deletePosts(app.settings.activeProfile, tags!!)
                                    } catch (e: Exception) {
                                        Log.i(TAG, "Delete search posts failed!!")
                                    }
                                    try {
                                        app.searchManager.savePosts(result, app.settings.activeProfile, tags!!)
                                    } catch (e: Exception) {
                                        Log.i(TAG, "Save search posts failed!!")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            uiThread {
                refreshLayout.isRefreshing = false
                if (result != null) {
                    items = result
                    postsAdapter.updateData(items)
                } else {
                    Toast.makeText(context, "Not data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}