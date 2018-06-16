package im.mash.moebooru.glide

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import im.mash.moebooru.App.Companion.glideHeaders
import java.net.URL

class MoeGlideUrl : GlideUrl {

    constructor(url: String?) : this(url, glideHeaders)

    constructor(url: String?, headers: Headers?): super(url, headers)

    constructor(url: URL?): this(url, glideHeaders)

    constructor(url: URL?, headers: Headers?): super(url, headers)

}