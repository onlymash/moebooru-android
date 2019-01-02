package im.mash.moebooru.main.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.*
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.mash.moebooru.App.Companion.getMoePath
import im.mash.moebooru.R
import im.mash.moebooru.common.base.RecyclerViewClickListener
import im.mash.moebooru.common.base.SafeGridLayoutManager
import im.mash.moebooru.common.base.ToolbarDialog
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.widget.AccordionTransformer
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.glide.GlideRequests
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.adapter.GalleryAdapter
import im.mash.moebooru.main.adapter.GalleryDrawerAdapter
import im.mash.moebooru.main.adapter.GalleryPagerAdapter
import im.mash.moebooru.main.viewmodel.MediaViewModel
import im.mash.moebooru.util.formatDate
import im.mash.moebooru.util.logi
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

    private lateinit var drawerBooruView: RecyclerView
    private lateinit var drawerBooruAdapter: GalleryDrawerAdapter

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
        drawerBooruView = view.findViewById(R.id.drawer_rv_list)
        drawerBooruView.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        drawerBooruAdapter = GalleryDrawerAdapter()
        drawerBooruView.adapter = drawerBooruAdapter
        drawerBooruAdapter.updateData(mainActivity.boorus)
        drawerBooruAdapter.setItemClickListener(object : GalleryDrawerAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                if (position == 0) {
                    if (mayRequestStoragePermission(mainActivity, 0)) {
                        mediaViewModel.loadMedia(getMoePath())
                    }
                } else if (position > 0) {
                    if (mayRequestStoragePermission(mainActivity, 0)) {
                        val path = getMoePath() + File.separator + mainActivity.boorus[position - 1].host
                        mediaViewModel.loadMedia(path)
                    }
                }
                drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        })
    }

    private fun initViewModel() {
        mediaViewModel.mediaOutcome.observe(this, Observer<Outcome<MutableList<MediaStoreData>>> { outcome ->
            when (outcome) {
                is Outcome.Progress -> {
                    logi(TAG, "Media Outcome.Progress")
                }
                is Outcome.Success -> {
                    logi(TAG, "Media Outcome.Success")
                    media = outcome.data
                    galleryAdapter.updateData(media)
                }
                is Outcome.Failure -> {
                    if (outcome.e is IOException) {
                        outcome.e.printStackTrace()
                    }
                    logi(TAG, "Media Outcome.Failure")
                }
            }
        })

        if (mayRequestStoragePermission(mainActivity, 0)) {
            mediaViewModel.loadMedia(getMoePath())
        }
    }
    private fun initGalleryView(view: View) {
        view.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.window_background))
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.transparent))
        toolbar.setTitle(R.string.local_gallery)
        spanCount = mainActivity.screenWidth/mainActivity.resources.getDimension(R.dimen.item_width).toInt()
        if (spanCount == 0) spanCount = 1
        galleryView = view.findViewById(R.id.gallery)
        galleryView.layoutManager = SafeGridLayoutManager(this.requireContext(), spanCount, GridLayoutManager.VERTICAL, false)
        galleryView.setHasFixedSize(true)
        galleryView.itemAnimator = DefaultItemAnimator()
        galleryView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this.requireContext(), R.anim.layout_animation)
        galleryView.setItemViewCacheSize(20)
        val glideRequests: GlideRequests = GlideApp.with(this)
        galleryAdapter = GalleryAdapter(this.requireContext(), spanCount, glideRequests)
        galleryView.adapter = galleryAdapter
        galleryView.addOnItemTouchListener(RecyclerViewClickListener(this.requireContext(),
                object : RecyclerViewClickListener.OnItemClickListener {
                    override fun onItemClick(itemView: View, position: Int) {
                        val dialog = GalleryDialog(mainActivity, media, position)
                        dialog.setItemRemoveListener(object : GalleryDialog.ItemRemoveListener {
                            override fun onItemRemoved(position: Int) {
                                media.removeAt(position)
                                galleryAdapter.updateData(media)
                            }
                        })
                        dialog.show()
                    }

                    override fun onItemLongClick(itemView: View, position: Int) {

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
                                private var position: Int) :
            ToolbarDialog(context, R.layout.layout_local_gallery_pager), ViewPager.OnPageChangeListener, Toolbar.OnMenuItemClickListener {

        companion object {
            private const val TAG = "GalleryDialog"
        }

        private lateinit var galleryPager: ViewPager
        private lateinit var galleryPagerAdapter: GalleryPagerAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            toolbar.title = getFileName(media[position])
            toolbar.inflateMenu(R.menu.menu_gallery_dialog)
            toolbar.setOnMenuItemClickListener(this)
            val bg: View = findViewById(R.id.bg)
            galleryPager = findViewById(R.id.gallery_pager)
            galleryPagerAdapter = GalleryPagerAdapter(media)
            galleryPager.adapter = galleryPagerAdapter
            galleryPager.currentItem = position
            galleryPager.addOnPageChangeListener(this)
            galleryPager.setPageTransformer(true, AccordionTransformer())
            galleryPagerAdapter.setPhotoViewListener(object : GalleryPagerAdapter.PhotoViewListener {
                override fun onClickListener(position: Int) {
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
            })
        }

        private fun getFileName(m: MediaStoreData): String {
            val path = m.mediaData
            return path.substring(path.lastIndexOf("/")+1)
        }

        private fun showBar() {
            val uiFlags = View.SYSTEM_UI_FLAG_VISIBLE
            window.decorView.systemUiVisibility = uiFlags
        }

        private fun hideBar() {
            val uiFlags = View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window!!.decorView.systemUiVisibility = uiFlags
        }

        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            this.position = position
            toolbar.title = getFileName(media[position])
        }

        @SuppressLint("InflateParams")
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_set_as -> {
                    val intent = Intent(Intent.ACTION_ATTACH_DATA)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra("mimeType", "image/*")
                    intent.data = media[position].uri
                    try {
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.set_as)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.action_share -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_STREAM, media[position].uri)
                    try {
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.action_delete -> {
                    AlertDialog.Builder(context)
                            .setTitle("Delete image")
                            .setMessage("Do you confirm the deletion of ${media[position].mediaData} ?")
                            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                                try {
                                    val file = File(media[position].mediaData)
                                    file.delete()
                                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))
                                    try {
                                        context.sendBroadcast(mediaScanIntent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    media.removeAt(position)
                                    listener?.onItemRemoved(position)
                                    galleryPagerAdapter.updateData(media)
                                    toolbar.title = getFileName(media[position])
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                            .setNegativeButton(context.getString(R.string.cancel), null)
                            .show()

                }
                R.id.action_edit -> {
                    val intent = Intent(Intent.ACTION_EDIT)
                    intent.setDataAndType(media[position].uri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.edit_img)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.action_info -> {
                    val view = layoutInflater.inflate(R.layout.layout_dialog_properties, null)
                    val linearLayout = view.findViewById<LinearLayout>(R.id.properties_holder)
                    setProperty("Mime type", media[position].mimeType, linearLayout)
                    setProperty("Date modified", formatDate(media[position].dateModified * 1000).toString(), linearLayout)
                    try {
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(media[position].mediaData, options)
                        setProperty("Resolution", "${options.outWidth} x ${options.outHeight}", linearLayout)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    setProperty("Path", media[position].mediaData, linearLayout)
                    val dialog = AlertDialog.Builder(context)
                            .setPositiveButton(R.string.ok, null)
                            .create()
                    dialog.apply {
                        setView(view)
                        requestWindowFeature(Window.FEATURE_NO_TITLE)
                        setCanceledOnTouchOutside(true)
                        show()
                    }
                }
            }
            return true
        }

        private fun setProperty(label: String, value: String, linearLayout: LinearLayout) {
            val v = layoutInflater.inflate(R.layout.layout_dialog_property_item, linearLayout, false)
                    .apply {
                        findViewById<TextView>(R.id.property_label).text = label
                        findViewById<TextView>(R.id.property_value).text = value
                    }
            linearLayout.addView(v)
        }

        private var listener: ItemRemoveListener? = null

        fun setItemRemoveListener(listener: ItemRemoveListener) {
            this.listener = listener
        }

        interface ItemRemoveListener {
            fun onItemRemoved(position: Int)
        }
    }
}