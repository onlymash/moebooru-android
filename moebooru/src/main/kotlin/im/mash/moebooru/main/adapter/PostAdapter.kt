package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.glide.MoeGlideUrl
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.util.*

class PostAdapter(private val activity: MainActivity) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    companion object {
        private const val TAG = "PostAdapter"
    }

    private var posts = mutableListOf<Post>()
    private var spanCount = activity.screenWidth/activity.resources.getDimension(R.dimen.item_width).toInt()
    private val padding = activity.resources.getDimension(R.dimen.item_padding).toInt()

    private var idsOneTwo: MutableList<Int> = mutableListOf()
    private var idsThree: MutableList<Int> = mutableListOf()

    fun updateData(posts: MutableList<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    fun updateVoteIdsOneTwo(idsOneTwo: MutableList<Int>) {
        this.idsOneTwo = idsOneTwo
    }

    fun updateVoteIdsThree(idsThree: MutableList<Int>) {
        this.idsThree = idsThree
    }

    fun addData(posts: MutableList<Post>) {
        logi(TAG, "addData")
        val countBefore = itemCount
        this.posts = posts
        notifyItemRangeInserted(countBefore, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_post_item_bar, parent, false)
        return PostViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        if (position in 0 until spanCount) {
            holder.itemView.setPadding(padding, padding + activity.toolbarHeight + activity.paddingTop, padding, padding)
        } else {
            holder.itemView.setPadding(padding, padding, padding, padding)
        }
        val placeHolderId = when (posts[position].rating) {
            "q" -> R.drawable.background_rating_q
            "e" -> R.drawable.background_rating_e
            else -> R.drawable.background_rating_s
        }
        val id = posts[position].id
        holder.id.text = "#$id"
        holder.res.text = "${posts[position].width} x ${posts[position].height}"
        if (!app.settings.showInfoBar) holder.bar.visibility = View.GONE else holder.bar.visibility = View.VISIBLE
        when (app.settings.enabledStaggered) {
            false -> {
                val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                lp.dimensionRatio = "H, 1:1"
                holder.post.layoutParams = lp
                GlideApp.with(holder.itemView)
                        .load(MoeGlideUrl(posts[position].preview_url))
                        .centerCrop()
                        .placeholder(activity.resources.getDrawable(placeHolderId, activity.theme))
                        .into(holder.post)
            }
            else -> {
                val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                lp.dimensionRatio = "H, ${posts[position].actual_preview_width}:${posts[position].actual_preview_height}"
                holder.post.layoutParams = lp
                GlideApp.with(holder.itemView)
                        .load(MoeGlideUrl(posts[position].preview_url))
                        .fitCenter()
                        .placeholder(activity.resources.getDrawable(placeHolderId, activity.theme))
                        .into(holder.post)
            }
        }
        activity.sharedPreferences.registerOnSharedPreferenceChangeListener {_, key: String ->
            when (key) {
                Settings.SHOW_INFO_BAR -> if (!app.settings.showInfoBar) holder.bar.visibility = View.GONE else holder.bar.visibility = View.VISIBLE
                Settings.STAGGERED_GRID -> {
                    when (app.settings.enabledStaggered) {
                        false -> {
                            val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                            lp.dimensionRatio = "H, 1:1"
                            holder.post.layoutParams = lp
                            GlideApp.with(holder.itemView)
                                    .load(MoeGlideUrl(posts[position].preview_url))
                                    .centerCrop()
                                    .placeholder(activity.resources.getDrawable(placeHolderId, activity.theme))
                                    .into(holder.post)
                        }
                        else -> {
                            val lp = holder.post.layoutParams as ConstraintLayout.LayoutParams
                            lp.dimensionRatio = "H, ${posts[position].actual_preview_width}:${posts[position].actual_preview_height}"
                            holder.post.layoutParams = lp
                            GlideApp.with(holder.itemView)
                                    .load(MoeGlideUrl(posts[position].preview_url))
                                    .fitCenter()
                                    .placeholder(activity.resources.getDrawable(placeHolderId, activity.theme))
                                    .into(holder.post)
                        }
                    }
                }
            }
        }
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

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val post: ImageView = itemView.findViewById(R.id.post_item)
        val rate: ImageView = itemView.findViewById(R.id.rate)
        val bar: LinearLayout = itemView.findViewById(R.id.bar)
        val id: TextView = itemView.findViewById(R.id.post_id)
        val res: TextView = itemView.findViewById(R.id.post_res)
    }
}