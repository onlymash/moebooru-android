package im.mash.moebooru.main.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import im.mash.moebooru.common.data.media.entity.MediaStoreData

class GalleryPagerAdapter(private var media: MutableList<MediaStoreData>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return media.size
    }
}