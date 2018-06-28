package im.mash.moebooru.muzei

import im.mash.moebooru.common.data.local.entity.PostSearch

data class MuzeiArt( var id: Int,
                     var isRotated: Boolean,
                     var page: Int,
                     var post: PostSearch,
                     var sourceId: Int)