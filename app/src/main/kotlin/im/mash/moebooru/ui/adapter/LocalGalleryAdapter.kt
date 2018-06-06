package im.mash.moebooru.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.*
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.signature.MediaStoreSignature
import im.mash.moebooru.glide.GlideRequests
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideRequest
import im.mash.moebooru.model.MediaStoreData
import im.mash.moebooru.ui.widget.FixedImageView

class LocalGalleryAdapter(context: Context, private var items: MutableList<MediaStoreData>,
                          glideRequests: GlideRequests) : RecyclerView.Adapter<LocalGalleryAdapter.LocalGalleryViewHolder>(),
        ListPreloader.PreloadModelProvider<MediaStoreData>, ListPreloader.PreloadSizeProvider<MediaStoreData> {

//    private val screenWidth: Int
    private val requestBuilder: GlideRequest<Drawable> = glideRequests.asDrawable().fitCenter()
    private var actualDimensions: IntArray? = null

    init {
        setHasStableIds(true)
//        screenWidth = context.screenWidth
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalGalleryViewHolder {
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
        return LocalGalleryViewHolder(itemView)
    }

    fun updateData(items: MutableList<MediaStoreData>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return items[position].rowId
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LocalGalleryViewHolder, position: Int) {
        holder.img
        val current = items.get(position)
        val signature = MediaStoreSignature(current.mimeType, current.dateModified, current.orientation)
        requestBuilder
                .clone()
                .signature(signature)
                .load(current.uri)
                .into(holder.img)
    }


    class LocalGalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<FixedImageView>(R.id.gallery_img)!!
    }

    override fun getPreloadItems(position: Int): MutableList<MediaStoreData> {
        return mutableListOf(items[position])
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
}