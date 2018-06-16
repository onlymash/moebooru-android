package im.mash.moebooru.detail.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.toolbarHeight
import java.util.*


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

    private var type = "post"
    private var post: Post? = null
    private var postSearch: PostSearch? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        type = detailActivity.type
        initInfo(detailActivity.position)
        detailActivity.positionViewModel.getPosition().observe(this, Observer { position ->
            if (position != null && (position < detailActivity.postsSearch.size || position < detailActivity.posts.size)) {
                logi(TAG, "$position")
                initInfo(position)
            }
        })
    }

    private fun initInfo(position: Int) {
        when (type) {
            "post" -> {
                post = detailActivity.posts[position]
                if (post == null) {
                    return
                }
                id.text = post!!.id.toString()
                author.text = post!!.author
                val cal = Calendar.getInstance(Locale.getDefault())
                cal.timeInMillis = (post!!.created_at * 1000).toLong()
                createdAt.text = DateFormat.format("yyyy-MM-dd HH:mm", cal)
                creatorId.text = post!!.creator_id.toString()
                val hw = "${post!!.height} x ${post!!.width}"
                heightWidth.text = hw
                if (post!!.source != null) {
                    source.text = post!!.source
                } else {
                    source.text = ""
                }
                rating.text = post!!.rating
                score.text = post!!.score.toString()
                if (post!!.parent_id != null) {
                    parentIdLayout.visibility = View.VISIBLE
                } else {
                    parentIdLayout.visibility = View.GONE
                }
                sampleSize.text = Formatter.formatFileSize(detailActivity, post!!.sample_file_size.toLong())

                largerSize.text = Formatter.formatFileSize(detailActivity, post!!.jpeg_file_size.toLong())

                originSize.text = Formatter.formatFileSize(detailActivity, post!!.file_size.toLong())
            }
            else -> {
                postSearch = detailActivity.postsSearch[position]
                if (postSearch == null) {
                    return
                }
                logi(TAG, "postSearch.site: ${postSearch!!.site}")
                id.text = postSearch!!.id.toString()
                author.text = postSearch!!.author
                val cal = Calendar.getInstance(Locale.getDefault())
                cal.timeInMillis = (postSearch!!.created_at * 1000).toLong()
                createdAt.text = DateFormat.format("yyyy-MM-dd HH:mm", cal)
                creatorId.text = postSearch!!.creator_id.toString()
                val hw = "${postSearch!!.height} x ${postSearch!!.width}"
                heightWidth.text = hw
                if (postSearch!!.source != null) {
                    source.text = postSearch!!.source
                } else {
                    source.text = ""
                }
                rating.text = postSearch!!.rating
                score.text = postSearch!!.score.toString()
                if (postSearch!!.parent_id != null) {
                    parentIdLayout.visibility = View.VISIBLE
                } else {
                    parentIdLayout.visibility = View.GONE
                }
                sampleSize.text = Formatter.formatFileSize(detailActivity, postSearch!!.sample_file_size.toLong())

                largerSize.text = Formatter.formatFileSize(detailActivity, postSearch!!.jpeg_file_size.toLong())

                originSize.text = Formatter.formatFileSize(detailActivity, postSearch!!.file_size.toLong())
            }
        }
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