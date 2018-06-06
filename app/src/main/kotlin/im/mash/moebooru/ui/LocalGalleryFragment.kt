package im.mash.moebooru.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.glide.GlideRequests
import im.mash.moebooru.model.MediaStoreData
import im.mash.moebooru.loader.MediaStoreDataLoader
import im.mash.moebooru.ui.adapter.LocalGalleryAdapter
import im.mash.moebooru.ui.listener.RecyclerViewClickListener
import im.mash.moebooru.utils.mayRequestStoragePermission
import java.io.File

@SuppressLint("RtlHardcoded")
class LocalGalleryFragment : ToolbarFragment(), DrawerLayout.DrawerListener,
        LoaderManager.LoaderCallbacks<MutableList<MediaStoreData>> {

    companion object {
        private const val TAG = "LocalGalleryFragment"
        var mediaStoreDataList: MutableList<MediaStoreData> = mutableListOf()
    }

    private lateinit var toolbar: Toolbar
    private lateinit var drawer: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var appBarLayoutRight: AppBarLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var localGalleryAdapter: LocalGalleryAdapter

    private var spanCount = 0

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mayRequestStoragePermission(activity as MainActivity, 0)) {
            loaderManager.initLoader(R.id.loader_id_media_store_data, null, this)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_local_gallery, container, false)
        toolbar = inflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        drawerToolbar = inflater.inflate(R.layout.layout_toolbar_drawer, null) as Toolbar
        recyclerView = view.findViewById(R.id.gallery)
        spanCount = mainActivity.widthScreen/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        recyclerView.layoutManager = GridLayoutManager(this.requireContext(), spanCount,
                GridLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary))
        toolbar.setTitle(R.string.local_gallery)
        setInsetsListener(toolbar)
        initRightDrawer(view)
//        loaderManager.restartLoader(R.id.loader_id_media_store_data, null, this)

        val itemClickListener = object : RecyclerViewClickListener.OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {

            }

            override fun onItemLongClick(itemView: View?, position: Int) {

            }
        }
        val itemTouchListener = RecyclerViewClickListener(this.requireContext(), itemClickListener)
        recyclerView.addOnItemTouchListener(itemTouchListener)
    }

    private fun initRightDrawer(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout_local_gallery)
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawer = view.findViewById(R.id.right_drawer_view)
        appBarLayoutRight = view.findViewById(R.id.appbar_layout_drawer)
        appBarLayoutRight.addView(drawerToolbar)
        val rightDrawerViewLayout = view.findViewById<LinearLayout>(R.id.drawer_list_layout)
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) {_, insets ->
            val statusBarSize = insets.systemWindowInsetTop
            rightDrawerViewLayout.setPadding(0, mainActivity.toolbarHeight + statusBarSize, 0, insets.systemWindowInsetBottom)
            drawerToolbar.setPadding(0, statusBarSize, 0, 0)
            appBarLayoutRight.minimumHeight = mainActivity.toolbarHeight + statusBarSize
            appBarLayoutRight.removeView(drawerToolbar)
            appBarLayoutRight.addView(drawerToolbar)
            insets
        }
        mainActivity.drawer.drawerLayout.addDrawerListener(this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<MutableList<MediaStoreData>> {
        Log.i(TAG, "onCreateLoader")
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moebooru")
        return MediaStoreDataLoader(this.requireContext(), dir.absolutePath)
    }

    override fun onLoadFinished(loader: Loader<MutableList<MediaStoreData>>,
                                data: MutableList<MediaStoreData>) {
        Log.i(TAG, "onLoadFinished")
        mediaStoreDataList = data
        val glideRequests: GlideRequests = GlideApp.with(this)
        localGalleryAdapter = LocalGalleryAdapter(this.requireContext(), data, glideRequests)
        val preloader = RecyclerViewPreloader(glideRequests, localGalleryAdapter, localGalleryAdapter, 3)
        recyclerView.addOnScrollListener(preloader)
        recyclerView.adapter = localGalleryAdapter
    }

    override fun onLoaderReset(loader: Loader<MutableList<MediaStoreData>>) {
        Log.i(TAG, "onLoaderReset")
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

    override fun onDrawerStateChanged(newState: Int) {
        closeRightDrawer()
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

    }

    override fun onDrawerClosed(drawerView: View) {

    }

    override fun onDrawerOpened(drawerView: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.drawer.drawerLayout.removeDrawerListener(this)
    }
}