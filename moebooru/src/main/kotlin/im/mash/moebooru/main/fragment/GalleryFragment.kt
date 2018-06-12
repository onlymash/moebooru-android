package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import im.mash.moebooru.R
import im.mash.moebooru.common.base.RecyclerViewClickListener
import im.mash.moebooru.common.base.ToolbarDialog
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.application.SlidingDialog
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.glide.GlideRequests
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.GalleryAdapter
import im.mash.moebooru.main.viewmodel.MediaViewModel
import im.mash.moebooru.util.mayRequestStoragePermission
import im.mash.moebooru.util.screenWidth
import java.io.File
import java.io.IOException

@SuppressLint("RtlHardcoded")
class GalleryFragment : ToolbarFragment() {

    companion object {
        private const val TAG = "GalleryFragment"
    }

    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var appBarLayoutRight: AppBarLayout
    private lateinit var galleryView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var drawerListener: DrawerLayout.DrawerListener

    private var spanCount = 1
    private var media: MutableList<MediaStoreData> = mutableListOf()

    private val mainActivity: MainActivity by lazy { activity as MainActivity }
    private val mediaViewModel: MediaViewModel by lazy { this.getViewModel<MediaViewModel>(mainActivity.mediaViewModelFactory) }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_local_gallery, container, false)
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_drawer, null) as Toolbar
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGalleryView(view)
        initViewModel()
        initRightDrawer(view)
        initDrawerListener()
    }

    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_local_gallery)
        appBarLayoutRight = view.findViewById(R.id.appbar_layout_drawer)
        drawer = view.findViewById(R.id.right_drawer_view)
        drawerToolbar.setNavigationIcon(R.drawable.ic_action_close_white_24dp)
        appBarLayoutRight.addView(drawerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) {_, insets ->
            val paddingTop = insets.systemWindowInsetTop
            appBarLayoutRight.minimumHeight = paddingTop + drawerToolbar.minimumHeight
            drawerToolbar.setPadding(0, paddingTop, 0, 0)
            appBarLayoutRight.removeView(drawerToolbar)
            appBarLayoutRight.addView(drawerToolbar)
            insets
        }
    }

    private fun initViewModel() {
        mediaViewModel.mediaOutcome.observe(this, Observer<Outcome<MutableList<MediaStoreData>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {
                    Log.i(TAG, "Media Outcome.Progress")
                }
                is Outcome.Success -> {
                    Log.i(TAG, "Media Outcome.Success")
                    media = outcome.data
                    galleryAdapter.updateData(media)
                }
                is Outcome.Failure -> {
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    Log.i(TAG, "Media Outcome.Failure")
                }
            }
        })
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moebooru")
        if (mayRequestStoragePermission(mainActivity, 0)) {
            mediaViewModel.loadMedia(dir.absolutePath)
        }
    }
    private fun initGalleryView(view: View) {
        view.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.window_background))
        toolbar.setTitle(R.string.local_gallery)
        spanCount = mainActivity.screenWidth/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        if (spanCount == 0) spanCount = 1
        galleryView = view.findViewById(R.id.gallery)
        galleryView.layoutManager = GridLayoutManager(this.requireContext(), spanCount,
                GridLayoutManager.VERTICAL, false)
        galleryView.setHasFixedSize(true)
        galleryView.itemAnimator = DefaultItemAnimator()
        galleryView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        galleryView.setItemViewCacheSize(20)
        galleryView.isDrawingCacheEnabled = true
        galleryView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val glideRequests: GlideRequests = GlideApp.with(this)
        galleryAdapter = GalleryAdapter(glideRequests)
        galleryView.adapter = galleryAdapter
        galleryView.addOnItemTouchListener(RecyclerViewClickListener(this.requireContext(),
                object : RecyclerViewClickListener.OnItemClickListener {
                    override fun onItemClick(itemView: View?, position: Int) {
                        GalleryDialog(mainActivity, media, position).show()
                    }

                    override fun onItemLongClick(itemView: View?, position: Int) {

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

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.drawer.drawerLayout.addDrawerListener(drawerListener)
    }

    private class GalleryDialog(context: Context,
                                private val media: MutableList<MediaStoreData>,
                                private val position: Int) :
            ToolbarDialog(context, R.layout.layout_local_gallery_pager) {

        companion object {
            private const val TAG = "GalleryDialog"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            toolbar.title = "Gallery"
            toolbar.inflateMenu(R.menu.menu_gallery_dialog)
        }
    }
}