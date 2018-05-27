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
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.*
import android.view.*
import android.util.Log
import android.widget.LinearLayout

import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.ui.adapter.PostsAdapter
import im.mash.moebooru.ui.adapter.TagsDrawerAdapter
import im.mash.moebooru.utils.*

@SuppressLint("RtlHardcoded")
class PostsFragment : BasePostsFragment(), Toolbar.OnMenuItemClickListener, View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        DrawerLayout.DrawerListener {

    private val TAG = this.javaClass.simpleName

    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var appBarLayoutTags: AppBarLayout
    private lateinit var tagsDrawerAdapter: TagsDrawerAdapter
    private lateinit var tagsDrawerView: RecyclerView

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar = inflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_tags, null) as Toolbar
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = this.requireContext()
        type = TableType.POSTS
        //init toolbar
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener(this)
        setToolbarGridOption()
        setInsetsListener(toolbar)

        //计算列数
        spanCount = mainActivity.widthScreen/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        app.settings.spanCountInt = spanCount

        //item 边距
        itemPadding = mainActivity.resources.getDimension(R.dimen.item_padding).toInt()

        //监听 MainActivity 的左侧抽屉
        mainActivity.drawer.drawerLayout.addDrawerListener(this)

        //SwipeRefreshLayout
        refreshLayout = view.findViewById(R.id.refresh)
        setSwipeRefreshLayout(refreshLayout, mainActivity.toolbarHeight)
        refreshLayout.setOnRefreshListener(this)

        //右侧搜索抽屉
        initRightDrawer(view)

        //init Adapter
        postsAdapter = PostsAdapter(this.requireContext(), itemPadding,
                mainActivity.toolbarHeight + app.settings.statusBarHeightInt,null)

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

    //初始右侧化抽屉
    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_posts)
        drawer = view.findViewById(R.id.tags_drawer_view)
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_24dp)
        drawerToolbar.inflateMenu(R.menu.menu_search)
        drawerToolbar.setOnMenuItemClickListener(this)
        drawerToolbar.setOnClickListener(this)

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
        tagsDrawerView = view.findViewById(R.id.rv_tags_list)
        tagsDrawerView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        tagsDrawerView.adapter = tagsDrawerAdapter
        val tagsDrawerViewLayout = view.findViewById<LinearLayout>(R.id.rv_tags_list_layout)
        appBarLayoutTags = view.findViewById(R.id.appbar_layout_tags)
        appBarLayoutTags.addView(drawerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { _, insets ->
            val statusBarSize = insets.systemWindowInsetTop
            tagsDrawerViewLayout.setPadding(0, mainActivity.toolbarHeight + statusBarSize, 0, insets.systemWindowInsetBottom)
            drawerToolbar.setPadding(0, statusBarSize, 0, 0)
            appBarLayoutTags.minimumHeight = mainActivity.toolbarHeight + statusBarSize
            appBarLayoutTags.removeView(drawerToolbar)
            appBarLayoutTags.addView(drawerToolbar)
            insets
        }
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


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_grid -> app.settings.gridModeString = Key.GRID_MODE_GRID
            R.id.action_staggered_grid -> app.settings.gridModeString = Key.GRID_MODE_STAGGERED_GRID
            R.id.action_search_open -> openRightDrawer()
            R.id.action_search -> {
                val intent = Intent(activity, SearchActivity().javaClass)
                val bundle = Bundle()
                bundle.putString(Key.TAGS_SEARCH, "yuri")
                intent.putExtra(Key.BUNDLE, bundle)
                startActivity(intent)
                closeRightDrawer()
            }
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
                postsAdapter.updateData(null)
                loadData()
            }
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
}