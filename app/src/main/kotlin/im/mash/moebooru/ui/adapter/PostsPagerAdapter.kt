/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.DetailsActivity
import im.mash.moebooru.ui.widget.photoview.PhotoView
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.glideHeader

class PostsPagerAdapter(private var items: MutableList<RawPost>?,
                        private val postSize: String,
                        private val activity: DetailsActivity) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return items?.size?:0
    }

    fun updateData(items: MutableList<RawPost>?) {
        this.items = items
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.layout_post_img, null)
        val photoView = view.findViewById<PhotoView>(R.id.post_img)
        photoView.enable()
        photoView.enableRotate()
        photoView.setOnClickListener {
            activity.changeBackground()
        }
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        val url = when (postSize) {
            Key.POST_SIZE_LARGER -> items!![position].jpeg_url
            Key.POST_SIZE_ORIGIN -> items!![position].file_url
            else -> items!![position].sample_url
        }
        GlideApp.with(container.context)
                .load(GlideUrl(url, glideHeader))
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?,
                                                 target: Target<Drawable>?, dataSource: DataSource?,
                                                 isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(photoView)
        container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}