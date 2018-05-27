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
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.adapter.PostsPagerAdapter
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.TableType
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DetailsFragment : ToolbarFragment(), ViewPager.OnPageChangeListener {

    private val TAG = this::class.java.simpleName
    private var post: RawPost? = null
    private lateinit var bg: View
    private lateinit var postsPager: ViewPager
    private lateinit var postsPagerAdapter: PostsPagerAdapter
    private lateinit var type: String

    private lateinit var toolbar: Toolbar

    private var items: MutableList<RawPost>? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        toolbar = inflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        return inflater.inflate(R.layout.layout_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.transparent))
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))
        toolbar.title = getString(R.string.post)
        setInsetsListener(toolbar)
        val activity = activity as DetailsActivity
        activity.setActionBar(toolbar)
        bg = view.findViewById(R.id.details_bg)
        bg.visibility = View.GONE
        postsPager = view.findViewById(R.id.post_view_pager)
        postsPager.addOnPageChangeListener(this)
        val bundle = arguments
        if (bundle != null) {
            val pos = bundle.getInt(Key.ITEM_POS, 0)
            val id = bundle.getInt(Key.ITEM_ID)
            val type = bundle.getString(Key.TYPE)
            var tags = ""
            if (type == TableType.SEARCH) {
                tags = bundle.getString(Key.TAGS_SEARCH)
            }
            toolbar.title = "${getString(R.string.post)} $id"
            doAsync {
                items = try {
                    when (type) {
                        TableType.SEARCH -> app.searchManager.loadPosts(app.settings.activeProfile, tags)
                        else -> app.postsManager.loadPosts(app.settings.activeProfile)
                    }
                } catch (e: Exception) {
                    null
                }
                uiThread {
                    if (items != null && items!!.size > 0) {
                        Log.i(TAG, "items?.size: ${items?.size}")
                        postsPagerAdapter = PostsPagerAdapter(this@DetailsFragment, items)
                        postsPager.adapter = postsPagerAdapter
                        postsPager.currentItem = pos
                    } else {
                        Toast.makeText(this@DetailsFragment.requireContext(), "Load failed!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        toolbar.title = "${getString(R.string.post)} ${items!![position].id}"
    }

    override fun onPageSelected(position: Int) {

    }

    fun onClickPhotoView() {
        when (bg.visibility) {
            View.GONE -> {
                bg.visibility = View.VISIBLE
                toolbar.visibility = View.GONE
                hideBar()
            }
            else -> {
                bg.visibility = View.GONE
                toolbar.visibility = View.VISIBLE
                showBar()
            }
        }
    }

    private fun showBar() {
        val uiFlags = View.SYSTEM_UI_FLAG_VISIBLE
        activity!!.window.decorView.systemUiVisibility = uiFlags
    }
    private fun hideBar() {
        val uiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity!!.window.decorView.systemUiVisibility = uiFlags
    }
}