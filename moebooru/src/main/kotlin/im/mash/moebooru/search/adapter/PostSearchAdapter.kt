package im.mash.moebooru.search.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.glide.MoeGlideUrl
import im.mash.moebooru.util.*

class PostSearchAdapter(private val context: Context, private var gridMode: String) : RecyclerView.Adapter<PostSearchAdapter.PostSearchViewHolder>() {

    companion object {
        private const val TAG = "PostSearchAdapter"
    }

    private var posts = mutableListOf<PostSearch>()
    private var spanCount = context.screenWidth/context.resources.getDimension(R.dimen.item_width).toInt()
    private val padding = context.resources.getDimension(R.dimen.item_padding).toInt()

    private var idsOneTwo: MutableList<Int> = mutableListOf()
    private var idsThree: MutableList<Int> = mutableListOf()

    fun updateData(posts: MutableList<PostSearch>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    fun updateVoteIdsOneTwo(idsOneTwo: MutableList<Int>) {
        this.idsOneTwo.clear()
        this.idsOneTwo.addAll(idsOneTwo)
    }

    fun updateVoteIdsThree(idsThree: MutableList<Int>) {
        this.idsThree.clear()
        this.idsThree.addAll(idsThree)
    }

    fun setGridMode(gridMode: String) {
        this.gridMode = gridMode
    }

    fun addData(posts: MutableList<PostSearch>) {
        val countBefore = itemCount
        this.posts.clear()
        this.posts.addAll(posts)
        notifyItemRangeInserted(countBefore, itemCount)
    }

    fun clearData() {
        posts.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSearchViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_post_item, parent, false)
        return PostSearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostSearchViewHolder, position: Int) {
        if (position in 0 until spanCount) {
            holder.itemView.setPadding(padding, padding + context.toolbarHeight + statusBarHeight, padding, padding)
        } else {
            holder.itemView.setPadding(padding, padding, padding, padding)
        }
        val placeHolderId = when (posts[position].rating) {
            "q" -> R.drawable.background_rating_q
            "e" -> R.drawable.background_rating_e
            else -> R.drawable.background_rating_s
        }
        when (gridMode) {
            Settings.GRID_MODE_GRID -> {
                val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                lp.dimensionRatio = "H, 1:1"
                holder.post.layoutParams = lp
                GlideApp.with(context)
                        .load(MoeGlideUrl(posts[position].preview_url))
                        .centerCrop()
                        .placeholder(context.resources.getDrawable(placeHolderId, context.theme))
                        .into(holder.post)
            }
            else -> {
                val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                lp.dimensionRatio = "H, ${posts[position].actual_preview_width}:${posts[position].actual_preview_height}"
                holder.post.layoutParams = lp
                GlideApp.with(context)
                        .load(MoeGlideUrl(posts[position].preview_url))
                        .fitCenter()
                        .placeholder(context.resources.getDrawable(placeHolderId, context.theme))
                        .into(holder.post)
            }
        }
        val id = posts[position].id
        holder.rate.tag = id
        holder.itemView.setOnClickListener {
            postItemClickListener?.onClickPostItem(position)
        }
        holder.rate.setOnClickListener {
            postItemClickListener?.onClickRate(position, id, holder.rate)
        }
        if (idsThree.size > 0 && idsThree.contains(id)) {
            holder.rate.setImageResource(R.drawable.ic_action_star_24dp)
        } else if (idsOneTwo.size > 0 && idsOneTwo.contains(id)) {
            holder.rate.setImageResource(R.drawable.ic_action_star_half_24dp)
        } else {
            holder.rate.setImageResource(R.drawable.ic_action_star_border_24dp)
        }

    }

    private var postItemClickListener: PostItemClickListener? = null

    fun setPostItemClickListener(listener: PostItemClickListener) {
        this.postItemClickListener = listener
    }

    interface PostItemClickListener {
        fun onClickPostItem(position: Int)
        fun onClickRate(position: Int, id: Int, rate: ImageView)
    }

    inner class PostSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val post: ImageView = itemView.findViewById(R.id.post_item)
        val rate: ImageView = itemView.findViewById(R.id.rate)
    }
}