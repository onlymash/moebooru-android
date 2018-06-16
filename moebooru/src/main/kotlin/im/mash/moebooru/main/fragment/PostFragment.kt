package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.*
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.App.Companion.coreComponent
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.base.LastItemListener
import im.mash.moebooru.common.base.RecyclerViewClickListener
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.PostAdapter
import im.mash.moebooru.main.adapter.TagDrawerAdapter
import im.mash.moebooru.main.viewmodel.PostViewModel
import im.mash.moebooru.main.viewmodel.TagViewModel
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.screenWidth
import im.mash.moebooru.util.toolbarHeight
import okhttp3.HttpUrl
import java.io.IOException

@SuppressLint("RtlHardcoded")
class PostFragment : ToolbarFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "PostFragment"
    }

    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var drawerListener: DrawerLayout.DrawerListener

    private lateinit var postView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private lateinit var tagView: RecyclerView
    private lateinit var tagAdapter: TagDrawerAdapter

    private lateinit var refreshLayout: SwipeRefreshLayout

    private var spanCount = 3
    private var page = 1
    private var posts = mutableListOf<Post>()

    private var refreshing = false
    private var loadingMore = false
    private var notiNotMore = true
    private var limit = 50

    private var paddingBottom = 0
    
    private val mainActivity by lazy { activity as MainActivity }
    
    private val postViewModel: PostViewModel by lazy { this.getViewModel<PostViewModel>(mainActivity.postViewModelFactory) }
    private val tagViewModel: TagViewModel by lazy { this.getViewModel<TagViewModel>(mainActivity.tagViewModelFactory) }
    private var tags: MutableList<Tag> = mutableListOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_drawer, null) as Toolbar
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coreComponent.sharedPreferences().registerOnSharedPreferenceChangeListener(this)
        initToolbar()
        initRightDrawer(view)
        initDrawerListener()
        spanCount = mainActivity.screenWidth/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        limit = app.settings.postLimitInt
        initRefresh(view)
        initPostList(view)
        observePosts()
        if (savedInstanceState == null) {
            refreshLayout.isRefreshing = true
            postViewModel.loadPosts(getHttpUrl())
        }
    }

    private fun observePosts() {
        postViewModel.postsOutcome.observe( this, Observer<Outcome<MutableList<Post>>> { outcome: Outcome<MutableList<Post>>? ->
            when (outcome) {
                is Outcome.Progress -> {
                    if (!refreshLayout.isRefreshing) refreshLayout.isRefreshing = true
                    logi(TAG, "postViewModel Outcome.Progress")
                }
                is Outcome.Success -> {
                    refreshLayout.isRefreshing = false
                    val data = outcome.data
                    logi(TAG, "postViewModel Outcome.Success. data.size: ${data.size}")
                    if (loadingMore) {
                        posts = data
                        postAdapter.addData(posts)
                        loadingMore = false
                    } else {
                        posts = data
                        postAdapter.updateData(posts)
                        refreshing = false
                    }
                }
                is Outcome.Failure -> {
                    refreshLayout.isRefreshing = false
                    loadingMore = false
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    logi(TAG, "postViewModel Outcome.Failure")
                }
            }
        })
    }

    private fun initRefresh(view: View) {
        refreshLayout = view.findViewById(R.id.refresh)
        refreshLayout.setProgressViewOffset(true, toolbar.minimumHeight, toolbar.minimumHeight + 170)
        refreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.purple,
                R.color.green,
                R.color.orange,
                R.color.red
        )
        refreshLayout.setOnRefreshListener {
            if (!loadingMore && !refreshing) {
                notiNotMore = true
                page = 1
                refreshing = true
                postViewModel.refreshPosts(getHttpUrl())
            }
        }
    }

    private fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileScheme)
                .host(app.settings.activeProfileHost)
                .addPathSegment("post.json")
                .addQueryParameter("limit", limit.toString())
                .addQueryParameter("page", page.toString())
                .addQueryParameter("tags", "")
                .build()
    }

    private fun initPostList(view: View) {
        postView = view.findViewById(R.id.posts_list)
        postView.itemAnimator = DefaultItemAnimator()
        postView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        postView.setItemViewCacheSize(20)
        postView.isDrawingCacheEnabled = true
        postView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> {
                val layoutManager = GridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                postView.layoutManager = layoutManager
                postView.setHasFixedSize(true)
            }
            else -> {
                val layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                postView.layoutManager = layoutManager
                postView.setHasFixedSize(false)
            }
        }
        postAdapter = PostAdapter(this.requireContext(), app.settings.gridModeString)
        postView.adapter = postAdapter

        postView.addOnScrollListener(object : LastItemListener() {
            override fun onLastItemVisible() {
                loadMoreData()
            }
        })

        postView.addOnItemTouchListener(RecyclerViewClickListener(this.requireContext(),
                object : RecyclerViewClickListener.OnItemClickListener {
                    override fun onItemClick(itemView: View?, position: Int) {
                        val intent = Intent(this@PostFragment.requireContext(), DetailActivity::class.java)
                        intent.putExtra("tags", "")
                        intent.putExtra("position", position)
                        startActivity(intent)
                    }

                    override fun onItemLongClick(itemView: View?, position: Int) {
                        logi(TAG, "Long click item: $position")
                    }
                }))
    }

    private fun initDrawerListener() {
        drawerListener = object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                closeRightDrawer()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }
        }
        mainActivity.drawer.drawerLayout.addDrawerListener(drawerListener)
    }

    private fun initToolbar() {
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.transparent))
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_grid -> {
                    if (!toolbar.menu.findItem(R.id.action_grid).isChecked) {
                        toolbar.menu.findItem(R.id.action_grid).isChecked = true
                        app.settings.gridModeString = Settings.GRID_MODE_GRID
                    }
                }
                R.id.action_staggered_grid -> {
                    if (!toolbar.menu.findItem(R.id.action_staggered_grid).isChecked) {
                        toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
                        app.settings.gridModeString = Settings.GRID_MODE_STAGGERED_GRID
                    }
                }
                R.id.action_search_open -> openRightDrawer()
            }
            return@setOnMenuItemClickListener true
        }
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Settings.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
    }

    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_posts)
        drawer = view.findViewById(R.id.right_drawer_view)
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_white_24dp)
        drawerToolbar.setNavigationOnClickListener {
            closeRightDrawer()
        }
        drawerToolbar.inflateMenu(R.menu.menu_main_search)
        drawerToolbar.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_add -> {
                    val editText = EditText(this.requireContext())
                    editText.setSingleLine()
                    val container = FrameLayout(this.requireContext())
                    val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val margin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
                    params.leftMargin = margin
                    params.rightMargin = margin
                    params.topMargin = margin
                    params.bottomMargin = margin
                    editText.layoutParams = params
                    container.addView(editText)
                    AlertDialog.Builder(this.requireContext())
                            .setTitle(getString(R.string.add_a_tag))
                            .setView(container)
                            .setPositiveButton(getString(R.string.ok)) { _, _->
                                val input = editText.text.toString()
                                if (input.isEmpty() || input == "") {
                                    val snackbar = Snackbar.make(view, getString(R.string.tag_can_not_be_empty), Snackbar.LENGTH_SHORT)
                                    snackbar.view.setPadding(0, 0, 0, paddingBottom)
                                    snackbar.show()
                                } else {
                                    var isExist = false
                                    tags.forEach { tag ->
                                        if (input == tag.tag) {
                                            isExist = true
                                            return@forEach
                                        }
                                    }
                                    if (!isExist) {
                                        val tag = Tag(null, app.settings.activeProfileHost, input, false)
                                        tagViewModel.saveTag(tag)
                                    }
                                }
                            }
                            .setNegativeButton(getString(R.string.cancel), null)
                            .show()
                }
                R.id.action_search -> {
                    var keyword = ""
                    tags.forEach { tag ->
                        if (tag.is_selected) {
                            keyword = when (keyword) {
                                "" -> tag.tag
                                else -> "$keyword ${tag.tag}"
                            }
                        }
                    }
                    if (keyword == "") {
                        val snackbar: Snackbar = Snackbar.make(this.view!!, "Tag cant be null", Snackbar.LENGTH_SHORT)
                        snackbar.view.setPadding(0, 0, 0, paddingBottom)
                        snackbar.show()
                    } else {
                        val intent = Intent(this.requireContext(), SearchActivity::class.java)
                        intent.putExtra("keyword", keyword)
                        startActivity(intent)
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
        val tagView = view.findViewById<RecyclerView>(R.id.drawer_rv_list)
        tagView.layoutManager = LinearLayoutManager(this.requireContext(),
                LinearLayoutManager.VERTICAL, false)
        tagAdapter = TagDrawerAdapter(this.requireContext())
        tagView.adapter = tagAdapter

        val drawerViewLayout: LinearLayout = view.findViewById(R.id.drawer_list_layout)
        val drawerAppBarLayout: AppBarLayout = view.findViewById(R.id.appbar_layout_drawer)
        drawerAppBarLayout.addView(drawerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) {_, insets ->
            val paddingTop = insets.systemWindowInsetTop
            paddingBottom = insets.systemWindowInsetBottom
            val toolbarHeight = this.requireContext().toolbarHeight
            drawerViewLayout.setPadding(0, 0, 0, paddingBottom)
            drawerAppBarLayout.minimumHeight = paddingTop + toolbarHeight
            drawerToolbar.setPadding(0, paddingTop, 0, 0)
            insets
        }

        tagViewModel.tagOutcome.observe(this,
                Observer<Outcome<MutableList<Tag>>> { outcome ->
                    when (outcome) {
                        is Outcome.Progress -> {
                            logi(TAG, "tagViewModel Outcome.Progress")
                        }
                        is Outcome.Success -> {
                            tags = outcome.data
                            tagAdapter.updateData(tags)
                            logi(TAG, "tagViewModel Outcome.Success.")
                        }
                        is Outcome.Failure -> {
                            logi(TAG, "tagViewModel Outcome.Failure")
                        }
                    }
        })
        tagViewModel.loadTags(app.settings.activeProfileHost)

        tagAdapter.setTagItemClickListener(object : TagDrawerAdapter.TagItemClickListener {
            override fun onClickItemView(position: Int) {
                tags[position].is_selected = !tags[position].is_selected
                tagViewModel.saveTag(tags[position])
            }

            override fun onCopyTag(position: Int) {
                val cm = this@PostFragment.requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as  ClipboardManager
                val cd = ClipData.newPlainText("Tag: $position", tags[position].tag)
                cm.primaryClip = cd
            }

            override fun onRemoveTag(position: Int) {
                tagViewModel.deleteTag(tags[position])
            }

        })
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
            Settings.GRID_MODE -> {
                when (app.settings.gridModeString) {
                    Settings.GRID_MODE_GRID -> {
                        postView.layoutManager = GridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                        postView.setHasFixedSize(true)
                        postAdapter.setGridMode(Settings.GRID_MODE_GRID)
                        postAdapter.updateData(mutableListOf())
                        postAdapter.updateData(posts)
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        postView.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                        postView.setHasFixedSize(false)
                        postAdapter.setGridMode(Settings.GRID_MODE_STAGGERED_GRID)
                        postAdapter.updateData(mutableListOf())
                        postAdapter.updateData(posts)
                    }
                }
            }
            Settings.ACTIVE_PROFILE_HOST -> {
                posts.clear()
                postAdapter.clearData()
//                loadData()
                mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_moebooru, PostFragment())
                        .commitAllowingStateLoss()
            }
            Settings.POST_LIMIT -> {
                limit = app.settings.postLimitInt
            }
        }
    }

    private fun loadData() {
        if (!loadingMore && !refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
            notiNotMore = true
            postViewModel.reLoadPosts(getHttpUrl())
        }
    }

    private fun loadMoreData() {
        val isNotMore = postViewModel.isNotMore()
        if (!refreshLayout.isRefreshing && !loadingMore && !isNotMore) {
            logi(TAG, "loadMoreData()")
            refreshLayout.isRefreshing = true
            loadingMore = true
            page = posts.size/(limit-1) + 1
            postViewModel.loadMorePosts(getHttpUrl())
        }
        if (isNotMore && notiNotMore) {
            notiNotMore = false
            val snackbar: Snackbar = Snackbar.make(this.view!!, "Not more data.", Snackbar.LENGTH_SHORT)
            snackbar.view.setPadding(0, 0, 0, paddingBottom)
            snackbar.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.drawer.drawerLayout.removeDrawerListener(drawerListener)
        coreComponent.sharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }
}