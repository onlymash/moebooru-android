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
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.ui.adapter.PostsAdapter
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.TableType
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SearchFragment : BasePostsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = this.javaClass.simpleName

    private val searchActivity by lazy { this.activity as SearchActivity }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar = inflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        return inflater.inflate(R.layout.layout_posts_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = this.requireContext()
        type = TableType.SEARCH
        toolbar.setTitle(R.string.posts)
        searchActivity.setActionBar(toolbar)
        setInsetsListener(toolbar)
        val bundle = arguments
        if (bundle != null) {
            tags = bundle.getString(Key.TAGS_SEARCH)
            if (tags != null) toolbar.subtitle = tags
        }
        //SwipeRefreshLayout
        refreshLayout = view.findViewById(R.id.refresh)
        setSwipeRefreshLayout(refreshLayout, searchActivity.toolbarHeight)
        refreshLayout.setOnRefreshListener(this)

        //计算列数
        spanCount = searchActivity.widthScreen/searchActivity.resources.getDimension(R.dimen.item_width).toInt()
        app.settings.spanCountInt = spanCount

        //item 边距
        itemPadding = searchActivity.resources.getDimension(R.dimen.item_padding).toInt()

        //init Adapter
        postsAdapter = PostsAdapter(this.requireContext(), itemPadding,
                searchActivity.toolbarHeight + app.settings.statusBarHeightInt,null)

        //init RecyclerView
        initPostsView(view)

        //监听设置变化
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        sp.registerOnSharedPreferenceChangeListener(this)

        postsViewModel!!.getPostsModel(tags).observe(this, Observer {
            val posts = postsViewModel!!.getPosts(tags)
            if (posts != null && items !=null && posts.size > items!!.size) {
                //加载更多
                items = posts
                postsAdapter.addData(items)
            } else {
                items = posts
                postsAdapter.updateData(posts)
            }
        })

        initData()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Key.GRID_MODE -> {
                reSetupGridMode()
            }
            Key.ACTIVE_PROFILE -> {
                app.settings.isNotMoreData = false
                postsAdapter.updateData(null)
                initData()
            }
        }
    }
}