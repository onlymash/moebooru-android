package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.util.ColorUtil
import im.mash.moebooru.util.TextUtil

class BooruAdapter(private val context: Context, private var boorus: MutableList<Booru>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "BooruAdapter"
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_DETAIL = 1
    }

    private var detailPosition = -1
    private var booruChangeListener: BooruChangeListener? = null

    fun updateData(boorus: MutableList<Booru>) {
        this.boorus = boorus
        notifyDataSetChanged()
    }

    fun setBooruChangeListener(listener: BooruChangeListener) {
        booruChangeListener = listener
    }

    interface BooruChangeListener {
        fun onBooruEdit(booru: Booru)
        fun onBooruDelete(booru: Booru)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_booru_item, parent, false)
                BooruViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_booru_item_detail, parent, false)
                BooruDetailViewHolder(itemView)
            }
        }
    }

    override fun getItemCount(): Int {
        if (detailPosition >= 0) {
            return boorus.size + 1
        }
        return boorus.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        when (detailPosition >= 0) {
            true -> {
                when {
                    position <= detailPosition -> {
                        val booru = boorus[position]
                        holder as BooruViewHolder
                        val text = booru.name[0].toString()
                        holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                        holder.name.text = booru.name
                        holder.itemView.setOnClickListener {
                            detailPosition = if (position == detailPosition) {
                                -1
                            } else {
                                position
                            }
                            notifyDataSetChanged()
                        }
                    }
                    position == detailPosition + 1 -> {
                        val booru = boorus[detailPosition]
                        holder as BooruDetailViewHolder
                        holder.scheme.text = booru.scheme
                        holder.domain.text = booru.host
                        holder.hashSalt.text = booru.hash_salt
                        holder.edit.setOnClickListener {
                            detailPosition = -1
                            booruChangeListener?.onBooruEdit(booru)
                        }
                        holder.delete.setOnClickListener {
                            detailPosition = -1
                            booruChangeListener?.onBooruDelete(booru)
                        }
                    }
                    else -> {
                        val booru =  boorus[position -1]
                        holder as BooruViewHolder
                        val text = booru.name[0].toString()
                        holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                        holder.name.text = booru.name
                        holder.itemView.setOnClickListener {
                            detailPosition = position-1
                            notifyDataSetChanged()
                        }
                    }
                }
            }
            else -> {
                val booru = boorus[position]
                holder as BooruViewHolder
                val text = booru.name[0].toString()
                holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                holder.name.text = booru.name
                holder.itemView.setOnClickListener {
                    detailPosition = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (detailPosition >= 0 && position == detailPosition + 1) {
            return VIEW_TYPE_DETAIL
        }
        return VIEW_TYPE_NORMAL
    }

    inner class BooruViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val expand: ImageView = itemView.findViewById(R.id.expand)
    }

    inner class BooruDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheme: TextView = itemView.findViewById(R.id.scheme)
        val domain: TextView = itemView.findViewById(R.id.domain)
        val hashSalt: TextView = itemView.findViewById(R.id.hash_salt)
        val edit: Button = itemView.findViewById(R.id.btn_edit)
        val delete: Button = itemView.findViewById(R.id.btn_delete)
    }

}