/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.viewpager.widget.PagerAdapter
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