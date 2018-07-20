package im.mash.moebooru.glide

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import im.mash.moebooru.App.Companion.getGlideHeaders
import java.net.URL

class MoeGlideUrl : GlideUrl {

    constructor(url: String?) : this(url, getGlideHeaders())

    constructor(url: String?, headers: Headers?): super(url, headers)

    constructor(url: URL?): this(url, getGlideHeaders())

    constructor(url: URL?, headers: Headers?): super(url, headers)

}