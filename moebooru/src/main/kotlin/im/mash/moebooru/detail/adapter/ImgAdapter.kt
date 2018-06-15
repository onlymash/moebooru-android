package im.mash.moebooru.detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.widget.photoview.PhotoView
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.util.userAgent
import me.panpf.sketch.Sketch
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.decode.ImageAttrs
import me.panpf.sketch.http.HurlStack
import me.panpf.sketch.request.CancelCause
import me.panpf.sketch.request.DisplayListener
import me.panpf.sketch.request.ErrorCause
import me.panpf.sketch.request.ImageFrom

class ImgAdapter(private val context: Context) : PagerAdapter() {

    private var posts: MutableList<Post> = mutableListOf()
    private var postsSearch: MutableList<PostSearch> = mutableListOf()
    private var type = "post"
    private var size = Settings.POST_SIZE_SAMPLE
    private val imageLoader = app.settings.imageLoader

    init {
        if (imageLoader == Settings.IMAGE_LOADER_SKETCH) {
            val hurlStack = HurlStack()
            hurlStack.userAgent = app.userAgent
            Sketch.with(app).configuration.httpStack = hurlStack
        }
    }

    override fun getCount(): Int {
        return posts.size
    }

    fun updateData(posts: MutableList<Post>, size: String) {
        this.posts = posts
        type = "post"
        this.size = size
        notifyDataSetChanged()
    }

    fun updateSearchData(posts: MutableList<PostSearch>, size: String) {
        this.postsSearch = posts
        type = "search"
        this.size = size
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        when (imageLoader) {
            Settings.IMAGE_LOADER_GLIDE -> {
                val view = LayoutInflater.from(container.context)
                        .inflate(R.layout.layout_post_img, null)
                val photoView = view.findViewById<PhotoView>(R.id.post_img)
                photoView.enable()
                photoView.enableRotate()
                photoView.setOnClickListener {
                    listener?.onClickListener(position)
                }
                val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
                val url = when (type) {
                    "post" -> {
                        when (size) {
                            Settings.POST_SIZE_SAMPLE -> posts[position].sample_url
                            Settings.POST_SIZE_LARGER -> posts[position].jpeg_url
                            else -> posts[position].file_url
                        }
                    }
                    else -> {
                        when (size) {
                            Settings.POST_SIZE_SAMPLE -> postsSearch[position].sample_url
                            Settings.POST_SIZE_LARGER -> postsSearch[position].jpeg_url
                            else -> postsSearch[position].file_url
                        }
                    }
                }
                GlideApp.with(context)
                        .load(url)
                        .fitCenter()
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                                         dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                                      isFirstResource: Boolean): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(photoView)
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                return view
            }
            else -> {
                val view = LayoutInflater.from(container.context)
                        .inflate(R.layout.layout_post_sketch, null)
                val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
                val sketchImageView = view.findViewById<SketchImageView>(R.id.post_img)
                sketchImageView.setOnClickListener {
                    listener?.onClickListener(position)
                }
                sketchImageView.isZoomEnabled = true
                sketchImageView.displayListener = object : DisplayListener {
                    override fun onStarted() {

                    }
                    override fun onCanceled(cause: CancelCause) {
                        progressBar.visibility = View.INVISIBLE
                    }
                    override fun onError(cause: ErrorCause) {
                        progressBar.visibility = View.INVISIBLE
                    }
                    override fun onCompleted(drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) {
                        progressBar.visibility = View.INVISIBLE
                    }
                }
                val url = when (type) {
                    "post" -> {
                        when (size) {
                            Settings.POST_SIZE_SAMPLE -> posts[position].sample_url
                            Settings.POST_SIZE_LARGER -> posts[position].jpeg_url
                            else -> posts[position].file_url
                        }
                    }
                    else -> {
                        when (size) {
                            Settings.POST_SIZE_SAMPLE -> postsSearch[position].sample_url
                            Settings.POST_SIZE_LARGER -> postsSearch[position].jpeg_url
                            else -> postsSearch[position].file_url
                        }
                    }
                }
                sketchImageView.displayImage(url)
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                return view
            }
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    interface PhotoViewListener {
        fun onClickListener(position: Int)
    }

    private var listener: PhotoViewListener? = null

    fun setPhotoViewListener(listener: PhotoViewListener) {
        this.listener = listener
    }
}