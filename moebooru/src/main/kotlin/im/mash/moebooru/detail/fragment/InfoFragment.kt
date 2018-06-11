package im.mash.moebooru.detail.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.util.toolbarHeight


class InfoFragment : Fragment() {

    companion object {
        private const val TAG = "InfoFragment"
    }

    private lateinit var id: TextView
    private lateinit var createdAt: TextView
    private lateinit var author: TextView
    private lateinit var creatorId: TextView
    private lateinit var heightWidth: TextView
    private lateinit var source: TextView
    private lateinit var rating: TextView
    private lateinit var score: TextView

    private lateinit var parentId: ImageButton
    private lateinit var parentIdLayout: LinearLayout

    private lateinit var sampleDownload: ImageButton
    private lateinit var sampleOpen: ImageButton
    private lateinit var sampleSize: TextView
    private lateinit var sampleLayout: LinearLayout

    private lateinit var largerDownload: ImageButton
    private lateinit var largerOpen: ImageButton
    private lateinit var largerSize: TextView
    private lateinit var largerLayout: LinearLayout

    private lateinit var originDownload: ImageButton
    private lateinit var originOpen: ImageButton
    private lateinit var originSize: TextView
    private lateinit var originLayout: LinearLayout

    private lateinit var scrollView: ScrollView

    private val detailActivity by lazy { activity as DetailActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        val padding = detailActivity.resources.getDimension(R.dimen.list_padding).toInt()
        view.setPadding(padding, detailActivity.toolbarHeight + detailActivity.paddingTop + padding, padding, detailActivity.paddingBottom )

        id = view.findViewById(R.id.tv_id)
        author = view.findViewById(R.id.tv_author)
        createdAt = view.findViewById(R.id.tv_created_at)
        creatorId = view.findViewById(R.id.tv_creator_id)
        heightWidth = view.findViewById(R.id.tv_height_width)
        source = view.findViewById(R.id.tv_source)
        rating = view.findViewById(R.id.tv_rating)
        score = view.findViewById(R.id.tv_score)
        parentId = view.findViewById(R.id.btn_parent_id)
        parentIdLayout = view.findViewById(R.id.container_parent_id)

        sampleDownload = view.findViewById(R.id.btn_download_sample)
        sampleOpen = view.findViewById(R.id.btn_browser_sample)
        sampleSize = view.findViewById(R.id.tv_sample_size)
        sampleLayout = view.findViewById(R.id.sample_layout)

        largerDownload = view.findViewById(R.id.btn_download_larger)
        largerOpen = view.findViewById(R.id.btn_browser_larger)
        largerSize = view.findViewById(R.id.tv_larger_size)
        largerLayout = view.findViewById(R.id.larger_layout)

        originDownload = view.findViewById(R.id.btn_download_origin)
        originOpen = view.findViewById(R.id.btn_browser_origin)
        originSize = view.findViewById(R.id.tv_origin_size)
        originLayout = view.findViewById(R.id.origin_layout)

        scrollView = view.findViewById(R.id.scroll_view)
    }
}