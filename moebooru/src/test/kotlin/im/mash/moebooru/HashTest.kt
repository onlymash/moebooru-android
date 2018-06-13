package im.mash.moebooru

import im.mash.moebooru.util.HashUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class HashTest {

    @Test
    fun sha1() {
        assertEquals("f013d66c7f6817d08b7eb2a93e6d0440c1f3e7f8", HashUtil.sha1("sha1"))
    }

    @Test
    fun md5() {
        assertEquals("", HashUtil.md5("md5"))
    }
}