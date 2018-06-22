package im.mash.moebooru.detail.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.widget.AccordionTransformer
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.adapter.ImgAdapter
import im.mash.moebooru.util.logi

class PagerFragment : Fragment(), DetailActivity.PostsChangeListener {

    companion object {
        private const val TAG = "PagerFragment"
    }

    private lateinit var postPager: ViewPager
    private lateinit var postPagerAdapter: ImgAdapter

    private var position = 0

    private val detailActivity by lazy { activity as DetailActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logi(TAG, "onCreateView")
        return inflater.inflate(R.layout.layout_details_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logi(TAG, "onViewCreated")
        initPager(view)
    }

    private fun initPager(view: View) {
        postPager = view.findViewById(R.id.post_pager)
        postPager.setPageTransformer(true, AccordionTransformer())
        postPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                this@PagerFragment.position = position
                detailActivity.positionViewModel.setPosition(position)
            }
        })
        detailActivity.setPostsChangeListener(this)
    }

    override fun onPostsChanged(posts: MutableList<Post>, position: Int) {
        logi(TAG, "onPostsChanged")
        postPagerAdapter = ImgAdapter(detailActivity, posts, mutableListOf(), "post")
        postPagerAdapter.setPhotoViewListener(object : ImgAdapter.PhotoViewListener {
            override fun onClickListener(position: Int) {
                detailActivity.setBg()
            }
        })
        postPager.adapter = postPagerAdapter
        postPager.currentItem = position
    }

    override fun onPostsSearchChanged(postsSearch: MutableList<PostSearch>, position: Int) {
        logi(TAG, "onPostsSearchChanged")
        postPagerAdapter = ImgAdapter(detailActivity, mutableListOf(), postsSearch, "search")
        postPagerAdapter.setPhotoViewListener(object : ImgAdapter.PhotoViewListener {
            override fun onClickListener(position: Int) {
                detailActivity.setBg()
            }
        })
        postPager.adapter = postPagerAdapter
        postPager.currentItem = position
    }

    override fun onPause() {
        super.onPause()
        logi(TAG, "onPause")
    }

    override fun onStart() {
        super.onStart()
        logi(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        logi(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logi(TAG, "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        logi(TAG, "onResume")
    }
}