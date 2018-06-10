package im.mash.moebooru.detail.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R

class PagerFragment : Fragment() {
    companion object {
        private const val TAG = "PagerFragment"
    }

    private lateinit var postPager: ViewPager
//    private var postPagerAdapter: PostsPagerAdapter? = null
    private var tags: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postPager = view.findViewById(R.id.post_pager)
    }
}