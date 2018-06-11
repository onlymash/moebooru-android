package im.mash.moebooru.detail.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.adapter.TagAdapter
import im.mash.moebooru.util.screenWidth
import im.mash.moebooru.util.toolbarHeight

class TagFragment : Fragment() {

    private var tags: MutableList<String> = mutableListOf()

    private lateinit var tagView: RecyclerView
    private lateinit var tagAdapter: TagAdapter

    private var spanCount = 1
    private var position = 0
    private var type = "post"

    private val detailActivity by lazy { activity as DetailActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_details_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setPadding(0, detailActivity.paddingTop + detailActivity.toolbarHeight, 0, detailActivity.paddingBottom)
        spanCount = detailActivity.screenWidth/detailActivity.resources.getDimension(R.dimen.tag_item_width).toInt()
        if (spanCount == 0) {
            spanCount = 1
        }
        tagView = view.findViewById(R.id.rv_details_tags)
        tagView.layoutManager = GridLayoutManager(this.requireContext(), spanCount, GridLayoutManager.VERTICAL, false)
        tagView.setHasFixedSize(true)
        tagAdapter = TagAdapter()
        tagView.adapter = tagAdapter
        position = detailActivity.position
        type = detailActivity.type
        setData()
        detailActivity.positionViewModel.getPosition().observe(this, Observer {  pos ->
            if (pos != null) {
                position = pos
                setData()
            }
        })
    }

    private fun setData() {
        when (type) {
            "post" -> {
                val tagsList = detailActivity.posts[position].tags?.split(" ")?.toMutableList()
                if (tagsList != null) {
                    tags = tagsList
                    tagAdapter.updateData(tags)
                }
            }
            else -> {
                val tagsList = detailActivity.postsSearch[position].tags?.split(" ")?.toMutableList()
                if (tagsList != null) {
                    tags = tagsList
                    tagAdapter.updateData(tags)
                }
            }
        }
    }
}