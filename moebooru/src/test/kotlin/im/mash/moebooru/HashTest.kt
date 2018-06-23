package im.mash.moebooru

import im.mash.moebooru.util.HashUtil
import kotlin.test.Test
import kotlin.test.assertEquals

class HashTest {

    @Test
    fun sha1() {
        assertEquals("AC4CB8DB202C4E046136B56DDCA5BF02851DB5E5", HashUtil.sha1("So-I-Heard-You-Like-Mupkids-?--your-password--"))
    }

}