package im.mash.moebooru.detail.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.adapter.TagAdapter
import im.mash.moebooru.helper.getViewModel
import im.mash.moebooru.main.viewmodel.TagViewModel
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.util.logi
import im.mash.moebooru.util.screenWidth
import im.mash.moebooru.util.toolbarHeight

class TagFragment : Fragment(), DetailActivity.TagsChangeListener {

    companion object {
        private const val TAG = "TagFragment"
    }

    private var tags: MutableList<String> = mutableListOf()

    private lateinit var tagView: RecyclerView
    private lateinit var tagAdapter: TagAdapter

    private var spanCount = 1
    private var type = "post"

    private val detailActivity by lazy { activity as DetailActivity }
    private val tagViewModel: TagViewModel by lazy { this.getViewModel<TagViewModel>(detailActivity.tagViewModelFactory) }

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
        tagAdapter.setTagItemClickListener(object : TagAdapter.TagItemClickListener {

            override fun onClickItem(tag: String) {
                val intent = Intent(this@TagFragment.requireContext(), SearchActivity::class.java)
                intent.putExtra("keyword", tag)
                startActivity(intent)
            }

            override fun onLongClickItem(tag: String) {
                val cm = this@TagFragment.requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val cd = ClipData.newPlainText("Tag", tag)
                cm.primaryClip = cd
            }

            override fun onClickAdd(tag: String) {
                tagViewModel.saveTag(Tag(null, app.settings.activeProfileHost, tag, false))
            }

            override fun onClickAddNegated(tag: String) {
                tagViewModel.saveTag(Tag(null, app.settings.activeProfileHost, "-$tag", false))
            }

        })
        detailActivity.setTagsChangeListener(this)
    }

    override fun onTagsChanged(post: Any) {
        updateTags(post)
    }

    private fun updateTags(post: Any) {
        when (post) {
            is Post -> {
                type = "post"
                val tagsString = post.tags
                if (tagsString == "") {
                    return
                }
                tags = tagsString.split(" ").toMutableList()
                tagAdapter.updateData(tags)
            }
            is PostSearch -> {
                type = "search"
                val tagsString = post.tags
                if (tagsString == "") {
                    return
                }
                tags = tagsString.split(" ").toMutableList()
                tagAdapter.updateData(tags)
            }
        }
    }
}