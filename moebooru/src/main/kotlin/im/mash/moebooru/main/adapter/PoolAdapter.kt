package im.mash.moebooru.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Pool
import im.mash.moebooru.util.formatDate
import java.text.SimpleDateFormat
import java.util.*

class PoolAdapter : RecyclerView.Adapter<PoolAdapter.PoolViewHolder>() {

    private var pools: MutableList<Pool> = mutableListOf()

    fun updateData(pools: MutableList<Pool>) {
        this.pools = pools
        notifyDataSetChanged()
    }

    fun addData(pools: MutableList<Pool>) {
        val beforeCount = itemCount
        this.pools = pools
        notifyItemRangeInserted(beforeCount, pools.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoolViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_pool_item, parent, false)
        return PoolViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pools.size
    }

    override fun onBindViewHolder(holder: PoolViewHolder, position: Int)  {
        holder.bind(pools[position])
        holder.itemView.setOnClickListener {
            itemClickListener?.onClickItem("pool:${pools[position].id}")
        }
    }

    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onClickItem(keyword: String)
    }

    class PoolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poolName: TextView = itemView.findViewById(R.id.tv_pool_name)
        private val poolDescription: TextView = itemView.findViewById(R.id.tv_pool_description)
        private val poolCount: TextView = itemView.findViewById(R.id.tv_pool_post_count)
        private val poolDate: TextView = itemView.findViewById(R.id.tv_pool_created_at)

        fun bind(pool: Pool) {
            poolName.text = pool.name
            if (pool.description != null) {
                poolDescription.visibility = View.VISIBLE
                poolDescription.text = pool.description
            } else {
                poolDescription.visibility = View.GONE
            }
            poolCount.text = pool.post_count.toString()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.ENGLISH)
            poolDate.text = formatDate(sdf.parse(pool.created_at).time)
        }
    }
}