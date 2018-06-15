package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
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
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.widget.photoview.PhotoView
import im.mash.moebooru.glide.GlideApp
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.decode.ImageAttrs
import me.panpf.sketch.request.CancelCause
import me.panpf.sketch.request.DisplayListener
import me.panpf.sketch.request.ErrorCause
import me.panpf.sketch.request.ImageFrom

class GalleryPagerAdapter(private var media: MutableList<MediaStoreData>) : PagerAdapter() {

    private val imageLoader = app.settings.imageLoader

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return media.size
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
                GlideApp.with(container.context)
                        .load(media[position].uri)
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
                val sketchImageView = view.findViewById<SketchImageView>(R.id.post_img)
                sketchImageView.setOnClickListener {
                    listener?.onClickListener(position)
                }
                val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
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
                sketchImageView.displayImage(media[position].mediaData)
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                return view
            }
        }
    }

    fun updateData(media: MutableList<MediaStoreData>) {
        this.media = media
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    interface PhotoViewListener {
        fun onClickListener(position: Int)
    }

    private var listener: PhotoViewListener? = null

    fun setPhotoViewListener(listener: PhotoViewListener) {
        this.listener = listener
    }
}