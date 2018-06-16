package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import java.lang.Exception

class GalleryPagerAdapter(private var media: MutableList<MediaStoreData>) : PagerAdapter() {

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
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.layout_post_local, null)
        val imageView = view.findViewById<SubsamplingScaleImageView>(R.id.post_img)
        imageView.setOnClickListener {
            listener?.onClickListener(position)
        }
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        imageView.setOnImageEventListener(object : SubsamplingScaleImageView.OnImageEventListener {
            override fun onImageLoaded() {
                progressBar.visibility = View.INVISIBLE
            }
            override fun onReady() {

            }
            override fun onTileLoadError(e: Exception?) {

            }
            override fun onPreviewReleased() {

            }
            override fun onImageLoadError(e: Exception?) {
                progressBar.visibility = View.INVISIBLE
            }
            override fun onPreviewLoadError(e: Exception?) {
            }

        })
        imageView.setImage(ImageSource.uri(media[position].uri))
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return view
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