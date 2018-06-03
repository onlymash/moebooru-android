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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.text.format.Formatter
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.adapter.DetailsTagsAdapter
import im.mash.moebooru.ui.adapter.PostsPagerAdapter
import im.mash.moebooru.ui.widget.AccordionTransformer
import im.mash.moebooru.ui.widget.VerticalViewPager
import im.mash.moebooru.utils.*
import org.jetbrains.anko.doAsync
import java.net.URL
import java.util.*

class DetailsFragment : ToolbarFragment(), ViewPager.OnPageChangeListener {

    private val TAG = this::class.java.simpleName
    private lateinit var bg: View
    private lateinit var detailsPager: VerticalViewPager
    private lateinit var detailsPagerAdapter: DummyAdapter
    private var detailsPagerPosition = 2
    private lateinit var toolbar: Toolbar
    internal var items: MutableList<RawPost>? = null

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
//        detailsPager.offscreenPageLimit = 4
        detailsPagerAdapter = DummyAdapter(activity.supportFragmentManager,
                mutableListOf(
//                        TagsFragment(),
                        InfoFragment(),
                        PagerFragment(),
                        TagsFragment()
//                        InfoFragment()
                ))
        detailsPager.adapter = detailsPagerAdapter
        detailsPager.currentItem = 1
        detailsPager.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
//        detailsPagerPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (bg.visibility == View.VISIBLE) {
            bg.visibility = View.GONE
            toolbar.visibility = View.VISIBLE
            (activity as DetailsActivity).showBar()
        }
//        if (state == 0) {
//            if (detailsPagerPosition == 0) {
//                detailsPager.setCurrentItem(3, false)
//            } else if (detailsPagerPosition == 4) {
//                detailsPager.setCurrentItem(1, false)
//            }
//        }
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
            postsPager.setPageTransformer(true, AccordionTransformer())
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

        private var pos = 0
        private var tagsList: List<String>? = null
        private lateinit var tagsView: RecyclerView
        private lateinit var tagsAdapter: DetailsTagsAdapter
        private var spanCount = 1

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_tags, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val activity = activity as DetailsActivity
            view.setPadding(0, activity.toolbarHeight + statusBarHeight, 0, activity.bottomHeight)
            spanCount = activity.getScreenWidth()/activity.resources.getDimension(R.dimen.tag_item_width).toInt()
            if (spanCount == 0) {
                spanCount = 1
            }
            tagsView = view.findViewById(R.id.rv_details_tags)
            tagsView.layoutManager = GridLayoutManager(this.requireContext(), spanCount, GridLayoutManager.VERTICAL, false)
            tagsView.hasFixedSize()
            pos = activity.positionViewModel.getPosition()
            tagsList = activity.items?.get(pos)?.tags?.split(" ")?.toMutableList()
            tagsAdapter = DetailsTagsAdapter(this, tagsList)
            tagsView.adapter = tagsAdapter
            activity.positionViewModel.getPositionModel().observe(this, Observer {
                pos = it!!
                tagsList = activity.items?.get(pos)?.tags?.split(" ")
                tagsAdapter.updateData(tagsList)
            })
        }

        internal fun clickTag(position: Int) {
            val intent = Intent(activity, SearchActivity().javaClass)
            val bundle = Bundle()
            bundle.putString(Key.TAGS_SEARCH, tagsList!![position])
            intent.putExtra(Key.BUNDLE, bundle)
            startActivity(intent)
        }

    }

    internal class InfoFragment : Fragment() {

        private lateinit var id: TextView
        private lateinit var createdAt: TextView
        private lateinit var author: TextView
        private lateinit var creatorId: TextView
        private lateinit var heightWidth: TextView
        private lateinit var source: TextView
        private lateinit var rating: TextView
        private lateinit var score: TextView

        private lateinit var parentId: ImageButton
        private lateinit var parentIdLayout: LinearLayout

        private lateinit var sampleDownload: ImageButton
        private lateinit var sampleOpen: ImageButton
        private lateinit var sampleSize: TextView
        private lateinit var sampleLayout: LinearLayout

        private lateinit var largerDownload: ImageButton
        private lateinit var largerOpen: ImageButton
        private lateinit var largerSize: TextView
        private lateinit var largerLayout: LinearLayout

        private lateinit var originDownload: ImageButton
        private lateinit var originOpen: ImageButton
        private lateinit var originSize: TextView
        private lateinit var originLayout: LinearLayout

        private var post: RawPost? = null
        private var pos = 0

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.layout_details_info, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val activity = activity as DetailsActivity
            val padding = activity.resources.getDimension(R.dimen.list_padding).toInt()
            view.setPadding(padding, activity.toolbarHeight + statusBarHeight + padding, padding, activity.bottomHeight)
            init(view)
            pos = activity.positionViewModel.getPosition()
            post = activity.items?.get(pos)
            activity.positionViewModel.getPositionModel().observe(this, Observer {
                pos = it!!
                post = activity.items?.get(pos)
                setData()
            })
            setListener()
            setData()
        }

        private fun setData() {
            if (post != null) {
                id.text = post!!.id.toString()
                author.text = post!!.author
                val cal = Calendar.getInstance(Locale.getDefault())
                cal.timeInMillis = post!!.created_at!! * 1000
                createdAt.text = DateFormat.format("yyyy-MM-dd HH:mm", cal)
                creatorId.text = post!!.creator_id.toString()
                val hw = "${post!!.height} x ${post!!.width}"
                heightWidth.text = hw
                if (post!!.source != null) {
                    source.text = post!!.source
                } else {
                    source.text = ""
                }
                rating.text = post!!.rating
                score.text = post!!.score.toString()
                if (post!!.parent_id != null) {
                    parentIdLayout.visibility = View.VISIBLE
                } else {
                    parentIdLayout.visibility = View.GONE
                }

                sampleSize.text = Formatter.formatFileSize(this.requireContext(), post!!.sample_file_size!!)

                if (post!!.jpeg_url != null) {
                    if (largerLayout.visibility == View.GONE) {
                        largerLayout.visibility = View.VISIBLE
                    }
                    largerSize.text = Formatter.formatFileSize(this.requireContext(), post!!.jpeg_file_size!!)
                } else {
                    if (largerLayout.visibility == View.VISIBLE) {
                        largerLayout.visibility = View.GONE
                    }
                }

                if (post!!.file_url != null) {
                    if (originLayout.visibility == View.GONE) {
                        originLayout.visibility = View.VISIBLE
                    }
                    originSize.text = Formatter.formatFileSize(this.requireContext(), post!!.file_size!!)
                } else {
                    if (originLayout.visibility == View.VISIBLE) {
                        originLayout.visibility = View.GONE
                    }
                }
            }
        }

        private fun setListener() {
            sampleDownload.setOnClickListener {
                if (post != null) {
                    val booru = app.boorusManager.getBooru(app.settings.activeProfile)
                    val title = booru.name + " " + post!!.id
                    val url = post!!.sample_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1)
                    if (mayRequestStoragePermission(activity!!, 0)) {
                        downloadPost(post!!.sample_url!!, title, booru.name, fileName)
                    }
                }
            }
            sampleOpen.setOnClickListener {
                if (post != null) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val uri = Uri.parse(post!!.sample_url)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
            sampleLayout.setOnLongClickListener {
                val cm: ClipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as  ClipboardManager
                val cd = ClipData.newPlainText("url: $pos", post!!.sample_url)
                cm.primaryClip = cd
                val snackbar = Snackbar.make(view!!, "Sample url has been copied", Snackbar.LENGTH_SHORT)
                snackbar.view.setPadding(0, 0, 0, (activity as DetailsActivity).bottomHeight)
                snackbar.show()
                true
            }
            largerDownload.setOnClickListener {
                if (post != null) {
                    val booru = app.boorusManager.getBooru(app.settings.activeProfile)
                    val title = booru.name + " " + post!!.id
                    val url = post!!.jpeg_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1)
                    if (mayRequestStoragePermission(activity!!, 0)) {
                        downloadPost(url, title, booru.name, fileName)
                    }
                }
            }
            largerOpen.setOnClickListener {
                if (post != null) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val uri = Uri.parse(post!!.jpeg_url)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
            largerLayout.setOnLongClickListener {
                val cm: ClipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as  ClipboardManager
                val cd = ClipData.newPlainText("url: $pos", post!!.jpeg_url)
                cm.primaryClip = cd
                val snackbar = Snackbar.make(view!!, "Larger url has been copied", Snackbar.LENGTH_SHORT)
                snackbar.view.setPadding(0, 0, 0, (activity as DetailsActivity).bottomHeight)
                snackbar.show()
                true
            }
            originDownload.setOnClickListener {
                if (post != null) {
                    val booru = app.boorusManager.getBooru(app.settings.activeProfile)
                    val title = booru.name + " " + post!!.id
                    val url = post!!.file_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1)
                    if (mayRequestStoragePermission(activity!!, 0)) {
                        downloadPost(post!!.file_url!!, title, booru.name, fileName)
                    }
                }
            }
            originOpen.setOnClickListener {
                if (post != null) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val uri = Uri.parse(post!!.file_url)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
            originLayout.setOnLongClickListener {
                val cm: ClipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as  ClipboardManager
                val cd = ClipData.newPlainText("url: $pos", post!!.file_url)
                cm.primaryClip = cd
                val snackbar = Snackbar.make(view!!, "Origin url has been copied", Snackbar.LENGTH_SHORT)
                snackbar.view.setPadding(0, 0, 0, (activity as DetailsActivity).bottomHeight)
                snackbar.show()
                true
            }
        }

        private fun init(view: View) {
            id = view.findViewById(R.id.tv_id)
            author = view.findViewById(R.id.tv_author)
            createdAt = view.findViewById(R.id.tv_created_at)
            creatorId = view.findViewById(R.id.tv_creator_id)
            heightWidth = view.findViewById(R.id.tv_height_width)
            source = view.findViewById(R.id.tv_source)
            rating = view.findViewById(R.id.tv_rating)
            score = view.findViewById(R.id.tv_score)
            parentId = view.findViewById(R.id.btn_parent_id)
            parentIdLayout = view.findViewById(R.id.container_parent_id)

            sampleDownload = view.findViewById(R.id.btn_download_sample)
            sampleOpen = view.findViewById(R.id.btn_browser_sample)
            sampleSize = view.findViewById(R.id.tv_sample_size)
            sampleLayout = view.findViewById(R.id.sample_layout)

            largerDownload = view.findViewById(R.id.btn_download_larger)
            largerOpen = view.findViewById(R.id.btn_browser_larger)
            largerSize = view.findViewById(R.id.tv_larger_size)
            largerLayout = view.findViewById(R.id.larger_layout)

            originDownload = view.findViewById(R.id.btn_download_origin)
            originOpen = view.findViewById(R.id.btn_browser_origin)
            originSize = view.findViewById(R.id.tv_origin_size)
            originLayout = view.findViewById(R.id.origin_layout)
        }
    }
}