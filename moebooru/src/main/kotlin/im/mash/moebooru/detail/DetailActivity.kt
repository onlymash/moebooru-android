package im.mash.moebooru.detail

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.application.SlidingActivity
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.widget.VerticalViewPager
import im.mash.moebooru.detail.adapter.DetailAdapter
import im.mash.moebooru.detail.fragment.InfoFragment
import im.mash.moebooru.detail.fragment.PagerFragment
import im.mash.moebooru.detail.fragment.TagFragment
import im.mash.moebooru.detail.viewmodel.DetailViewModel
import im.mash.moebooru.detail.viewmodel.DetailViewModelFactory
import im.mash.moebooru.detail.viewmodel.PositionViewModel
import im.mash.moebooru.detail.viewmodel.PositionViewModelFactory
import im.mash.moebooru.download.DownloadService
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.viewmodel.DownloadViewModel
import im.mash.moebooru.main.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.main.viewmodel.TagViewModelFactory
import im.mash.moebooru.util.launchUrl
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.mayRequestStoragePermission
import javax.inject.Inject

class DetailActivity : SlidingActivity(), ViewPager.OnPageChangeListener, Toolbar.OnMenuItemClickListener {

    companion object {
        private const val TAG = "DetailActivity"
    }

    internal var paddingBottom = 0
    internal var paddingTop = 0

    private lateinit var detailPager: VerticalViewPager
    private lateinit var detailAdapter: DetailAdapter

    private val component by lazy { MoeDH.detailComponent() }

    @Inject
    lateinit var detailViewModelFactory: DetailViewModelFactory
    private val detailViewModel: DetailViewModel by lazy { this.getViewModel<DetailViewModel>(detailViewModelFactory) }

    @Inject
    lateinit var positionViewModelFactory: PositionViewModelFactory
    internal val positionViewModel: PositionViewModel by lazy { this.getViewModel<PositionViewModel>(positionViewModelFactory) }

    @Inject
    lateinit var database: MoeDatabase

    @Inject
    lateinit var downloadViewModelFactory: DownloadViewModelFactory
    internal val downloadViewModel: DownloadViewModel by lazy { this.getViewModel<DownloadViewModel>(downloadViewModelFactory) }

    @Inject
    lateinit var tagViewModelFactory: TagViewModelFactory

    internal var posts: MutableList<Post> = mutableListOf()
    internal var postsSearch: MutableList<PostSearch> = mutableListOf()

    internal var type = "post"
    internal var position = 0

    private lateinit var bg: View
    private lateinit var appBarLayout: AppBarLayout
    internal lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        component.inject(this)
        initView()
        val tags = intent.getStringExtra("tags")
        position = intent.getIntExtra("position", 0)
        positionViewModel.setPosition(position)
        initViewModel(tags)
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        detailPager = findViewById(R.id.detail_pager)
        bg = findViewById(R.id.detail_bg)
        appBarLayout = findViewById(R.id.appbar_layout)
        toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        appBarLayout.addView(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            paddingTop = insets.systemWindowInsetTop
            paddingBottom = insets.systemWindowInsetBottom
            appBarLayout.minimumHeight = toolbar.minimumHeight + paddingTop
            toolbar.setPadding(0, paddingTop, 0, 0)
            appBarLayout.removeView(toolbar)
            appBarLayout.addView(toolbar)
            insets
        }
        toolbar.inflateMenu(R.menu.menu_details)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share_url -> {
                val shareUrl = getPostUrl()
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/*"
                intent.putExtra(Intent.EXTRA_TEXT, shareUrl)
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.share_to)))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.action_download -> {
                if (mayRequestStoragePermission(this, 0)) {
                    downloadViewModel.addTask(getDownloadPost())
                    DownloadService.startTask(this)
                }
            }
            R.id.action_browser -> {
                this.launchUrl(getPostUrl())
            }
        }
        return true
    }

    private fun getDownloadPost(): PostDownload {
        return when (type) {
            "post" -> {
                val url = when (app.settings.postSizeDownload) {
                    Settings.POST_SIZE_SAMPLE -> posts[position].sample_url
                    Settings.POST_SIZE_LARGER -> posts[position].getJpegUrl()
                    else -> posts[position].getFileUrl()
                }
                PostDownload(null, app.settings.activeProfileHost, posts[position].id, posts[position].preview_url, url,"")
            }
            else -> {
                val url = when (app.settings.postSizeDownload) {
                    Settings.POST_SIZE_SAMPLE -> postsSearch[position].sample_url
                    Settings.POST_SIZE_LARGER -> postsSearch[position].getJpegUrl()
                    else -> postsSearch[position].getFileUrl()
                }
                PostDownload(null, app.settings.activeProfileHost, postsSearch[position].id,
                        postsSearch[position].preview_url, url,"")
            }
        }
    }

    private fun getPostUrl(): String {
        return when (type) {
            "post" -> {
                app.settings.activeProfileScheme + "://"+ app.settings.activeProfileHost + "/post/show/" + posts[position].id
            }
            else -> {
                app.settings.activeProfileScheme + "://"+ app.settings.activeProfileHost + "/post/show/" + postsSearch[position].id
            }
        }
    }

    fun setBg() {
        when (appBarLayout.visibility) {
            View.VISIBLE -> {
                bg.visibility = View.VISIBLE
                appBarLayout.visibility = View.GONE
                hideBar()
            }
            else -> {
                bg.visibility = View.GONE
                appBarLayout.visibility = View.VISIBLE
                showBar()
            }
        }
    }

    private fun resetBg() {
        if (appBarLayout.visibility == View.GONE) {
            bg.visibility = View.GONE
            appBarLayout.visibility = View.VISIBLE
            showBar()
        }
    }

    private fun initDetailPager() {
        detailAdapter = DetailAdapter(supportFragmentManager, mutableListOf(
                InfoFragment(),
                PagerFragment(),
                TagFragment()
        ))
        detailPager.adapter = detailAdapter
        detailPager.currentItem = 1
        detailPager.addOnPageChangeListener(this)
    }

    private fun initViewModel(tags: String) {
        if (tags == "") {
            type = "post"
            logi(TAG, "type = post")
            detailViewModel.postOutcome.observe(this,
                    Observer<Outcome<MutableList<Post>>> { outcome ->
                when (outcome) {
                    is Outcome.Progress -> {

                    }
                    is Outcome.Success -> {
                        posts = outcome.data
                        if (posts.size > 0) {
                            toolbar.title = getString(R.string.post) + " " + posts[position].id
                            initDetailPager()
                        }
                    }
                    is Outcome.Failure -> {

                    }
                }
            })
            detailViewModel.loadPosts(app.settings.activeProfileHost)
        } else {
            type = "search"
            logi(TAG, "type = search")
            detailViewModel.postSearchOutcome.observe(this,
                    Observer<Outcome<MutableList<PostSearch>>> { outcome ->
                when (outcome) {
                    is Outcome.Progress -> {
                        logi(TAG, "Outcome.Progress")
                    }
                    is Outcome.Success -> {
                        logi(TAG, "Outcome.Success")
                        postsSearch = outcome.data
                        if (postsSearch.size > 0) {
                            toolbar.title = getString(R.string.post) + " " + postsSearch[position].id
                            initDetailPager()
                        }
                    }
                    is Outcome.Failure -> {
                        logi(TAG, "Outcome.Failure")
                        outcome.e.printStackTrace()
                    }
                }
            })
            detailViewModel.loadPosts(app.settings.activeProfileHost, tags)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        resetBg()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    fun setToolbarTitle(position: Int) {
        this.position = position
        when (type) {
            "post" -> {
                toolbar.title = getString(R.string.post) + " " + posts[position].id
            }
            else -> {
                toolbar.title = getString(R.string.post) + " " + postsSearch[position].id
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var result = true
        try {
            result = super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        return result
    }
}