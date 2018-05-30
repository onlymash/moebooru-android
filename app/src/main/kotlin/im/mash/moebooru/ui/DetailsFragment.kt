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
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.adapter.PostsPagerAdapter
import im.mash.moebooru.ui.widget.VerticalViewPager
import im.mash.moebooru.utils.getViewModel
import im.mash.moebooru.viewmodel.PostsViewModel
import org.jetbrains.anko.doAsync

class DetailsFragment : ToolbarFragment() {

    private val TAG = this::class.java.simpleName
    private var post: RawPost? = null
    private lateinit var bg: View
    private lateinit var detailsPager: VerticalViewPager
    private lateinit var detailsPagerAdapter: DummyAdapter
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
        detailsPager = view.findViewById(R.id.post_page_pager)
        detailsPagerAdapter = DummyAdapter(activity.supportFragmentManager,
                mutableListOf(
                        TagsFragment(),
                        PagerFragment(),
                        InfoFragment()
                ))
        detailsPager.adapter = detailsPagerAdapter
        detailsPager.currentItem = 1
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


    internal class PagerFragment : Fragment() {

        companion object {
            private const val TAG = "PagerFragment"
        }
        private lateinit var postsPager: ViewPager
        private var postsPagerAdapter: PostsPagerAdapter? = null

        private var currentPosition: Int = 0
        private var tags: String? = null

        private lateinit var postsViewModel: PostsViewModel

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_pager, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val activity = activity as DetailsActivity
            tags = activity.tags
            currentPosition = activity.currentPosition
            postsViewModel = this.getViewModel()
            postsPager = view.findViewById(R.id.post_pager)
            postsViewModel.getPosts(tags).observe(this, Observer {
                activity.items = postsViewModel.getPosts(tags).value
                if (postsPagerAdapter == null) {
                    postsPagerAdapter = PostsPagerAdapter(activity.items, app.settings.postSizeBrowse, activity)
                    postsPager.adapter = postsPagerAdapter
                    postsPager.currentItem = currentPosition
                }
            })
            doAsync {
                postsViewModel.getPosts(tags)
            }
        }
    }

    internal class TagsFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_tags, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            if (activity != null) {
            }
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