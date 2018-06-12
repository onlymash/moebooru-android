package im.mash.moebooru.detail.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.core.widget.AccordionTransformer
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.adapter.ImgAdapter

class PagerFragment : Fragment() {
    companion object {
        private const val TAG = "PagerFragment"
    }

    private lateinit var postPager: ViewPager
    private lateinit var postPagerAdapter: ImgAdapter

    private val detailActivity by lazy { activity as DetailActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postPager = view.findViewById(R.id.post_pager)
        postPagerAdapter = ImgAdapter(detailActivity)
        postPager.adapter = postPagerAdapter
        postPager.setPageTransformer(true, AccordionTransformer())
        postPagerAdapter.setPhotoViewListener(object : ImgAdapter.PhotoViewListener {
            override fun onClickListener(position: Int) {
                detailActivity.setBg()
            }
        })
        when (detailActivity.type) {
            "post" -> {
                postPagerAdapter.updateData(detailActivity.posts, app.settings.postSizeBrowse)
                postPager.currentItem = detailActivity.position
            }
            else -> {
                postPagerAdapter.updateSearchData(detailActivity.postsSearch, app.settings.postSizeBrowse)
                postPager.currentItem = detailActivity.position
            }
        }
        postPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                detailActivity.positionViewModel.setPosition(position)
                detailActivity.setToolbarTitle(position)
            }
        })
    }
}