package im.mash.moebooru.detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.viewpager.widget.PagerAdapter
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
import im.mash.moebooru.glide.MoeGlideUrl

class ImgAdapter(private val context: Context, private val posts: MutableList<Post>,
                 private val postsSearch: MutableList<PostSearch>,
                 private val type: String) : PagerAdapter() {

    private var imgSize = app.settings.postSizeBrowse

    override fun getCount(): Int {
        return when (type) {
            "post" -> posts.size
            else -> postsSearch.size
        }
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.layout_post_img, null)
        val photoView = view.findViewById<PhotoView>(R.id.post_img)
        photoView.enable()
        photoView.enableRotate()
        photoView.maxScale = 2f
        photoView.setOnClickListener {
            listener?.onClickListener(position)
        }
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        val url = when (type) {
            "post" -> {
                when (imgSize) {
                    Settings.POST_SIZE_SAMPLE -> posts[position].sample_url
                    Settings.POST_SIZE_LARGER -> posts[position].getJpegUrl()
                    else -> posts[position].getFileUrl()
                }
            }
            else -> {
                when (imgSize) {
                    Settings.POST_SIZE_SAMPLE -> postsSearch[position].sample_url
                    Settings.POST_SIZE_LARGER -> postsSearch[position].getJpegUrl()
                    else -> postsSearch[position].getFileUrl()
                }
            }
        }
        GlideApp.with(context)
                .load(MoeGlideUrl(url))
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