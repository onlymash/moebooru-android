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
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.adapter.DetailsTagsAdapter
import im.mash.moebooru.ui.adapter.PostsPagerAdapter
import im.mash.moebooru.ui.widget.VerticalViewPager
import im.mash.moebooru.utils.getViewModel
import im.mash.moebooru.utils.statusBarHeight
import im.mash.moebooru.viewmodel.PostsViewModel
import org.jetbrains.anko.doAsync

class DetailsFragment : ToolbarFragment(), VerticalViewPager.OnPageChangeListener {

    private val TAG = this::class.java.simpleName
    private var post: RawPost? = null
    private lateinit var bg: View
    private lateinit var detailsPager: VerticalViewPager
    private lateinit var detailsPagerAdapter: DummyAdapter
    private var detailsPagerPosition = 2
    private lateinit var toolbar: Toolbar
    internal var items: MutableList<RawPost>? = null
    private lateinit var postsPager: ViewPager

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
        activity.fmWidget(bg, toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(view) {_, insets ->
            activity.statusBarHeight = insets.systemWindowInsetTop
            activity.navBarHeight = insets.systemWindowInsetBottom
            insets
        }
        detailsPager = view.findViewById(R.id.post_page_pager)
        detailsPager.offscreenPageLimit = 4
        detailsPagerAdapter = DummyAdapter(activity.supportFragmentManager,
                mutableListOf(
                        TagsFragment(),
                        InfoFragment(),
                        PagerFragment(),
                        TagsFragment(),
                        InfoFragment()
                ))
        detailsPager.adapter = detailsPagerAdapter
        detailsPager.currentItem = detailsPagerPosition
        detailsPager.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        detailsPagerPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (bg.visibility == View.VISIBLE) {
            bg.visibility = View.GONE
            toolbar.visibility = View.VISIBLE
            (activity as DetailsActivity).showBar()
        }
        if (state == 0) {
            if (detailsPagerPosition == 0) {
                detailsPager.setCurrentItem(3, false)
            } else if (detailsPagerPosition == 4) {
                detailsPager.setCurrentItem(1, false)
            }
        }
    }

    inner class DummyAdapter(fm: FragmentManager,
                             private val fragments: MutableList<Fragment>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }


    internal class PagerFragment : Fragment(), ViewPager.OnPageChangeListener {

        companion object {
            private const val TAG = "PagerFragment"
        }

        private lateinit var detailsActivity: DetailsActivity
        private lateinit var postsPager: ViewPager
        private var postsPagerAdapter: PostsPagerAdapter? = null
        private var tags: String? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_pager, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            detailsActivity = activity as DetailsActivity
            tags = detailsActivity.tags
            postsPager = view.findViewById(R.id.post_pager)
            postsPager.addOnPageChangeListener(this)
            detailsActivity.postsViewModel.getPostsModel(tags).observe(this, Observer {
                detailsActivity.items = detailsActivity.postsViewModel.getPosts(tags)
                if (postsPagerAdapter == null && detailsActivity.items != null) {
                    detailsActivity.toolbarFm.title = getString(R.string.post) + " " + detailsActivity.items!![detailsActivity.positionViewModel.getPosition()].id
                    postsPagerAdapter = PostsPagerAdapter(detailsActivity.items, app.settings.postSizeBrowse, detailsActivity)
                    postsPager.adapter = postsPagerAdapter
                    postsPager.currentItem = detailsActivity.positionViewModel.getPosition()
                }
            })
            doAsync {
                detailsActivity.postsViewModel.initData(tags)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            detailsActivity.positionViewModel.setPosition(position)
            detailsActivity.toolbarFm.title = getString(R.string.post) + " " + detailsActivity.items!![position].id
        }
    }

    internal class TagsFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_tags, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val activity = activity as DetailsActivity
            view.setPadding(0, activity.toolbarHeight + activity.statusBarHeight, 0, activity.navBarHeight)
            val tagsView: RecyclerView = view.findViewById(R.id.rv_details_tags)
            tagsView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
            val tagsAdapter: DetailsTagsAdapter = DetailsTagsAdapter(mutableListOf(
                    "yuri",
                    "mash",
                    "blush",
                    "underwear",
                    "yuri",
                    "mash",
                    "blush",
                    "underwear",
                    "yuri",
                    "mash",
                    "blush",
                    "underwear",
                    "yuri",
                    "mash",
                    "blush",
                    "underwear",
                    "yuri",
                    "mash",
                    "blush",
                    "underwear",
                    "yuri",
                    "mash",
                    "blush",
                    "underwear"
            ))
            tagsView.adapter = tagsAdapter

        }
    }

    internal class InfoFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_info, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

        }
    }
}