package im.mash.moebooru.main.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.signature.MediaStoreSignature
import im.mash.moebooru.R
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.widget.FixedImageView
import im.mash.moebooru.glide.GlideRequest
import im.mash.moebooru.glide.GlideRequests
import im.mash.moebooru.util.statusBarHeight
import im.mash.moebooru.util.toolbarHeight

class GalleryAdapter(private val context: Context, private val spanCount: Int, glideRequests: GlideRequests) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(),
        ListPreloader.PreloadModelProvider<MediaStoreData>,
        ListPreloader.PreloadSizeProvider<MediaStoreData> {

    private var media: MutableList<MediaStoreData> = mutableListOf()
    private val requestBuilder: GlideRequest<Drawable> = glideRequests
            .asDrawable()
            .centerCrop()
            .format(DecodeFormat.PREFER_ARGB_8888)

    private var actualDimensions: IntArray? = null

    private val padding = context.resources.getDimension(R.dimen.item_padding).toInt()

    init {
        setHasStableIds(true)
    }

    override fun getPreloadItems(position: Int): MutableList<MediaStoreData> {
        return mutableListOf(media[position])
    }

    override fun getPreloadRequestBuilder(item: MediaStoreData): RequestBuilder<*>? {
        val signature = MediaStoreSignature(item.mimeType, item.dateModified, item.orientation)
        return requestBuilder
                .clone()
                .signature(signature)
                .load(item.uri)
    }

    override fun getPreloadSize(item: MediaStoreData, adapterPosition: Int, perItemPosition: Int): IntArray? {
        return actualDimensions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_local_galley_item, parent, false)
        if (actualDimensions == null) {
            itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (actualDimensions == null) {
                        actualDimensions = intArrayOf(itemView.width, itemView.height)
                    }
                    itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            })
        }
        return GalleryViewHolder(itemView)
    }

    override fun getItemId(position: Int): Long {
        return media[position].rowId
    }

    override fun getItemCount(): Int {
        return media.size
    }

    fun updateData(media: MutableList<MediaStoreData>) {
        this.media = media
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        if (position in 0 until spanCount) {
            holder.itemView.setPadding(padding, padding + context.toolbarHeight + statusBarHeight, padding, padding)
        } else {
            holder.itemView.setPadding(padding, padding, padding, padding)
        }
        val current = media[position]
        val signature = MediaStoreSignature(current.mimeType, current.dateModified, current.orientation)
        requestBuilder
                .clone()
                .signature(signature)
                .load(current.uri)
                .into(holder.img)
    }

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<FixedImageView>(R.id.gallery_img)!!
    }
}