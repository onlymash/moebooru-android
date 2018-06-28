package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.*
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.ListPopupWindow
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.base.LastItemListener
import im.mash.moebooru.common.base.SafeGridLayoutManager
import im.mash.moebooru.common.base.SafeStaggeredGridLayoutManager
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.common.viewmodel.UserViewModel
import im.mash.moebooru.common.viewmodel.VoteViewModel
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.PostAdapter
import im.mash.moebooru.main.adapter.TagDrawerAdapter
import im.mash.moebooru.main.viewmodel.PostViewModel
import im.mash.moebooru.main.viewmodel.TagViewModel
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.util.*
import okhttp3.HttpUrl
import retrofit2.HttpException
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

    private var safeMode = true

    private var spanCount = 3
    private var page = 1
    private var posts: MutableList<Post> = mutableListOf()

    private var refreshing = false
    private var loadingMore = false
    private var notiNotMore = true
    private var limit = 50

    private var firstStart = true

    private var paddingBottom = 0

    private val mainActivity by lazy { activity as MainActivity }

    private var user: User? = null
    private var users: MutableList<User> = mutableListOf()
    private val userViewModel: UserViewModel by lazy { this.getViewModel<UserViewModel>(mainActivity.userViewModelFactory) }
    private val postViewModel: PostViewModel by lazy { this.getViewModel<PostViewModel>(mainActivity.postViewModelFactory) }
    private val tagViewModel: TagViewModel by lazy { this.getViewModel<TagViewModel>(mainActivity.tagViewModelFactory) }
    private val voteViewModel: VoteViewModel by lazy { this.getViewModel<VoteViewModel>(mainActivity.voteViewModelFactory) }
    private var tags: MutableList<Tag> = mutableListOf()

    private var voteChangedId = -1
    private var voteChangedScore = 0

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_drawer, null) as Toolbar
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spanCount = mainActivity.screenWidth/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        limit = app.settings.postLimitInt
        safeMode = app.settings.safeMode
        initToolbar()
        initRightDrawer(view)
        initDrawerListener()
        initRefresh(view)
        initPostView(view)
        initVoteViewModel()
        initPostViewModel()
        initUserViewModel()
        mainActivity.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        if (savedInstanceState == null) {
            refreshLayout.isRefreshing = true
            postViewModel.loadPosts(getHttpUrl())
        }
    }

    private fun initVoteViewModel() {
        voteViewModel.idsOutcomeOneTwo.observe(this, Observer<Outcome<MutableList<Int>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {}
                is Outcome.Success -> {
                    if (voteChangedId > 0 && voteChangedScore == 0) {
                        try {
                            postView.findViewWithTag<ImageView>(voteChangedId)?.setImageResource(R.drawable.ic_action_star_border_24dp)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    val data = outcome.data
                    if (data.size > 0) {
                        logi(TAG, "idsOutcomeOneTwo size: ${data.size}")
                        postAdapter.updateVoteIdsOneTwo(data)
                        if (postAdapter.itemCount > 0) {
                            data.forEach { tag ->
                                try {
                                    postView.findViewWithTag<ImageView>(tag)?.setImageResource(R.drawable.ic_action_star_half_24dp)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                is Outcome.Failure -> {
                    val error = outcome.e
                    when (error) {
                        is HttpException -> {
                            Toast.makeText(this.requireContext(), "code: ${error.code()}, msg: ${error.message()}", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            error.printStackTrace()
                        }
                    }
                }
            }
        })
        voteViewModel.idsOutcomeThree.observe(this, Observer<Outcome<MutableList<Int>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {}
                is Outcome.Success -> {
                    val data = outcome.data
                    if (data.size > 0) {
                        logi(TAG, "idsOutcomeThree size: ${data.size}")
                        postAdapter.updateVoteIdsThree(data)
                        if (postAdapter.itemCount > 0) {
                            data.forEach { tag ->
                                try {
                                    postView.findViewWithTag<ImageView>(tag)?.setImageResource(R.drawable.ic_action_star_24dp)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                is Outcome.Failure -> {
                    val error = outcome.e
                    when (error) {
                        is HttpException -> {
                            Toast.makeText(this.requireContext(), "code: ${error.code()}, msg: ${error.message()}", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            error.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    private fun initUserViewModel() {
        userViewModel.userOutcome.observe(this, Observer<Outcome<MutableList<User>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {

                }
                is Outcome.Success -> {
                    users.clear()
                    if (outcome.data.isNotEmpty()) {
                        users = outcome.data
                        initUser()
                    }
                }
                is Outcome.Failure -> {
                    outcome.e.printStackTrace()
                }
            }
        })
        userViewModel.loadUsers()
    }

    private fun initUser() {
        val host = app.settings.activeProfileHost
        user = null
        users.forEach { user ->
            if (user.site == host) {
                this.user = user
                return@forEach
            }
        }
        if (user != null) {
            voteViewModel.getVoteIdsOneTwo(user!!.site, user!!.name)
            voteViewModel.getVoteIdsThree(user!!.site, user!!.name)
        }
    }

    private fun initPostViewModel() {
        postViewModel.postsOutcome.observe( this, Observer<Outcome<MutableList<Post>>> { outcome: Outcome<MutableList<Post>>? ->
            when (outcome) {
                is Outcome.Progress -> {
                    logi(TAG, "postViewModel Outcome.Progress")
                }
                is Outcome.Success -> {
                    refreshing = false
                    refreshLayout.isRefreshing = false
                    val data = outcome.data
                    if (loadingMore) {
                        loadingMore = false
                        if (posts != data) {
                            posts = data
                            if (safeMode) {
                                postAdapter.addData(getSafePosts())
                            } else {
                                postAdapter.addData(posts)
                            }
                        }
                    } else {
                        if (posts != data) {
                            posts = data
                            if (safeMode) {
                                postAdapter.updateData(getSafePosts())
                            } else {
                                postAdapter.updateData(posts)
                            }
                        } else if (firstStart) {
                            firstStart = false
                            if (!mainActivity.isNetworkConnected) {
                                takeSnackbarShort(this.view!!, "Network without connection", paddingBottom)
                            } else {
                                refresh()
                            }
                        }
                    }
                    logi(TAG, "postViewModel Outcome.Success. data.size: ${posts.size}")
                }
                is Outcome.Failure -> {
                    loadingMore = false
                    refreshLayout.isRefreshing = false
                    refreshing = false
                    when (outcome.e) {
                        is HttpException -> {
                            val httpException = outcome.e as HttpException
                            val message = httpException.response().message()
                            val code = httpException.response().code()
                            Toast.makeText(this.requireContext(), "code: $code, msg: $message", Toast.LENGTH_SHORT).show()
                        }
                        is IOException -> {
                            outcome.e.printStackTrace()
                        }
                    }
                    logi(TAG, "postViewModel Outcome.Failure")
                }
            }
        })
    }

    private fun getSafePosts(): MutableList<Post> {
        val safePosts:MutableList<Post> = mutableListOf()
        posts.forEach { post ->
            if (post.rating == "s") {
                safePosts.add(post)
            }
        }
        return safePosts
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
            if (!mainActivity.isNetworkConnected) {
                refreshLayout.isRefreshing = false
                takeSnackbarShort(this.view!!, "Network without connection", paddingBottom)
                return@setOnRefreshListener
            }
            if (!loadingMore && !refreshing) {
                refresh()
            } else {
                refreshLayout.isRefreshing = false
            }
        }
    }

    private fun refresh() {
        refreshLayout.isRefreshing = true
        notiNotMore = true
        page = 1
        refreshing = true
        postViewModel.refreshPosts(getHttpUrl())
    }

    private fun getHttpUrl(): HttpUrl {
        val user = this.user
        var username = ""
        var passwordHash = ""
        if (user != null) {
            username = user.name
            passwordHash = user.password_hash
        }
        return HttpUrl.Builder()
                .scheme(app.settings.activeProfileSchema)
                .host(app.settings.activeProfileHost)
                .addPathSegment("post.json")
                .addQueryParameter("limit", limit.toString())
                .addQueryParameter("page", page.toString())
                .addQueryParameter("tags", "")
                .addQueryParameter("login", username)
                .addQueryParameter("password_hash", passwordHash)
                .build()
    }

    @SuppressLint("InflateParams")
    private fun initPostView(view: View) {
        postView = view.findViewById(R.id.posts_list)
        (postView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        postView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        postView.setItemViewCacheSize(20)
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> {
                val layoutManager = SafeGridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                postView.layoutManager = layoutManager
                postView.setHasFixedSize(true)
            }
            else -> {
                val layoutManager = SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
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
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                when (newState) {
//                    RecyclerView.SCROLL_STATE_SETTLING -> GlideApp.with(this@PostFragment.requireContext()).pauseRequests()
//                    RecyclerView.SCROLL_STATE_IDLE -> GlideApp.with(this@PostFragment.requireContext()).resumeRequests()
//                }
//            }
        })
        postAdapter.setPostItemClickListener(object : PostAdapter.PostItemClickListener {
            override fun onClickPostItem(position: Int) {
                val intent = Intent(mainActivity, DetailActivity::class.java)
                intent.putExtra("tags", "")
                intent.putExtra("position", position)
                startActivity(intent)
            }

            override fun onClickRate(position: Int, id: Int, rate: ImageView) {
                voteChangedId = id
                val user = this@PostFragment.user
                if (user == null) {
                    takeSnackbarShort(this@PostFragment.view!!, "This operation requires a user account.", paddingBottom)
                    return
                }
                val v = layoutInflater.inflate(R.layout.layout_ratingbar, null)
                voteChangedScore = 0
                val scoreTv: TextView = v.findViewById(R.id.score)
                scoreTv.text = "0"
                val ratingBar: RatingBar = v.findViewById(R.id.rating_bar)
                val cancel: Button = v.findViewById(R.id.cancel)
                val vote: Button = v.findViewById(R.id.set_vote)
                ratingBar.setOnRatingBarChangeListener { _, star, _ ->
                    voteChangedScore = star.toInt()
                    scoreTv.text = voteChangedScore.toString()
                }
                val dialog = AlertDialog.Builder(this@PostFragment.requireContext())
                        .setTitle("Vote post")
                        .create()
                dialog.apply {
                    setView(v)
                    setCanceledOnTouchOutside(true)
                    show()
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                vote.setOnClickListener {
                    val url = app.settings.activeProfileSchema + "://" + user.site + "/post/vote.json"
                    voteViewModel.votePost(url, id, voteChangedScore, user.name, user.password_hash)
                    dialog.dismiss()
                }
            }
        })
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
                R.id.action_safe_mode -> { app.settings.safeMode = !app.settings.safeMode }
            }
            return@setOnMenuItemClickListener true
        }
        when (app.settings.gridModeString) {
            Settings.GRID_MODE_GRID -> toolbar.menu.findItem(R.id.action_grid).isChecked = true
            Settings.GRID_MODE_STAGGERED_GRID -> toolbar.menu.findItem(R.id.action_staggered_grid).isChecked = true
        }
        toolbar.menu.findItem(R.id.action_safe_mode).isChecked = safeMode
    }

    @SuppressLint("InflateParams")
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
                                    takeSnackbarShort(view, getString(R.string.tag_can_not_be_empty), paddingBottom)
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
                    var i = 0
                    tags.forEach { tag ->
                        if (tag.is_selected) {
                            i += 1
                            keyword = when (keyword) {
                                "" -> tag.tag
                                else -> "$keyword ${tag.tag}"
                            }
                        }
                    }
                    when {
                        keyword == "" -> {
                            takeSnackbarShort(view, getString(R.string.tag_can_not_be_empty), paddingBottom)
                        }
                        i > 6 -> {
                            takeSnackbarShort(view, "You can only search up to six tags at once", paddingBottom)
                        }
                        else -> {
                            val intent = Intent(this.requireContext(), SearchActivity::class.java)
                            intent.putExtra("keyword", keyword)
                            startActivity(intent)
                        }
                    }
                }
                R.id.action_filter -> {
                    val v = layoutInflater.inflate(R.layout.layout_filter, null)
                    val inputNumber: TextInputEditText = v.findViewById(R.id.input_number)
                    val typeSpinner: Spinner = v.findViewById(R.id.type)
                    val compareSpinner: Spinner = v.findViewById(R.id.compare)
                    val ratingSpinner: Spinner = v.findViewById(R.id.type_rating)
                    val orderSpinner: Spinner = v.findViewById(R.id.type_order)
                    try {
                        val popup = Spinner::class.java.getDeclaredField("mPopup")
                        popup.isAccessible = true
                        val listPopupWindow = popup.get(orderSpinner) as ListPopupWindow
                        listPopupWindow.height = mainActivity.screenHeight/2 - paddingBottom
                    } catch (e: NoClassDefFoundError) {
                        e.printStackTrace()
                    }
                    var type = "id"
                    var compare = ":"
                    var number = ""
                    var order = "id"
                    var rating = "safe"
                    typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when (position) {
                                0 -> type = "id"
                                1 -> type = "height"
                                2 -> type = "width"
                            }
                        }
                    }
                    compareSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when (position) {
                                0 -> compare = ":"
                                1 -> compare = ":>"
                                2 -> compare = ":>="
                                3 -> compare = ":<"
                                4 -> compare = ":<="
                            }
                        }
                    }
                    orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when (position) {
                                0 -> order = "id"
                                1 -> order = "id_desc"
                                2 -> order = "fav"
                                3 -> order = "wide"
                                4 -> order = "nonwide"
                                5 -> order = "score"
                                6 -> order = "score_asc"
                                7 -> order = "mpixels"
                                8 -> order = "mpixels_asc"
                                9 -> order = "landscape"
                                10 -> order = "portrait"
                                11 -> order = "vote"
                                12 -> order = "favcount"
                            }
                        }
                    }
                    ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            when (position) {
                                0 -> rating = "safe"
                                1 -> rating = "questionable"
                                2 -> rating = "explicit"
                            }
                        }
                    }
                    val site = app.settings.activeProfileHost
                    val addInterval: ImageView = v.findViewById(R.id.add_interval)
                    val addOrder: ImageView = v.findViewById(R.id.add_order)
                    val addRating: ImageView = v.findViewById(R.id.add_rating)
                    addInterval.setOnClickListener {
                        number = inputNumber.text.toString()
                        if (TextUtils.isEmpty(number)) {
                            return@setOnClickListener
                        }
                        val tag = Tag(null, site, "$type$compare$number", false)
                        tagViewModel.saveTag(tag)
                    }
                    addOrder.setOnClickListener {
                        val tag = Tag(null, site, "order:$order", false)
                        tagViewModel.saveTag(tag)
                    }
                    addRating.setOnClickListener {
                        val tag = Tag(null, site, "rating:$rating", false)
                        tagViewModel.saveTag(tag)
                    }
                    val dialog = AlertDialog.Builder(this.requireContext())
                            .setTitle(R.string.tag_filter)
                            .create()
                    dialog.apply {
                        setView(v)
                        setCanceledOnTouchOutside(true)
                        show()
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
        tagView = view.findViewById(R.id.drawer_rv_list)
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
                        postView.layoutManager = SafeGridLayoutManager(this.context, spanCount, GridLayoutManager.VERTICAL, false)
                        postView.setHasFixedSize(true)
                        postAdapter.setGridMode(Settings.GRID_MODE_GRID)
                        postAdapter.updateData(mutableListOf())
                        if (safeMode) postAdapter.updateData(getSafePosts()) else postAdapter.updateData(posts)
                    }
                    Settings.GRID_MODE_STAGGERED_GRID -> {
                        postView.layoutManager = SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
                        postView.setHasFixedSize(false)
                        postAdapter.setGridMode(Settings.GRID_MODE_STAGGERED_GRID)
                        postAdapter.updateData(mutableListOf())
                        if (safeMode) postAdapter.updateData(getSafePosts()) else postAdapter.updateData(posts)
                    }
                }
            }
            Settings.ACTIVE_PROFILE_HOST -> {
                posts.clear()
                postAdapter.clearData()
                mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_moebooru, PostFragment())
                        .commitAllowingStateLoss()
            }
            Settings.POST_LIMIT -> {
                limit = app.settings.postLimitInt
            }
            Settings.SAFE_MODE -> {
                safeMode = app.settings.safeMode
                toolbar.menu.findItem(R.id.action_safe_mode).isChecked = safeMode
                if (safeMode) {
                    postAdapter.updateData(getSafePosts())
                } else {
                    postAdapter.updateData(posts)
                }
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
        if (!mainActivity.isNetworkConnected) {
            takeSnackbarShort(this.view!!, "Network without connection", paddingBottom)
        }
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
            takeSnackbarShort(this.view!!, "Not more posts", paddingBottom)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.drawer.drawerLayout.removeDrawerListener(drawerListener)
        mainActivity.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}