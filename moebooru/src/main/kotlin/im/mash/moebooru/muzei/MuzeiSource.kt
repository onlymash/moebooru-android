package im.mash.moebooru.muzei

data class MuzeiSource(
        var id: Int = 0,
        var currentPage: Int = 0,
        var noMoreResult: Boolean = false,
        var siteId: Int = 0,
        var tags: Set<String>? = null
) {

    override fun toString(): String {
        if (this.tags == null || this.tags!!.isEmpty()) {
            return "#"
        }
        val stringBuilder = StringBuilder()
        for (str in this.tags!!) {
            stringBuilder.append("#")
            stringBuilder.append(str)
            stringBuilder.append("   ")
        }
        return stringBuilder.toString()
    }
}
