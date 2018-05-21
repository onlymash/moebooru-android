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
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.*
import android.view.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.animation.AnimationUtils

import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.glide.GetUrl
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.network.MoeHttpClient
import im.mash.moebooru.network.MoeResponse
import im.mash.moebooru.ui.widget.FixedImageView
import im.mash.moebooru.utils.Key

@SuppressLint("RtlHardcoded")
class PostsFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener,
        View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = this.javaClass.simpleName

    private lateinit var drawer: Drawer
    private lateinit var drawerView: View
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var postsView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private var currentGridMode: String = Key.GRID_MODE_STAGGERED_GRID

    private var metric: DisplayMetrics = DisplayMetrics()
    private var width: Int = 0
    private var spanCount: Int = 1
    private var itemPadding: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerView = inflater.inflate(R.layout.layout_drawer_posts, container, false)
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))

        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.post_toolbar))
        toolbar.setOnMenuItemClickListener(this)
        setGridItemOption()

        val activity = activity!!
        activity.windowManager.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels

        itemPadding = activity.resources.getDimension(R.dimen.item_padding).toInt()

        drawer = DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withDrawerGravity(Gravity.RIGHT)
                .withDisplayBelowStatusBar(true)
                .withRootView(R.id.fragment_main)
                .withCustomView(drawerView)
                .withSavedInstance(savedInstanceState)
                .withDrawerWidthPx((width*0.75F).toInt())
                .withActionBarDrawerToggle(false)
                .buildForFragment()

        drawerLayout = drawer.drawerLayout
        drawerToolbar = drawerView.findViewById(R.id.toolbar_drawer_posts)
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_24dp)
        drawerToolbar.inflateMenu(R.menu.menu_search)
        drawerToolbar.setOnMenuItemClickListener(this)
        drawerToolbar.setOnClickListener(this)

        //init Adapter
        val tv = TypedValue()
        var toolbarHeight = 0
        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        postAdapter = PostAdapter(toolbarHeight, itemPadding)

        //init RecyclerView
        postsView = view.findViewById(R.id.posts_list)
        spanCount = width/this.requireContext().resources.getDimension(R.dimen.item_width).toInt()
        Settings.spanCountInt = spanCount
        currentGridMode = Settings.gridModeString
        setupGridMode()
        postsView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        postsView.itemAnimator = DefaultItemAnimator()
        postsView.adapter = postAdapter

        if (savedInstanceState == null) {
            Log.i(TAG, "savedInstanceState == null")
        }

        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        sp.registerOnSharedPreferenceChangeListener(this)
//
//        Thread(Runnable {
//            var response: MoeResponse? = MoeHttpClient.instance.post("https://konachan.com/post.json", null, null)
//            val result: String? = response?.getResponseAsString()
//            activity.runOnUiThread {
//                Log.i(TAG, result)
//            }
//        }).start()
    }

    private fun setupGridMode() {
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

    private fun reSetupGridMode() {
        if (Settings.gridModeString != currentGridMode) {
            currentGridMode = Settings.gridModeString
            setupGridMode()
            postsView.adapter = null
            postsView.adapter = postAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_grid -> Settings.gridModeString = Key.GRID_MODE_GRID
            R.id.action_staggered_grid -> Settings.gridModeString = Key.GRID_MODE_STAGGERED_GRID
            R.id.action_search_open -> drawer.openDrawer()
            R.id.action_search -> drawer.closeDrawer()
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Key.GRID_MODE -> {
                setGridItemOption()
                reSetupGridMode()
            }
        }
    }

    private fun setGridItemOption() {
        when (Settings.gridModeString) {
            Key.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Key.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            drawerToolbar -> drawer.closeDrawer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        drawer.closeDrawer()
    }

    override fun onPause() {
        super.onPause()
        drawer.closeDrawer()
    }

    override fun onResume() {
        super.onResume()
        reSetupGridMode()
    }

    override fun onBackPressed(): Boolean {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
            return true
        }
        return super.onBackPressed()
    }

    private class PostAdapter(private val toolbarHeight: Int, private val itemPadding: Int) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        companion object {
            private val items : List<String> = listOf(
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg",
                    "https://konachan.com/data/preview/e3/f1/e3f18776471c84b2fda5dc5552b07ce3.jpg",
                    "https://konachan.com/data/preview/34/69/346942d8a914f7821adf3560ee39f9ae.jpg",
                    "https://konachan.com/data/preview/fa/e6/fae61185bf4814bd06f06d0c4d5ae081.jpg"
            )

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            when (Settings.gridModeString) {
                Key.GRID_MODE_STAGGERED_GRID -> {
                    holder.fixedImageView.setWidthAndHeightWeight(150, 100)
                }
                else -> {
                    holder.fixedImageView.setWidthAndHeightWeight(1,1)
                }
            }
            if (position in 0..(Settings.spanCountInt - 1)) {
                holder.itemView.setPadding(itemPadding, itemPadding + toolbarHeight, itemPadding, itemPadding)
                Log.i(this.javaClass.simpleName, "toolbarHeight = $toolbarHeight")
            }
            GlideApp.with(holder.fixedImageView.context)
                    .load(GetUrl(items[position]).glideUrl)
                    .centerCrop()
                    .into(holder.fixedImageView)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        private class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fixedImageView: FixedImageView = itemView.findViewById(R.id.post_item)
        }

    }
}